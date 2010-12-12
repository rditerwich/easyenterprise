package easyenterprise.lib.sexpr;

public class Error extends SExpr {

	static final Error instance = new Error("", 0, 0);
	
	protected Error(String expression, int startPos, int endPos) {
		super(expression, startPos, endPos);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public String evaluate(SExprContext context) throws SExprEvaluationException {
		throw new SExprEvaluationException("Error");
	}

}
