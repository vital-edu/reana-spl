package tool.analyzers.strategies;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import paramwrapper.ParametricModelChecker;
import tool.Analyzer;
import tool.RDGNode;
import tool.stats.CollectibleTimers;
import tool.stats.IFormulaCollector;
import tool.stats.ITimeCollector;
import fdtmc.FDTMC;

public class FeatureBasedPreAnalysisStrategy {
    private static final Logger LOGGER = Logger.getLogger(FeatureBasedPreAnalysisStrategy.class.getName());

    ParametricModelChecker modelChecker;
    private ITimeCollector timeCollector;
    private IFormulaCollector formulaCollector;

    public FeatureBasedPreAnalysisStrategy(ParametricModelChecker modelChecker,
                                           ITimeCollector timeCollector,
                                           IFormulaCollector formulaCollector) {
        this.modelChecker = modelChecker;

        this.timeCollector = timeCollector;
        this.formulaCollector = formulaCollector;
    }

    /**
     * Computes the reliability expression for the model of the given RDG nodes,
     * returning them in a map which is conveniently sorted in the same order as
     * the input list.
     *
     * This function implements the feature-based part of the analysis.
     *
     * @see {@link Analyzer.getReliabilityExpression}
     * @param node
     * @return
     */
    public LinkedHashMap<RDGNode, String> getReliabilityExpressions(List<RDGNode> nodes) {
        Map<RDGNode, String> expressionsByNode = nodes.parallelStream()
            .collect(Collectors.toMap(Function.identity(),
                     this::getReliabilityExpression));

        // Now we need to recover ordering information...
        Map<RDGNode, Integer> order = new HashMap<RDGNode, Integer>();
        for (int i = 0; i < nodes.size(); i++) {
            order.put(nodes.get(i), i);
        }
        // ... so that we can format the response accordingly.
        LinkedHashMap<RDGNode, String> reliabilityExpressions = new LinkedHashMap<RDGNode, String>();
        expressionsByNode.entrySet().stream()
            .sorted((entry1, entry2) -> {
                return order.get(entry1.getKey()) - order.get(entry2.getKey());
            })
            .forEach(entry -> {
                reliabilityExpressions.put(entry.getKey(),
                                           entry.getValue());
            });
        return reliabilityExpressions;
    }

    /**
     * Computes the reliability expression for the model of a given RDG node.
     *
     * @param node
     * @return an algebraic expression on the variables present in the node's model.
     */
    private String getReliabilityExpression(RDGNode node) {
        timeCollector.startTimer(CollectibleTimers.FEATURE_BASED_TIME);
        FDTMC model = node.getFDTMC();
        String reliabilityExpression = modelChecker.getReliability(model);
        timeCollector.stopTimer(CollectibleTimers.FEATURE_BASED_TIME);

        formulaCollector.collectFormula(node, reliabilityExpression);
        LOGGER.fine("Reliability expression for "+ node.getId() + " -> " + reliabilityExpression);
        return reliabilityExpression;
    }

}
