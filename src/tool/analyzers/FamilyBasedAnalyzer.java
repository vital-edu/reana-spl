package tool.analyzers;

import jadd.ADD;
import jadd.JADD;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
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

/**
 * Orchestrator of family-based analyses.
 *
 * @author thiago
 */
public class FamilyBasedAnalyzer {
    private static final Logger LOGGER = Logger.getLogger(FamilyBasedAnalyzer.class.getName());

    private ADD featureModel;
    private ExpressionSolver expressionSolver;
    ParametricModelChecker modelChecker;

    private ITimeCollector timeCollector;

    public FamilyBasedAnalyzer(JADD jadd,
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
     * Evaluates the family-based reliability function of an RDG node.
     *
     * @param node RDG node whose reliability is to be evaluated.
     * @return
     * @throws CyclicRdgException
     */
    public IReliabilityAnalysisResults evaluateReliability(RDGNode node, Collection<List<String>> configurations) throws CyclicRdgException, UnknownFeatureException {
        List<RDGNode> dependencies = node.getDependenciesTransitiveClosure();

        timeCollector.startTimer(CollectibleTimers.FAMILY_BASED_TIME);
        Map<RDGNode, FDTMC> simulators = derive150Models(dependencies);
        FDTMC simulator = simulators.get(node);
        LOGGER.fine(simulator.toString());
        String expression = getReliabilityExpression(simulator);
        ADD reliability = evaluateReliability(expression, dependencies);
        ADD result = featureModel.times(reliability);
        timeCollector.stopTimer(CollectibleTimers.FAMILY_BASED_TIME);

        return new ADDReliabilityResults(result);
    }

    private String getReliabilityExpression(FDTMC model) throws UnknownFeatureException {
        return modelChecker.getReliability(model);
    }

    private Map<RDGNode, FDTMC> derive150Models(List<RDGNode> dependencies) {
        Map<RDGNode, FDTMC> derivedModels = new HashMap<RDGNode, FDTMC>();
        for (RDGNode node: dependencies) {
            FDTMC model = node.getFDTMC();
            FDTMC derivedModel = inline150Model(model, derivedModels);
            derivedModels.put(node, derivedModel);
        }
        return derivedModels;
    }

    private FDTMC inline150Model(FDTMC model, final Map<RDGNode, FDTMC> derivedModels) {
        Map<String, FDTMC> indexedModels = derivedModels.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getId(),
                                          entry -> entry.getValue()));
        return model.inlineWithVariability(indexedModels);
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
