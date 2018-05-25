package tool.analyzers.buildingblocks;

import jadd.ADD;
import expressionsolver.Expression;
import expressionsolver.ExpressionSolver;
import expressionsolver.ExpressionSolverParser;

/**
 * Helper for lifting of expressions in *-family-*-based strategies.
 */
public class FamilyBasedHelper {

    private ExpressionSolver expressionSolver;
    private ExpressionSolverParser expressionSolverParser;

    public FamilyBasedHelper(ExpressionSolver expressionSolver) {
        this.expressionSolver = expressionSolver;
    }

    public Expression<ADD> lift(String expression) {
        return expressionSolverParser.parseExpressionForFunctions(expression);
    }

    public Component<Expression<ADD>> lift(Component<String> expression) {
        return expression.fmap(this::lift);
    }

}
