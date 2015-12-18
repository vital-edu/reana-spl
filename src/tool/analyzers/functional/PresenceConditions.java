package tool.analyzers.functional;

import jadd.ADD;
import jadd.UnrecognizedVariableException;

import java.util.List;

import tool.UnknownFeatureException;
import expressionsolver.ExpressionSolver;

public class PresenceConditions {

    public static boolean isPresent(String presenceCondition, List<String> configuration, ExpressionSolver expressionSolver) {
        ADD encodedPresenceCondition = expressionSolver.encodeFormula(presenceCondition);
        Double presenceValue;
        try {
            presenceValue = encodedPresenceCondition.eval(configuration.toArray(new String[configuration.size()]));
        } catch (UnrecognizedVariableException e) {
            throw new UnknownFeatureException(e.getVariableName());
        }
        return presenceValue.compareTo(1.0) == 0;
    }

}
