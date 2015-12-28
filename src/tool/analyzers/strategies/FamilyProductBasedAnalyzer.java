package tool.analyzers.strategies;

import jadd.JADD;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import paramwrapper.ParametricModelChecker;
import tool.CyclicRdgException;
import tool.RDGNode;
import tool.analyzers.IReliabilityAnalysisResults;
import tool.analyzers.MapBasedReliabilityResults;
import tool.analyzers.buildingblocks.PresenceConditions;
import tool.analyzers.buildingblocks.ProductIterationHelper;
import tool.analyzers.buildingblocks.ProductIterationHelper.CONCURRENCY;
import tool.stats.CollectibleTimers;
import tool.stats.IFormulaCollector;
import tool.stats.ITimeCollector;
import expressionsolver.Expression;
import expressionsolver.ExpressionSolver;

/**
 * Orchestrator of family-product-based analyses.
 */
public class FamilyProductBasedAnalyzer {
    private static final Logger LOGGER = Logger.getLogger(FamilyProductBasedAnalyzer.class.getName());

    private ExpressionSolver expressionSolver;

    private FamilyBasedFirstPhase firstPhase;

    private ITimeCollector timeCollector;
    private IFormulaCollector formulaCollector;

    public FamilyProductBasedAnalyzer(JADD jadd,
                               ParametricModelChecker modelChecker,
                               ITimeCollector timeCollector,
                               IFormulaCollector formulaCollector) {
        this.expressionSolver = new ExpressionSolver(jadd);

        this.firstPhase = new FamilyBasedFirstPhase(modelChecker);

        this.timeCollector = timeCollector;
        this.formulaCollector = formulaCollector;
    }

    /**
     * Evaluates the family-product-based reliability function of an RDG node.
     *
     * @param node RDG node whose reliability is to be evaluated.
     * @return
     * @throws CyclicRdgException
     */
    public IReliabilityAnalysisResults evaluateReliability(RDGNode node, Collection<Collection<String>> configurations) throws CyclicRdgException {
        List<RDGNode> dependencies = node.getDependenciesTransitiveClosure();

        timeCollector.startTimer(CollectibleTimers.FAMILY_BASED_TIME);

        // Lambda_v + alpha_v
        String expression = firstPhase.getReliabilityExpression(dependencies);
        formulaCollector.collectFormula(node, expression);

        Expression<Double> parsedExpression = expressionSolver.parseExpression(expression);

        List<String> presenceConditions = dependencies.stream()
                .map(RDGNode::getPresenceCondition)
                .collect(Collectors.toList());
        Map<String, String> pcEquivalence = PresenceConditions.toEquivalenceClasses(presenceConditions);
        Map<String, String> eqClassToPC = pcEquivalence.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getValue(),
                                          e -> e.getKey(),
                                          (a, b) -> a));

        timeCollector.stopTimer(CollectibleTimers.FAMILY_BASED_TIME);
        timeCollector.startTimer(CollectibleTimers.PRODUCT_BASED_TIME);

        Map<Collection<String>, Double> results = ProductIterationHelper.evaluate(configuration -> evaluateSingle(parsedExpression,
                                                                                                                  configuration,
                                                                                                                  eqClassToPC),
                                                                                  configurations,
                                                                                  CONCURRENCY.SEQUENTIAL);

        timeCollector.stopTimer(CollectibleTimers.PRODUCT_BASED_TIME);
        LOGGER.info("Formulae evaluation ok...");
        return new MapBasedReliabilityResults(results);
    }

    private Double evaluateSingle(Expression<Double> expression, Collection<String> configuration, Map<String, String> eqClassToPC) {
        Function<Map.Entry<String, String>, Boolean> isPresent = e -> PresenceConditions.isPresent(e.getValue(),
                                                                                                   configuration,
                                                                                                   expressionSolver);
        Map<String, Double> values = eqClassToPC.entrySet().stream()
            .collect(Collectors.toMap(e -> e.getKey(),
                                      isPresent.andThen(present -> present ? 1.0 : 0.0)));

        return expression.solve(values);

    }

}
