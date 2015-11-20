package tool.analyzers;

import jadd.ADD;
import jadd.JADD;

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
import expressionsolver.ExpressionSolver;

/**
 * Orchestrator of family-based analyses.
 *
 * @author thiago
 */
public class FamilyBasedAnalyzer {
    private static final Logger LOGGER = Logger.getLogger(FamilyBasedAnalyzer.class.getName());

    private ADD featureModel;
    private ExpressionSolver expressionSolver;

    private FamilyBasedPreAnalysisStrategy familyBasedPreAnalysisStrategy;

    private ITimeCollector timeCollector;

    public FamilyBasedAnalyzer(JADD jadd,
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
     * Evaluates the family-based reliability function of an RDG node.
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
        timeCollector.startTimer(CollectibleTimers.FAMILY_BASED_TIME);
        ADD reliability = evaluateReliability(expression, dependencies);
        ADD result = featureModel.times(reliability);
        LOGGER.info("Formula evaluation ok...");
        timeCollector.stopTimer(CollectibleTimers.FAMILY_BASED_TIME);

        return new ADDReliabilityResults(result);
    }

    private ADD evaluateReliability(String reliabilityExpression, List<RDGNode> dependencies) {
        Map<String, ADD> childrenReliabilities = new HashMap<String, ADD>();
        for (RDGNode child: dependencies) {
            ADD presenceCondition = expressionSolver.encodeFormula(child.getPresenceCondition());
            childrenReliabilities.put(child.getId(),
                                      presenceCondition);
        }

        ADD reliability = expressionSolver.solveExpressionAsFunction(reliabilityExpression,
                                                                     childrenReliabilities);
        return reliability;
    }

}
