package easyenterprise.lib.sexpr;

public class IdentifierOrNumberExpectedException extends SExprParseException {

	private static final long serialVersionUID = 1L;

	public IdentifierOrNumberExpectedException(String expression, int pos) {
		super(expression, pos, pos, "Identifier or number expected");
	}

}
