package tool.analyzers.strategies;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import paramwrapper.ParametricModelChecker;
import tool.Analyzer;
import tool.RDGNode;
import tool.UnknownFeatureException;
import tool.stats.CollectibleTimers;
import tool.stats.IFormulaCollector;
import tool.stats.ITimeCollector;
import fdtmc.FDTMC;

public class FamilyBasedPreAnalysisStrategy {
    private static final Logger LOGGER = Logger.getLogger(FamilyBasedPreAnalysisStrategy.class.getName());

    ParametricModelChecker modelChecker;
    private ITimeCollector timeCollector;
    private IFormulaCollector formulaCollector;

    public FamilyBasedPreAnalysisStrategy(ParametricModelChecker modelChecker,
                                           ITimeCollector timeCollector,
                                           IFormulaCollector formulaCollector) {
        this.modelChecker = modelChecker;

        this.timeCollector = timeCollector;
        this.formulaCollector = formulaCollector;
    }

    /**
     * Computes the reliability expression for the 150% model of the given RDG node,
     * using the given order of nodes for sequential composition.
     *
     * The returned expression has variables encoding the presence/absence of
     * RDG nodes (components).
     *
     * This function implements the family-based first phase of analyses.
     *
     * @see {@link Analyzer.getReliabilityExpression}
     * @param node
     * @return
     */
    public String getReliabilityExpression(RDGNode node, List<RDGNode> dependencies) {
        timeCollector.startTimer(CollectibleTimers.FAMILY_BASED_TIME);

        Map<RDGNode, FDTMC> simulators = derive150Models(dependencies);
        FDTMC simulator = simulators.get(node);
        String expression = getReliabilityExpression(simulator);
        LOGGER.fine(expression);

        timeCollector.stopTimer(CollectibleTimers.FAMILY_BASED_TIME);
        formulaCollector.collectFormula(node, expression);

        return expression;
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

}
