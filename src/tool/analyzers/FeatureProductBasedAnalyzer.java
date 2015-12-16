package tool.analyzers;

import jadd.ADD;
import jadd.JADD;
import jadd.UnrecognizedVariableException;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import paramwrapper.ParametricModelChecker;
import tool.CyclicRdgException;
import tool.RDGNode;
import tool.UnknownFeatureException;
import tool.analyzers.strategies.FeatureBasedPreAnalysisStrategy;
import tool.stats.CollectibleTimers;
import tool.stats.IFormulaCollector;
import tool.stats.ITimeCollector;
import expressionsolver.ExpressionSolver;

/**
 * Orchestrator of feature-product-based analyses.
 *
 * @author thiago
 */
public class FeatureProductBasedAnalyzer {

    private ADD featureModel;
    private ExpressionSolver expressionSolver;
    private FeatureBasedPreAnalysisStrategy featureBasedPreAnalysisStrategy;

    private ITimeCollector timeCollector;

    public FeatureProductBasedAnalyzer(JADD jadd,
                                       ADD featureModel,
                                       ParametricModelChecker modelChecker,
                                       ITimeCollector timeCollector,
                                       IFormulaCollector formulaCollector) {
        this.expressionSolver = new ExpressionSolver(jadd);
        this.featureModel = featureModel;

        this.timeCollector = timeCollector;

        this.featureBasedPreAnalysisStrategy = new FeatureBasedPreAnalysisStrategy(modelChecker,
                                                                                   timeCollector,
                                                                                   formulaCollector);
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
    public IReliabilityAnalysisResults evaluateReliability(RDGNode node, Collection<List<String>> configurations) throws CyclicRdgException, UnknownFeatureException {
        List<RDGNode> dependencies = node.getDependenciesTransitiveClosure();
        LinkedHashMap<RDGNode, String> expressionsByNode = featureBasedPreAnalysisStrategy.getReliabilityExpressions(dependencies);

        MapBasedReliabilityResults results = new MapBasedReliabilityResults();
        timeCollector.startTimer(CollectibleTimers.PRODUCT_BASED_TIME);

        configurations.parallelStream().forEach(configuration -> {
            Double result = evaluateReliabilityForSingleConfiguration(node,
                                                                      configuration,
                                                                      expressionsByNode);
            results.putResult(configuration, result);
        });

        timeCollector.stopTimer(CollectibleTimers.PRODUCT_BASED_TIME);
        return results;
    }

    /**
     * @param node
     * @param configuration
     * @param expressionsByNode
     * @return
     * @throws UnknownFeatureException
     */
    private Double evaluateReliabilityForSingleConfiguration(RDGNode node, List<String> configuration, LinkedHashMap<RDGNode, String> expressionsByNode) throws UnknownFeatureException {
        if (!featureModel.isValidConfiguration(configuration)) {
            return 0.0;
        }
        return evaluateReliabilities(node,
                                     expressionsByNode,
                                     configuration);
    }

    /**
     * Evaluates the reliability values of the RDG nodes according to the
     * correspondent pre-computed reliability expressions.
     *
     * This function implements the product-based part of a feature-product-based analysis.
     *
     * @param targetNode RDG node whose reliability is to be evaluated.
     * @return
     * @throws UnknownFeatureException
     */
    private Double evaluateReliabilities(RDGNode targetNode, LinkedHashMap<RDGNode, String> expressionsByNode, List<String> configuration) throws UnknownFeatureException {
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
