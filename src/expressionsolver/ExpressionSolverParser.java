package expressionsolver;

import java.util.logging.Logger;

import org.nfunk.jep.JEP;
import org.nfunk.jep.type.DoubleNumberFactory;

import expressionsolver.functions.ADDAdd;
import expressionsolver.functions.ADDDivide;
import expressionsolver.functions.ADDMultiply;
import expressionsolver.functions.ADDPower;
import expressionsolver.functions.ADDSubtract;
import expressionsolver.functions.LogicalAnd;
import expressionsolver.functions.LogicalNot;
import expressionsolver.functions.LogicalOr;
import expressionsolver.functions.UnaryMinus;
import jadd.ADD;
import jadd.JADD;

public class ExpressionSolverParser {
	private static JADD jadd;
	private static final Logger LOGGER = Logger.getLogger(ExpressionSolver.class.getName());

	public ExpressionSolverParser() {
	}

	public static JADD getJadd() {
		return jadd;
	}

	public void setJadd(JADD jadd) {
		this.jadd = jadd;
	}
	
    /**
     * Lower level alternative for {@link solveExpressionAsFunction(String)}.
     *
     * @see {@link parseExpression(String)}
     *
     * @param expression
     * @return A handle to the parsed expression or {@code null} if there
     *      is a parsing error.
     */
	
    public static Expression<ADD> parseExpressionForFunctions(String expression) {
        JEP parser = makeADDParser(getJadd());
        parser.parseExpression(expression);
        if (parser.hasError()) {
            LOGGER.warning("Parser error: " + parser.getErrorInfo());
            return null;
        }
        return new Expression<ADD>(parser, ADD.class);
    }
	

    /**
     * @param jadd
     */
	public static JEP makeADDParser(JADD jadd) {
        JEP parser = new JEP(false, true, true, new ADDNumberFactory(jadd));
        parser.addFunction("\"+\"", new ADDAdd());
        parser.addFunction("\"-\":2", new ADDSubtract());
        parser.addFunction("\"-\":1", new UnaryMinus());
        parser.addFunction("\"*\"", new ADDMultiply());
        parser.addFunction("\"^\"", new ADDPower(jadd));
        parser.addFunction("\"/\"", new ADDDivide());

        parser.addFunction("\"&&\"", new LogicalAnd());
        parser.addFunction("\"||\"", new LogicalOr());
        parser.addFunction("\"!\"", new LogicalNot());
        return parser;
    }

    /**
     * Makes a standard floating-point-based parser.
     */
    public JEP makeFloatingPointParser() {
        JEP parser = new JEP(false, true, true, new DoubleNumberFactory());
        parser.setAllowUndeclared(true);
        return parser;
    }
}