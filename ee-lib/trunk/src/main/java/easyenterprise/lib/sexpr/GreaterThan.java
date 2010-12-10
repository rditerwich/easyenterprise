package easyenterprise.lib.sexpr;

public class GreaterThan extends BinaryExpression {

	private static final long serialVersionUID = 1L;

	public GreaterThan(String expression, int startPos, int endPos, SExpr left, SExpr right) {
		super(expression, startPos, endPos, ">", left, right);
	}
	
	@Override
	public String evaluate(SExprContext context) throws SExprEvaluationException {
		if (getLeft().evaluateNumber(context) > getRight().evaluateNumber(context)) {
			return trueValue; 
		} else {
			return falseValue;
		}
	}

}
