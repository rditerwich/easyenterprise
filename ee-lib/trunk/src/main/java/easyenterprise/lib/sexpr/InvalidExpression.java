package easyenterprise.lib.sexpr;

public class InvalidExpression extends SExprParseException {

	private static final long serialVersionUID = 1L;

	public InvalidExpression(String expression, int startPos, int endPos) {
		super(expression, startPos, endPos, "Invalid expression: " + expression.substring(startPos, endPos));
	}

}
