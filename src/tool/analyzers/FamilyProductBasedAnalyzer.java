package tool.analyzers;

import jadd.ADD;
import jadd.JADD;
import jadd.UnrecognizedVariableException;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import paramwrapper.ParametricModelChecker;
import tool.CyclicRdgException;
import tool.RDGNode;
import tool.UnknownFeatureException;
import tool.analyzers.strategies.FamilyBasedPreAnalysisStrategy;
import tool.stats.CollectibleTimers;
import tool.stats.IFormulaCollector;
import tool.stats.ITimeCollector;
import expressionsolver.Expression;
import expressionsolver.ExpressionSolver;

/**
 * Orchestrator of family-product-based analyses.
 *
 * @author thiago
 */
public class FamilyProductBasedAnalyzer {
    private static final Logger LOGGER = Logger.getLogger(FamilyProductBasedAnalyzer.class.getName());

    private ADD featureModel;
    private ExpressionSolver expressionSolver;

    private FamilyBasedPreAnalysisStrategy familyBasedPreAnalysisStrategy;

    private ITimeCollector timeCollector;

    public FamilyProductBasedAnalyzer(JADD jadd,
                               ADD featureModel,
                               ParametricModelChecker modelChecker,
                               ITimeCollector timeCollector,
                               IFormulaCollector formulaCollector) {
        this.expressionSolver = new ExpressionSolver(jadd);
        this.featureModel = featureModel;

        familyBasedPreAnalysisStrategy = new FamilyBasedPreAnalysisStrategy(modelChecker,
                                                                            timeCollector,
                                                                            formulaCollector);

        this.timeCollector = timeCollector;
    }

    /**
     * Evaluates the family-product-based reliability function of an RDG node.
     *
     * @param node RDG node whose reliability is to be evaluated.
     * @return
     * @throws CyclicRdgException
     */
    public IReliabilityAnalysisResults evaluateReliability(RDGNode node, Collection<List<String>> configurations) throws CyclicRdgException, UnknownFeatureException {
        List<RDGNode> dependencies = node.getDependenciesTransitiveClosure();
        String expression = familyBasedPreAnalysisStrategy.getReliabilityExpression(node,
                                                                                    dependencies);
        LOGGER.info("Parametric model-checking ok...");

        Expression<Double> parsedExpression = expressionSolver.parseExpression(expression);

        MapBasedReliabilityResults results = new MapBasedReliabilityResults();
        timeCollector.startTimer(CollectibleTimers.PRODUCT_BASED_TIME);

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

    private Double evaluateReliabilityForSingleConfiguration(Expression<Double> parsedExpression, List<String> configuration, List<RDGNode> dependencies) throws UnknownFeatureException {
        if (!featureModel.isValidConfiguration(configuration)) {
            return 0.0;
        }

        Map<String, Double> nodesPresence = new HashMap<String, Double>();
        for (RDGNode node: dependencies) {
            ADD presenceCondition = expressionSolver.encodeFormula(node.getPresenceCondition());
            Double presenceValue;
            try {
                presenceValue = presenceCondition.eval(configuration.toArray(new String[configuration.size()]));
            } catch (UnrecognizedVariableException e) {
                throw new UnknownFeatureException(e.getVariableName());
            }

            nodesPresence.put(node.getId(),
                              presenceValue);
        }

        return parsedExpression.solve(nodesPresence);
    }

}
