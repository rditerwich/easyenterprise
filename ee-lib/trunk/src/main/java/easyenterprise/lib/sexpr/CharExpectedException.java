package easyenterprise.lib.sexpr;

public class CharExpectedException extends SExprParseException {

	private static final long serialVersionUID = 1L;
	
	private final char c;

	public CharExpectedException(String expression, int pos, char c) {
		super(expression, pos, pos, "Missing character '" + c + "'");
		this.c = c;
	}

	public char getChar() {
		return c;
	}
}
