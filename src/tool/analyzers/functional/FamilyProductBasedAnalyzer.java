package tool.analyzers.functional;

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
import tool.stats.CollectibleTimers;
import tool.stats.IFormulaCollector;
import tool.stats.ITimeCollector;
import expressionsolver.Expression;
import expressionsolver.ExpressionSolver;

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
    public IReliabilityAnalysisResults evaluateReliability(RDGNode node, Collection<List<String>> configurations) throws CyclicRdgException {
        List<RDGNode> dependencies = node.getDependenciesTransitiveClosure();

        timeCollector.startTimer(CollectibleTimers.FAMILY_BASED_TIME);

        // Lambda_v + alpha_v
        String expression = firstPhase.getReliabilityExpression(dependencies);
        formulaCollector.collectFormula(node, expression);

        Expression<Double> parsedExpression = expressionSolver.parseExpression(expression);

        timeCollector.stopTimer(CollectibleTimers.FAMILY_BASED_TIME);
        timeCollector.startTimer(CollectibleTimers.PRODUCT_BASED_TIME);

        MapBasedReliabilityResults results = new MapBasedReliabilityResults();
        configurations.stream().forEach(configuration -> {
            Double result = evaluateReliabilityForSingleConfiguration(parsedExpression,
                                                                      configuration,
                                                                      dependencies);
            results.putResult(configuration, result);
        });
        timeCollector.stopTimer(CollectibleTimers.PRODUCT_BASED_TIME);
        LOGGER.info("Formulae evaluation ok...");

        return results;
    }

    private Double evaluateReliabilityForSingleConfiguration(Expression<Double> expression, List<String> configuration, List<RDGNode> dependencies) {
        Function<RDGNode, Boolean> isPresent = node -> isPresent(node, configuration);
        Map<String, Double> values = dependencies.stream()
            .collect(Collectors.toMap(RDGNode::getId,
                                      isPresent.andThen(present -> present ? 1.0 : 0.0)));

        return expression.solve(values);

    }

    // Candidate!
    private boolean isPresent(RDGNode node, List<String> configuration) {
        return PresenceConditions.isPresent(node.getPresenceCondition(),
                                            configuration,
                                            expressionSolver);
    }

}
