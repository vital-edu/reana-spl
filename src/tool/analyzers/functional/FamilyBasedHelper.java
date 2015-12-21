package tool.analyzers.functional;

import jadd.ADD;
import tool.analyzers.buildingblocks.Component;
import expressionsolver.Expression;
import expressionsolver.ExpressionSolver;

public class FamilyBasedHelper {

    private ExpressionSolver expressionSolver;

    public FamilyBasedHelper(ExpressionSolver expressionSolver) {
        this.expressionSolver = expressionSolver;
    }

    public Expression<ADD> lift(String expression) {
        return expressionSolver.parseExpressionForFunctions(expression);
    }

    public Component<Expression<ADD>> lift(Component<String> expression) {
        return expression.fmap(this::lift);
    }

}
