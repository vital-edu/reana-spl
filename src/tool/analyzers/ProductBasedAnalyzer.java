package tool.analyzers;

import jadd.ADD;
import jadd.JADD;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import paramwrapper.ParametricModelChecker;
import tool.CyclicRdgException;
import tool.RDGNode;
import tool.UnknownFeatureException;
import tool.stats.CollectibleTimers;
import tool.stats.IFormulaCollector;
import tool.stats.ITimeCollector;
import expressionsolver.ExpressionSolver;
import fdtmc.FDTMC;

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
            derivedModels.put(node,
                              deriveModel(node,
                                          derivedModels));
        }
        return derivedModels;
    }

    private FDTMC deriveModel(RDGNode node, Map<RDGNode, FDTMC> derivedModels) {
        return null;
    }

}
