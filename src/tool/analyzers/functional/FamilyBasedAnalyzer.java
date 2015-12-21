package tool.analyzers.functional;

import jadd.ADD;
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
import tool.UnknownFeatureException;
import tool.analyzers.ADDReliabilityResults;
import tool.analyzers.IReliabilityAnalysisResults;
import tool.stats.CollectibleTimers;
import tool.stats.IFormulaCollector;
import tool.stats.ITimeCollector;
import expressionsolver.Expression;
import expressionsolver.ExpressionSolver;

public class FamilyBasedAnalyzer {
    private static final Logger LOGGER = Logger.getLogger(FamilyBasedAnalyzer.class.getName());

    private ADD featureModel;
    private ExpressionSolver expressionSolver;
    ParametricModelChecker modelChecker;

    private FamilyBasedFirstPhase firstPhase;
    private FamilyBasedHelper helper;

    private ITimeCollector timeCollector;
    private IFormulaCollector formulaCollector;

    public FamilyBasedAnalyzer(JADD jadd,
                               ADD featureModel,
                               ParametricModelChecker modelChecker,
                               ITimeCollector timeCollector,
                               IFormulaCollector formulaCollector) {
        this.expressionSolver = new ExpressionSolver(jadd);
        this.featureModel = featureModel;
        this.modelChecker = modelChecker;

        this.timeCollector = timeCollector;
        this.formulaCollector = formulaCollector;

        this.firstPhase = new FamilyBasedFirstPhase(modelChecker);
        this.helper = new FamilyBasedHelper(expressionSolver);
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

        timeCollector.startTimer(CollectibleTimers.FAMILY_BASED_TIME);

        // Lambda_v + alpha_v
        String expression = firstPhase.getReliabilityExpression(dependencies);
        formulaCollector.collectFormula(node, expression);

        // Lift
        Expression<ADD> liftedExpression = helper.lift(expression);

        Function<RDGNode, String> getPC = RDGNode::getPresenceCondition;
        Map<String, ADD> values = dependencies.stream()
                .collect(Collectors.toMap(RDGNode::getId,
                                          getPC.andThen(expressionSolver::encodeFormula)));

        // Sigma'_v
        ADD reliability = liftedExpression.solve(values);
        ADD result = featureModel.times(reliability);

        timeCollector.stopTimer(CollectibleTimers.FAMILY_BASED_TIME);
        LOGGER.info("Formula evaluation ok...");

        return new ADDReliabilityResults(result);
    }

}
