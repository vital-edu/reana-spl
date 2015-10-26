/**
 *
 */
package tool;

import jadd.ADD;
import jadd.JADD;
import jadd.UnrecognizedVariableException;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.logging.Logger;

import modeling.DiagramAPI;

import org.w3c.dom.DOMException;

import paramwrapper.ParamWrapper;
import paramwrapper.ParametricModelChecker;
import parsing.exceptions.InvalidNodeClassException;
import parsing.exceptions.InvalidNodeType;
import parsing.exceptions.InvalidNumberOfOperandsException;
import parsing.exceptions.InvalidTagException;
import parsing.exceptions.UnsupportedFragmentTypeException;
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
     * Abstracts UML to RDG transformation.
     *
     * @param umlModels
     * @return
     * @throws InvalidTagException
     * @throws UnsupportedFragmentTypeException
     * @throws DOMException
     * @throws InvalidNodeType
     * @throws InvalidNodeClassException
     * @throws InvalidNumberOfOperandsException
     */
    public RDGNode model(File umlModels) throws UnsupportedFragmentTypeException, InvalidTagException, InvalidNumberOfOperandsException, InvalidNodeClassException, InvalidNodeType {
        timeCollector.startParsingTimer();

        DiagramAPI modeler = new DiagramAPI(umlModels);
        RDGNode result = modeler.transform();

        timeCollector.stopParsingTimer();
        return result;
    }

    /**
     * Evaluates the feature-family-based reliability function of an RDG node, based
     * on the reliabilities of the nodes on which it depends.
     *
     * A reliability function is a boolean function from the set of features
     * to Real values, where the reliability of any invalid configuration is 0.
     *
     * @param node RDG node whose reliability is to be evaluated.
     * @return
     * @throws CyclicRdgException
     */
    public ADD evaluateFeatureFamilyBasedReliability(RDGNode node) throws CyclicRdgException {
        List<RDGNode> dependencies = node.getDependenciesTransitiveClosure();
        LinkedHashMap<RDGNode, String> expressionsByNode = getReliabilityExpressions(dependencies);

        timeCollector.startFamilyBasedTimer();
        Map<RDGNode, ADD> reliabilities = evaluateReliabilities(expressionsByNode);
        timeCollector.stopFamilyBasedTimer();
        ADD reliability = reliabilities.get(node);

        timeCollector.startFamilyBasedTimer();
        // After evaluating the expression, constant terms alter the {0,1} nature
        // of the reliability ADD. Thus, we must multiply the result by the
        // {0,1} representation of the feature model in order to retain 0 as the
        // value for invalid configurations.
        ADD result = featureModel.times(reliability);
        timeCollector.stopFamilyBasedTimer();
        return result;
    }

    /**
     * Evaluates the feature-product-based reliability value of an RDG node, based
     * on the reliabilities of the nodes on which it depends.
     *
     * @param node RDG node whose reliability is to be evaluated.
     * @return
     * @throws CyclicRdgException
     * @throws UnrecognizedVariableException
     */
    public Double evaluateFeatureProductBasedReliability(RDGNode node, List<String> configuration) throws CyclicRdgException, UnrecognizedVariableException {
        double validity = featureModel.eval(configuration.toArray(new String[configuration.size()]));
        if (Double.doubleToRawLongBits(validity) == 0) {
            return 0.0;
        }
        List<RDGNode> dependencies = node.getDependenciesTransitiveClosure();
        LinkedHashMap<RDGNode, String> expressionsByNode = getReliabilityExpressions(dependencies);

        // TODO Use parameterized time collector for getting x-based timers.
        timeCollector.startFamilyBasedTimer();
        Map<RDGNode, Double> reliabilities = evaluateReliabilities(expressionsByNode, configuration);
        timeCollector.stopFamilyBasedTimer();
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
            timeCollector.startFeatureBasedTimer();
            String reliabilityExpression = getReliabilityExpression(node);
            timeCollector.stopFeatureBasedTimer();

            reliabilityExpressions.put(node, reliabilityExpression);
            formulaCollector.collectFormula(reliabilityExpression);
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
     * @throws UnrecognizedVariableException
     */
    private Map<RDGNode, Double> evaluateReliabilities(LinkedHashMap<RDGNode, String> expressionsByNode, List<String> configuration) throws UnrecognizedVariableException {
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
     * @throws UnrecognizedVariableException
     */
    private Double evaluateNodeReliability(RDGNode node, String reliabilityExpression, List<String> configuration, Map<RDGNode, Double> reliabilityCache) throws UnrecognizedVariableException {
        Map<String, Double> childrenReliabilities = new HashMap<String, Double>();
        for (RDGNode child: node.getDependencies()) {
            Double childReliability = reliabilityCache.get(child);
            ADD presenceCondition = expressionSolver.encodeFormula(child.getPresenceCondition());
            Double presenceValue = presenceCondition.eval(configuration.toArray(new String[configuration.size()]));
            boolean present = presenceValue.compareTo(1.0) == 0;

            childrenReliabilities.put(child.getId(),
                                      present ? childReliability : 1);
        }

        Double reliability = expressionSolver.solveExpression(reliabilityExpression,
                                                           childrenReliabilities);
        return reliability;
    }

    public void printStats(PrintStream out) {
        out.println("-----------------------------");
        out.println("Stats:");
        out.println("------");
        timeCollector.printStats(out);
        formulaCollector.printStats(out);
    }

}
