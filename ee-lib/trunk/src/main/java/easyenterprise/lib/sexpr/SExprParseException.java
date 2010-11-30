package easyenterprise.lib.sexpr;

public class SExprParseException extends Exception {

	private static final long serialVersionUID = 1L;
	private final int pos;

	public SExprParseException(String message, int pos) {
		super(message);
		this.pos = pos;
	}

	public int getPos() {
		return pos;
	}

}
