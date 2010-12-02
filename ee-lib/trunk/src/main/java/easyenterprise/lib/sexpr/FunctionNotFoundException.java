package easyenterprise.lib.sexpr;

public class FunctionNotFoundException extends SExprParseException {

	private static final long serialVersionUID = 1L;
	private final String function;

	public FunctionNotFoundException(String expression, int startPos, int endPos,	String function) {
		super(expression, startPos, endPos, "Function not found: " + function);
		this.function = function;
	}
	
	public String getFunction() {
		return function;
	}
}
