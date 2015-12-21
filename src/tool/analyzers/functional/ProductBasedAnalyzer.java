package tool.analyzers.functional;

import jadd.JADD;

import java.util.Collection;
import java.util.List;

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
import fdtmc.FDTMC;
import fdtmc.State;

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
    public IReliabilityAnalysisResults evaluateReliability(RDGNode node, Collection<List<String>> configurations) throws CyclicRdgException, UnknownFeatureException {
        List<RDGNode> dependencies = node.getDependenciesTransitiveClosure();

        MapBasedReliabilityResults results = new MapBasedReliabilityResults();
        timeCollector.startTimer(CollectibleTimers.PRODUCT_BASED_TIME);

        configurations.parallelStream().forEach(configuration -> {
            Double result = evaluateReliabilityForSingleConfiguration(node,
                                                                      configuration,
                                                                      dependencies);
            results.putResult(configuration, result);
        });

        timeCollector.stopTimer(CollectibleTimers.PRODUCT_BASED_TIME);
        return results;
    }

    private Double evaluateReliabilityForSingleConfiguration(RDGNode node, List<String> configuration, List<RDGNode> dependencies) throws UnknownFeatureException {
        List<Component<FDTMC>> models = RDGNode.toComponentList(dependencies);
        // Lambda folding
        FDTMC rootModel = deriveFromMany(models, configuration);
        // Alpha
        String reliabilityExpression = modelChecker.getReliability(rootModel);
        formulaCollector.collectFormula(node, reliabilityExpression);
        // Sigma
        return expressionSolver.solveExpression(reliabilityExpression);
    }

    private FDTMC deriveFromMany(List<Component<FDTMC>> dependencies, List<String> configuration) {
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
