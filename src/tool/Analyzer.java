/**
 *
 */
package tool;

import jadd.ADD;
import jadd.JADD;
import jadd.UnrecognizedVariableException;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.logging.Logger;

import paramwrapper.ParamWrapper;
import paramwrapper.ParametricModelChecker;
import tool.stats.CollectibleTimers;
import tool.stats.IFormulaCollector;
import tool.stats.ITimeCollector;
import tool.stats.NoopFormulaCollector;
import tool.stats.NoopTimeCollector;
import expressionsolver.ExpressionSolver;
import fdtmc.FDTMC;

/**
 * Implements the orchestration of analysis tasks.
 *
 * This is the Façade to the domain model.
 * Its responsibility is establishing **what** needs to be done.
 *
 * @author thiago
 */
public class Analyzer {
    private static final Logger LOGGER = Logger.getLogger(ParamWrapper.class.getName());

    private ADD featureModel;
    private ParametricModelChecker modelChecker;
    private ExpressionSolver expressionSolver;
    private JADD jadd;
    private IPruningStrategy pruningStrategy;

    private ITimeCollector timeCollector;
    private IFormulaCollector formulaCollector;

    /**
     * Creates an Analyzer which will follow the logical rules
     * encoded in the provided feature model file.
     *
     * @param featureModel String containing a CNF view of the Feature Model
     *          expressed using Java logical operators.
     * @throws IOException if there is a problem reading the file.
     */
    public Analyzer(String featureModel, String paramPath, ITimeCollector timeCollector, IFormulaCollector formulaCollector) {
        this(new JADD(), featureModel, paramPath);

        this.timeCollector = timeCollector;
        this.formulaCollector = formulaCollector;
    }

    /**
     * Package-private constructor for testability.
     * It allows injection of ADD processor an feature model expression.
     * @param jadd
     * @param featureModel
     */
    Analyzer(JADD jadd, String featureModel, String paramPath) {
        this.modelChecker = new ParamWrapper(paramPath);
        this.jadd = jadd;
        this.expressionSolver = new ExpressionSolver(jadd);
        this.featureModel = expressionSolver.encodeFormula(featureModel);

        this.timeCollector = new NoopTimeCollector();
        this.formulaCollector = new NoopFormulaCollector();
        this.pruningStrategy = new NoPruningStrategy();
    }

    /**
     * Returns the set of all valid configurations according to the feature model.
     * @return
     */
    public Collection<List<String>> getValidConfigurations() {
        return featureModel.getExpandedConfigurations();
    }

    /**
     * Sets the pruning strategy to be used for preventing calculation
     * of reliability values for invalid configurations.
     *
     * If none is set, the default behavior is to multiply the reliability
     * mappings by the feature model's 0,1-ADD (so that valid configurations
     * yield the same reliability, but invalid ones yield 0).
     *
     * @param pruningStrategy the pruningStrategy to set
     */
    public void setPruningStrategy(IPruningStrategy pruningStrategy) {
        this.pruningStrategy = pruningStrategy;
    }

    /**
     * Evaluates the feature-family-based reliability function of an RDG node, based
     * on the reliabilities of the nodes on which it depends.
     *
     * A reliability function is a boolean function from the set of features
     * to Real values, where the reliability of any invalid configuration is 0.
     *
     * @param node RDG node whose reliability is to be evaluated.
     * @param dotOutput path at where to dump the resulting ADD as a dot file.
     * @return
     * @throws CyclicRdgException
     */
    public IReliabilityAnalysisResults evaluateFeatureFamilyBasedReliability(RDGNode node, String dotOutput) throws CyclicRdgException {
        List<RDGNode> dependencies = node.getDependenciesTransitiveClosure();
        LinkedHashMap<RDGNode, String> expressionsByNode = getReliabilityExpressions(dependencies);

        timeCollector.startTimer(CollectibleTimers.FAMILY_BASED_TIME);
        Map<RDGNode, ADD> reliabilities = evaluateReliabilities(expressionsByNode);
        timeCollector.stopTimer(CollectibleTimers.FAMILY_BASED_TIME);
        ADD reliability = reliabilities.get(node);

        timeCollector.startTimer(CollectibleTimers.FAMILY_BASED_TIME);
        // After evaluating the expression, constant terms alter the {0,1} nature
        // of the reliability ADD. Thus, we must multiply the result by the
        // {0,1} representation of the feature model in order to retain 0 as the
        // value for invalid configurations.
        ADD result = featureModel.times(reliability);
        timeCollector.stopTimer(CollectibleTimers.FAMILY_BASED_TIME);

        if (dotOutput != null) {
            generateDotFile(result, dotOutput);
        }

        return new ADDReliabilityResults(result);
    }
    /**
     * Evaluates the feature-family-based reliability function of an RDG node, based
     * on the reliabilities of the nodes on which it depends, but does not dump the
     * resulting ADD.
     *
     * @see {@link Analyzer.evaluateFeatureFamilyBasedReliability(RDGNode, String)}
     */
    public IReliabilityAnalysisResults evaluateFeatureFamilyBasedReliability(RDGNode node) throws CyclicRdgException {
        return evaluateFeatureFamilyBasedReliability(node, null);
    }

