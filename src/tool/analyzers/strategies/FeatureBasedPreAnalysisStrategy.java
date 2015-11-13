package tool.analyzers.strategies;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;

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
        LinkedHashMap<RDGNode, String> reliabilityExpressions = new LinkedHashMap<RDGNode, String>();
        for (RDGNode node: nodes) {
            timeCollector.startTimer(CollectibleTimers.FEATURE_BASED_TIME);
            String reliabilityExpression = getReliabilityExpression(node);
            timeCollector.stopTimer(CollectibleTimers.FEATURE_BASED_TIME);

            reliabilityExpressions.put(node, reliabilityExpression);
            formulaCollector.collectFormula(node, reliabilityExpression);
            LOGGER.fine("Reliability expression for "+ node.getId() + " -> " + reliabilityExpression);
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
        FDTMC model = node.getFDTMC();
        return modelChecker.getReliability(model);
    }

}
