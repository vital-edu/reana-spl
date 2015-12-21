package tool.analyzers.functional;

import jadd.JADD;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import paramwrapper.ParametricModelChecker;
import tool.CyclicRdgException;
import tool.RDGNode;
import tool.UnknownFeatureException;
import tool.analyzers.IReliabilityAnalysisResults;
import tool.analyzers.MapBasedReliabilityResults;
import tool.analyzers.buildingblocks.Component;
import tool.analyzers.buildingblocks.DerivationFunction;
import tool.analyzers.buildingblocks.IfOperator;
import tool.stats.CollectibleTimers;
import tool.stats.IFormulaCollector;
import tool.stats.ITimeCollector;
import expressionsolver.ExpressionSolver;

public class FeatureProductBasedAnalyzer {

    private ExpressionSolver expressionSolver;
    private FeatureBasedFirstPhase firstPhase;

    /**
     * Sigma
     */
    private DerivationFunction<Boolean, String, Double> solve;

    private ITimeCollector timeCollector;

    public FeatureProductBasedAnalyzer(JADD jadd,
                                       ParametricModelChecker modelChecker,
                                       ITimeCollector timeCollector,
                                       IFormulaCollector formulaCollector) {
        this.expressionSolver = new ExpressionSolver(jadd);

        this.timeCollector = timeCollector;

        this.firstPhase = new FeatureBasedFirstPhase(modelChecker,
                                                     formulaCollector);


        solve = DerivationFunction.abstractDerivation(new IfOperator<Double>(),
                                                      expressionSolver::solveExpression,
                                                      1.0);
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

        timeCollector.startTimer(CollectibleTimers.FEATURE_BASED_TIME);
        // Alpha_v
        List<Component<String>> expressions = firstPhase.getReliabilityExpressions(dependencies);
        timeCollector.stopTimer(CollectibleTimers.FEATURE_BASED_TIME);

        timeCollector.startTimer(CollectibleTimers.PRODUCT_BASED_TIME);

        MapBasedReliabilityResults results = new MapBasedReliabilityResults();
        configurations.parallelStream().forEach(configuration -> {
            Double result = evaluateReliabilityForSingleConfiguration(node,
                                                                      configuration,
                                                                      expressions);
            results.putResult(configuration, result);
        });

        timeCollector.stopTimer(CollectibleTimers.PRODUCT_BASED_TIME);
        return results;
    }

    private Double evaluateReliabilityForSingleConfiguration(RDGNode node, List<String> configuration, List<Component<String>> expressions) {
        return solveFromMany(expressions, configuration);
    }

    // TODO Candidate!
    private Double solveFromMany(List<Component<String>> dependencies, List<String> configuration) {
        Map<String, Double> reliabilities = new HashMap<String, Double>();
        return dependencies.stream()
                .map(c -> deriveSingle(c, configuration, solve, reliabilities))
                .reduce((first, actual) -> actual)
                .get();
    }

    // TODO Candidate!
    private Double deriveSingle(Component<String> component, List<String> configuration, DerivationFunction<Boolean, String, Double> derive, Map<String, Double> derivedModels) {
        boolean presence = isPresent(component, configuration);
        Double derived = derive.apply(presence, component.getAsset(), derivedModels);
        derivedModels.put(component.getId(), derived);
        return derived;
    }

    // TODO Candidate!
    private boolean isPresent(Component<String> component, List<String> configuration) {
        return PresenceConditions.isPresent(component.getPresenceCondition(),
                                            configuration,
                                            expressionSolver);
    }

}
