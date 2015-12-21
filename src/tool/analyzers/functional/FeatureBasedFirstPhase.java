package tool.analyzers.functional;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import paramwrapper.ParametricModelChecker;
import tool.Analyzer;
import tool.RDGNode;
import tool.analyzers.buildingblocks.Component;
import tool.stats.IFormulaCollector;
import fdtmc.FDTMC;

public class FeatureBasedFirstPhase {
    private static final Logger LOGGER = Logger.getLogger(FeatureBasedFirstPhase.class.getName());

    ParametricModelChecker modelChecker;
    private IFormulaCollector formulaCollector;

    public FeatureBasedFirstPhase(ParametricModelChecker modelChecker,
                                  IFormulaCollector formulaCollector) {
        this.modelChecker = modelChecker;
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
    public List<Component<String>> getReliabilityExpressions(List<RDGNode> nodes) {
        // Expressions can be calculated concurrently...
        Map<RDGNode, String> expressionsByNode = nodes.parallelStream()
            .collect(Collectors.toMap(Function.identity(),
                                      this::getReliabilityExpression));

        // ... but then we need to recover ordering information
        // so that we can format the response accordingly.
        List<Component<String>> reliabilityExpressions = new LinkedList<Component<String>>();
        for (RDGNode node: nodes) {
            reliabilityExpressions.add(mapNodeToExpression(node, expressionsByNode));
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
        String reliabilityExpression = modelChecker.getReliability(model);

        formulaCollector.collectFormula(node, reliabilityExpression);
        LOGGER.fine("Reliability expression for "+ node.getId() + " -> " + reliabilityExpression);
        return reliabilityExpression;
    }

    private Component<String> mapNodeToExpression(RDGNode node, Map<RDGNode, String> expressionsByNode) {
        Collection<Component<String>> dependencies = node.getDependencies().stream()
                .map(n -> mapNodeToExpression(n, expressionsByNode))
                .collect(Collectors.toSet());
        return new Component<String>(node.getId(),
                                     node.getPresenceCondition(),
                                     expressionsByNode.get(node),
                                     dependencies);
    }

}
