package easyenterprise.lib.sexpr;

public class IncorrectNumberOfParameters extends SExprParseException {

	private static final long serialVersionUID = 1L;

	public IncorrectNumberOfParameters(String expression, int startPos, int endPos, int expectedMin, int expectedMax) {
		super(expression, startPos, endPos, "Incorrect number of parameters (expected " + expectedMin + (expectedMax > expectedMin ? " to " + expectedMax : "") + ")");
	}

}
