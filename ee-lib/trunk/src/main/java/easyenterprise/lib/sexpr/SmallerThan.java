package easyenterprise.lib.sexpr;

public class SmallerThan extends BinaryExpression {

	private static final long serialVersionUID = 1L;

	public SmallerThan(String expression, int startPos, int endPos, SExpr left, SExpr right) {
		super(expression, startPos, endPos, "<", left, right);
	}
	
	@Override
	public String evaluate(SExprContext context) throws SExprEvaluationException {
		if (getLeft().evaluateNumber(context) < getRight().evaluateNumber(context)) {
			return trueValue; 
		} else {
			return falseValue;
		}
	}

}
