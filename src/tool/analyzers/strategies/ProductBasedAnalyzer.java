package tool.analyzers.strategies;

import jadd.JADD;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import paramwrapper.ParametricModelChecker;
import tool.CyclicRdgException;
import tool.RDGNode;
import tool.UnknownFeatureException;
import tool.analyzers.IReliabilityAnalysisResults;
import tool.analyzers.MapBasedReliabilityResults;
import tool.analyzers.buildingblocks.Component;
import tool.analyzers.buildingblocks.DerivationFunction;
import tool.analyzers.buildingblocks.IfOperator;
import tool.analyzers.buildingblocks.PresenceConditions;
import tool.analyzers.buildingblocks.ProductIterationHelper;
import tool.analyzers.buildingblocks.ProductIterationHelper.CONCURRENCY;
import tool.stats.CollectibleTimers;
import tool.stats.IFormulaCollector;
import tool.stats.ITimeCollector;
import expressionsolver.ExpressionSolver;
import fdtmc.FDTMC;
import fdtmc.State;

/**
 * Orchestrator of product-based analyses.
 */
public class ProductBasedAnalyzer {

    private ExpressionSolver expressionSolver;
    ParametricModelChecker modelChecker;
    /**
     * LAMBDA
     */
    private DerivationFunction<Boolean, FDTMC, FDTMC> derive;


    private ITimeCollector timeCollector;
    private IFormulaCollector formulaCollector;

    public ProductBasedAnalyzer(JADD jadd,
                                ParametricModelChecker modelChecker,
                                ITimeCollector timeCollector,
                                IFormulaCollector formulaCollector) {
        this.expressionSolver = new ExpressionSolver(jadd);
        this.modelChecker = modelChecker;

        this.timeCollector = timeCollector;
        this.formulaCollector = formulaCollector;

        derive = DerivationFunction.abstractDerivation(new IfOperator<FDTMC>(),
                                                       FDTMC::inline,
                                                       trivialFdtmc());
    }

    /**
     * Evaluates the product-based reliability values of an RDG node.
     *
     * @param node RDG node whose reliability is to be evaluated.
     * @return
     * @throws CyclicRdgException
     */
    public IReliabilityAnalysisResults evaluateReliability(RDGNode node, Stream<Collection<String>> configurations) throws CyclicRdgException, UnknownFeatureException {
        List<RDGNode> dependencies = node.getDependenciesTransitiveClosure();

        timeCollector.startTimer(CollectibleTimers.MODEL_CHECKING_TIME);

        Map<Collection<String>, Double> results = ProductIterationHelper.evaluate(configuration -> evaluateSingle(node,
                                                                                                                  configuration,
                                                                                                                  dependencies),
                                                                                  configurations,
                                                                                  CONCURRENCY.PARALLEL);

        timeCollector.stopTimer(CollectibleTimers.MODEL_CHECKING_TIME);
        return new MapBasedReliabilityResults(results);
    }

    private Double evaluateSingle(RDGNode node, Collection<String> configuration, List<RDGNode> dependencies) throws UnknownFeatureException {
        List<Component<FDTMC>> models = RDGNode.toComponentList(dependencies);
        // Lambda folding
        FDTMC rootModel = deriveFromMany(models, configuration);
        // Alpha
        String reliabilityExpression = modelChecker.getReliability(rootModel);
        formulaCollector.collectFormula(node, reliabilityExpression);
        // Sigma
        return expressionSolver.solveExpression(reliabilityExpression);
    }

    private FDTMC deriveFromMany(List<Component<FDTMC>> dependencies, Collection<String> configuration) {
        return Component.deriveFromMany(dependencies,
                                        derive,
                                        c -> PresenceConditions.isPresent(c.getPresenceCondition(),
                                                                          configuration,
                                                                          expressionSolver));
    }

    private FDTMC trivialFdtmc() {
        FDTMC trivial = new FDTMC();
        trivial.setVariableName("t");

        State initial = trivial.createInitialState();
        State success = trivial.createSuccessState();
        trivial.createTransition(initial, success, "", "1.0");

        return trivial;
    }

}
