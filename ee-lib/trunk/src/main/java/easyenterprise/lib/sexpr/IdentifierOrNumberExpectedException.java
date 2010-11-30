package easyenterprise.lib.sexpr;

public class IdentifierOrNumberExpectedException extends SExprParseException {

	private static final long serialVersionUID = 1L;

	public IdentifierOrNumberExpectedException(int pos) {
		super("Identifier or number expected", pos);
	}

}
