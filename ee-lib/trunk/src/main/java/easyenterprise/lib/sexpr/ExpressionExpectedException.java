package easyenterprise.lib.sexpr;

public class ExpressionExpectedException extends SExprParseException {

	private static final long serialVersionUID = 1L;

	public ExpressionExpectedException(String expression, int pos) {
		super(expression, pos, pos, "Expression expected");
	}

}
