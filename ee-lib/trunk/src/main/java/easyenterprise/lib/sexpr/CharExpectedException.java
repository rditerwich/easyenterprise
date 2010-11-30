package easyenterprise.lib.sexpr;

public class CharExpectedException extends SExprParseException {

	private static final long serialVersionUID = 1L;
	
	private final char c;

	public CharExpectedException(char c, int pos) {
		super("Missing character '" + c + "'", pos);
		this.c = c;
	}

	public char getChar() {
		return c;
	}
}
