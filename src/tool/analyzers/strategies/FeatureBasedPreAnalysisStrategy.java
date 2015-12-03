package tool.analyzers.strategies;

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
        // Expressions can be calculated concurrently...
        Map<RDGNode, String> expressionsByNode = nodes.parallelStream()
            .collect(Collectors.toMap(Function.identity(),
                     this::getReliabilityExpression));

        // ... but then we need to recover ordering information
        // so that we can format the response accordingly.
        LinkedHashMap<RDGNode, String> reliabilityExpressions = new LinkedHashMap<RDGNode, String>();
        for (RDGNode node: nodes) {
            reliabilityExpressions.put(node, expressionsByNode.get(node));
        }
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