    /**
     * Evaluates the feature-product-based reliability value of an RDG node, based
     * on the reliabilities of the nodes on which it depends.
     *
     * @param node RDG node whose reliability is to be evaluated.
     * @return
     * @throws CyclicRdgException
     * @throws UnknownFeatureException
     */
    public IReliabilityAnalysisResults evaluateFeatureProductBasedReliability(RDGNode node, Collection<List<String>> configurations) throws CyclicRdgException, UnknownFeatureException {
        List<RDGNode> dependencies = node.getDependenciesTransitiveClosure();
        LinkedHashMap<RDGNode, String> expressionsByNode = getReliabilityExpressions(dependencies);

        MapBasedReliabilityResults results = new MapBasedReliabilityResults();
        for (List<String> configuration: configurations) {
            Double result = evaluateFeatureProductBasedReliabilityForSingleConfiguration(node,
                                                                                         configuration,
                                                                                         expressionsByNode);
            results.putResult(configuration, result);
        }
        return results;
    }

    /**
     * @param node
     * @param configuration
     * @param expressionsByNode
     * @return
     * @throws UnknownFeatureException
     */
    private Double evaluateFeatureProductBasedReliabilityForSingleConfiguration(RDGNode node, List<String> configuration, LinkedHashMap<RDGNode, String> expressionsByNode) throws UnknownFeatureException {
        double validity;
        try {
            validity = featureModel.eval(configuration.toArray(new String[configuration.size()]));
        } catch (UnrecognizedVariableException e) {
            throw new UnknownFeatureException(e.getVariableName());
        }
        if (Double.doubleToRawLongBits(validity) == 0) {
            return 0.0;
        }

        // TODO Use parameterized time collector for getting x-based timers.
        timeCollector.startTimer(CollectibleTimers.PRODUCT_BASED_TIME);
        Map<RDGNode, Double> reliabilities = evaluateReliabilities(expressionsByNode, configuration);
        timeCollector.stopTimer(CollectibleTimers.PRODUCT_BASED_TIME);
        return reliabilities.get(node);
    }

    /**
     * Dumps the computed family reliability function to the output file
     * in the specified path.
     *
     * @param familyReliability Reliability function computed by a call to the
     *          {@link #evaluateFeatureFamilyBasedReliability(RDGNode)} method.
     * @param outputFile Path to the .dot file to be generated.
     */
    public void generateDotFile(ADD familyReliability, String outputFile) {
        jadd.dumpDot("Family Reliability", familyReliability, outputFile);
    }

    /**
     * Computes the reliability expression for the model of a given RDG node.
     *
     * @param node
     * @return an algebraic expression on the variables present in the node's model.
     */
    private String getReliabilityExpression(RDGNode node) {
        FDTMC model = node.getFDTMC();
        return modelChecker.getReliability(model);
    }

    /**
     * Computes the reliability expression for the model of the given RDG nodes,
     * returning them in a map which is conveniently sorted in the same order as
     * the input list.
     *
     * This function implements the feature-based part of the analysis.
     *
     * @see {@link Analyzer.getReliabilityExpression}
     * @param node
     * @return
     */
    private LinkedHashMap<RDGNode, String> getReliabilityExpressions(List<RDGNode> nodes) {
        LinkedHashMap<RDGNode, String> reliabilityExpressions = new LinkedHashMap<RDGNode, String>();
        for (RDGNode node: nodes) {
            timeCollector.startTimer(CollectibleTimers.FEATURE_BASED_TIME);
            String reliabilityExpression = getReliabilityExpression(node);
            timeCollector.stopTimer(CollectibleTimers.FEATURE_BASED_TIME);

            reliabilityExpressions.put(node, reliabilityExpression);
            formulaCollector.collectFormula(node, reliabilityExpression);
            LOGGER.fine("Reliability expression for "+ node.getId() + " -> " + reliabilityExpression);
        }
        return reliabilityExpressions;
    }

