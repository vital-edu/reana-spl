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
        // Lambda folding
        FDTMC rootModel = deriveFromMany(dependencies, configuration);
        // Alpha
        String reliabilityExpression = modelChecker.getReliability(rootModel);
        formulaCollector.collectFormula(node, reliabilityExpression);
        // Sigma
        return expressionSolver.solveExpression(reliabilityExpression);
    }

    /**
     * LAMBDA
     */
    private DerivationFunction<Boolean, FDTMC, FDTMC> derive = DerivationFunction.abstractDerivation(new IfOperator<FDTMC>(), FDTMC::inline, trivialFdtmc());

    private FDTMC deriveFromMany(List<RDGNode> dependencies, List<String> configuration) {
        Map<String, FDTMC> derivedModels = new HashMap<String, FDTMC>();
        return dependencies.stream()
                .map(n -> deriveNode(n, configuration, derive, derivedModels))
                .reduce((first, fdtmc) -> fdtmc)
                .get();
    }

    // TODO Candidate!
    private FDTMC deriveNode(RDGNode node, List<String> configuration, DerivationFunction<Boolean, FDTMC, FDTMC> derive, Map<String, FDTMC> derivedModels) {
        boolean presence = isPresent(node, configuration);
        FDTMC derived = derive.apply(presence, node.getFDTMC(), derivedModels);
        derivedModels.put(node.getId(), derived);
        return derived;
    }

    // Candidate!
    private boolean isPresent(RDGNode node, List<String> configuration) {
        return PresenceConditions.isPresent(node.getPresenceCondition(),
                                            configuration,
                                            expressionSolver);
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
