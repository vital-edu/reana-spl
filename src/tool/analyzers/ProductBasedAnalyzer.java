package tool.analyzers;

import jadd.ADD;
import jadd.JADD;
import jadd.UnrecognizedVariableException;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import paramwrapper.ParametricModelChecker;
import tool.CyclicRdgException;
import tool.RDGNode;
import tool.UnknownFeatureException;
import tool.stats.CollectibleTimers;
import tool.stats.IFormulaCollector;
import tool.stats.ITimeCollector;
import expressionsolver.ExpressionSolver;
import fdtmc.FDTMC;
import fdtmc.State;

/**
 * Orchestrator of product-based analyses.
 *
 * @author thiago
 */
public class ProductBasedAnalyzer {

    private ADD featureModel;
    private ExpressionSolver expressionSolver;
    ParametricModelChecker modelChecker;

    private ITimeCollector timeCollector;

    public ProductBasedAnalyzer(JADD jadd,
                                ADD featureModel,
                                ParametricModelChecker modelChecker,
                                ITimeCollector timeCollector,
                                IFormulaCollector formulaCollector) {
        this.expressionSolver = new ExpressionSolver(jadd);
        this.featureModel = featureModel;
        this.modelChecker = modelChecker;

        this.timeCollector = timeCollector;
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
        if (!featureModel.isValidConfiguration(configuration)) {
            return 0.0;
        }
        Map<RDGNode, FDTMC> derivedModels = deriveModels(dependencies, configuration);
        FDTMC rootModel = derivedModels.get(node);
        String reliabilityExpression = modelChecker.getReliability(rootModel);

        return expressionSolver.solveExpression(reliabilityExpression);
    }

    private Map<RDGNode, FDTMC> deriveModels(List<RDGNode> dependencies, List<String> configuration) {
        Map<RDGNode, FDTMC> derivedModels = new HashMap<RDGNode, FDTMC>();
        for (RDGNode node: dependencies) {
            ADD presenceCondition = expressionSolver.encodeFormula(node.getPresenceCondition());
            Double presenceValue;
            try {
                presenceValue = presenceCondition.eval(configuration.toArray(new String[configuration.size()]));
            } catch (UnrecognizedVariableException e) {
                throw new UnknownFeatureException(e.getVariableName());
            }
            boolean present = presenceValue.compareTo(1.0) == 0;

            FDTMC derivedModel = present ? deriveModel(node, derivedModels) : trivialFdtmc(node);
            derivedModels.put(node, derivedModel);
        }
        return derivedModels;
    }

    private FDTMC deriveModel(RDGNode node, final Map<RDGNode, FDTMC> derivedModels) {
        FDTMC currentModel = node.getFDTMC();
        Map<String, FDTMC> indexedModels = derivedModels.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getId(),
                                          entry -> entry.getValue()));
        return currentModel.inline(indexedModels);
    }

    private FDTMC trivialFdtmc(RDGNode node) {
        FDTMC trivial = new FDTMC();
        trivial.setVariableName(node.getId());

        State initial = trivial.createInitialState();
        State success = trivial.createSuccessState();
        trivial.createTransition(initial, success, "", "1.0");
        return trivial;
    }

}
