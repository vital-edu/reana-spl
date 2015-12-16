package tool.analyzers;

import jadd.ADD;
import jadd.JADD;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import paramwrapper.ParametricModelChecker;
import tool.CyclicRdgException;
import tool.RDGNode;
import tool.analyzers.strategies.FeatureBasedPreAnalysisStrategy;
import tool.stats.CollectibleTimers;
import tool.stats.IFormulaCollector;
import tool.stats.ITimeCollector;
import expressionsolver.ExpressionSolver;

/**
 * Orchestrator of feature-family-based analyses.
 *
 * @author thiago
 */
public class FeatureFamilyBasedAnalyzer {

    private ADD featureModel;
    private ExpressionSolver expressionSolver;
    private JADD jadd;
    private IPruningStrategy pruningStrategy;
    private FeatureBasedPreAnalysisStrategy featureBasedPreAnalysisStrategy;

    private ITimeCollector timeCollector;

    public FeatureFamilyBasedAnalyzer(JADD jadd,
                                      ADD featureModel,
                                      ParametricModelChecker modelChecker,
                                      ITimeCollector timeCollector,
                                      IFormulaCollector formulaCollector) {
        this.jadd = jadd;
        this.expressionSolver = new ExpressionSolver(jadd);
        this.featureModel = featureModel;

        this.timeCollector = timeCollector;
        this.pruningStrategy = new NoPruningStrategy();

        this.featureBasedPreAnalysisStrategy = new FeatureBasedPreAnalysisStrategy(modelChecker,
                                                                                   timeCollector,
                                                                                   formulaCollector);
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
    public IReliabilityAnalysisResults evaluateReliability(RDGNode node, String dotOutput) throws CyclicRdgException {
        List<RDGNode> dependencies = node.getDependenciesTransitiveClosure();
        LinkedHashMap<RDGNode, String> expressionsByNode = featureBasedPreAnalysisStrategy.getReliabilityExpressions(dependencies);

        timeCollector.startTimer(CollectibleTimers.FAMILY_BASED_TIME);
        ADD result = evaluateReliabilities(node, expressionsByNode);

        // After evaluating the expression, constant terms alter the {0,1} nature
        // of the reliability ADD. Thus, we must multiply the result by the
        // {0,1} representation of the feature model in order to retain 0 as the
        // value for invalid configurations.
        result = featureModel.times(result);
        timeCollector.stopTimer(CollectibleTimers.FAMILY_BASED_TIME);

        if (dotOutput != null) {
            generateDotFile(result, dotOutput);
        }

        return new ADDReliabilityResults(result);
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
     * Evaluates the reliability functions of the RDG nodes according to the
     * correspondent pre-computed reliability expressions.
     * A reliability function is a boolean function from the set of features
     * to Real values.
     *
     * This function implements the family-based part of a feature-family-based analysis.
     *
     * @param targetNode RDG node whose reliability is to be evaluated.
     * @return
     */
    private ADD evaluateReliabilities(RDGNode targetNode, LinkedHashMap<RDGNode, String> expressionsByNode) {
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
        }

        return reliabilities.get(targetNode);
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

}