    /**
     * Evaluates the reliability functions of the RDG nodes according to the
     * correspondent pre-computed reliability expressions.
     * A reliability function is a boolean function from the set of features
     * to Real values.
     *
     * This function implements the family-based part of a feature-family-based analysis.
     *
     * @param node RDG node whose reliability is to be evaluated.
     * @return
     */
    private Map<RDGNode, ADD> evaluateReliabilities(LinkedHashMap<RDGNode, String> expressionsByNode) {
        Map<RDGNode, ADD> reliabilities = new HashMap<RDGNode, ADD>();

        for (SortedMap.Entry<RDGNode, String> entry: expressionsByNode.entrySet()) {
            RDGNode node = entry.getKey();
            String reliabilityExpression = entry.getValue();

            // This must work without checking, since we expect the expressionsByNode
            // map to be topologically sorted, i.e., dependencies will have already
            // been evaluated.
            ADD reliability = evaluateNodeReliabilityFunction(node,
                                                              reliabilityExpression,
                                                              reliabilities);

            reliabilities.put(node, reliability);
//            jadd.dumpDot(reliabilityExpression,
//                         reliability,
//                         "result-"+node.getId()+".dot");
        }

        return reliabilities;
    }


    /**
     * Evaluates the reliability values of the RDG nodes according to the
     * correspondent pre-computed reliability expressions.
     *
     * This function implements the product-based part of a feature-product-based analysis.
     *
     * @param node RDG node whose reliability is to be evaluated.
     * @return
     * @throws UnknownFeatureException
     */
    private Map<RDGNode, Double> evaluateReliabilities(LinkedHashMap<RDGNode, String> expressionsByNode, List<String> configuration) throws UnknownFeatureException {
        Map<RDGNode, Double> reliabilities = new HashMap<RDGNode, Double>();

        for (SortedMap.Entry<RDGNode, String> entry: expressionsByNode.entrySet()) {
            RDGNode node = entry.getKey();
            String reliabilityExpression = entry.getValue();

            // This must work without checking, since we expect the expressionsByNode
            // map to be topologically sorted, i.e., dependencies will have already
            // been evaluated.
            Double reliability = evaluateNodeReliability(node,
                                                         reliabilityExpression,
                                                         configuration,
                                                         reliabilities);

            reliabilities.put(node, reliability);
        }

        return reliabilities;
    }

    /**
     * Evaluates the reliability function of a single RDG node according to the
     * correspondent pre-computed reliability expression and the given cache of
     * previously evaluated reliabilities.
     *
     * Assumes all dependencies have already been evaluated.
     *
     * @param node
     * @param reliabilityExpression
     * @param reliabilityCache
     * @return
     */
    private ADD evaluateNodeReliabilityFunction(RDGNode node, String reliabilityExpression, Map<RDGNode, ADD> reliabilityCache) {
        Map<String, ADD> childrenReliabilities = new HashMap<String, ADD>();
        for (RDGNode child: node.getDependencies()) {
            ADD childReliability = reliabilityCache.get(child);
            ADD presenceCondition = expressionSolver.encodeFormula(child.getPresenceCondition());
            ADD phi = presenceCondition.ifThenElse(childReliability, 1);
            childrenReliabilities.put(child.getId(),
                                      phi);
        }

        ADD reliability = expressionSolver.solveExpressionAsFunction(reliabilityExpression,
                                                                     childrenReliabilities);
        reliability = pruningStrategy.pruneInvalidConfigurations(node,
                                                                 reliability,
                                                                 featureModel);
        return reliability;
    }

    /**
     * Evaluates the reliability function of a single RDG node according to the
     * correspondent pre-computed reliability expression and the given cache of
     * previously evaluated reliabilities.
     *
     * Assumes all dependencies have already been evaluated.
     *
     * @param node
     * @param reliabilityExpression
     * @param reliabilityCache
     * @return
     * @throws UnknownFeatureException
     */
    private Double evaluateNodeReliability(RDGNode node, String reliabilityExpression, List<String> configuration, Map<RDGNode, Double> reliabilityCache) throws UnknownFeatureException {
        Map<String, Double> childrenReliabilities = new HashMap<String, Double>();
        for (RDGNode child: node.getDependencies()) {
            Double childReliability = reliabilityCache.get(child);
            ADD presenceCondition = expressionSolver.encodeFormula(child.getPresenceCondition());
            Double presenceValue;
            try {
                presenceValue = presenceCondition.eval(configuration.toArray(new String[configuration.size()]));
            } catch (UnrecognizedVariableException e) {
                throw new UnknownFeatureException(e.getVariableName());
            }
            boolean present = presenceValue.compareTo(1.0) == 0;

            childrenReliabilities.put(child.getId(),
                                      present ? childReliability : 1);
        }

        Double reliability = expressionSolver.solveExpression(reliabilityExpression,
                                                           childrenReliabilities);
        return reliability;
    }

}
