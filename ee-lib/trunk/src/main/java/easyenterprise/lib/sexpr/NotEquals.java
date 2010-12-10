package easyenterprise.lib.sexpr;

public class NotEquals extends BinaryExpression {

	private static final long serialVersionUID = 1L;

	public NotEquals(String expression, int startPos, int endPos, SExpr left, SExpr right) {
		super(expression, startPos, endPos, "!=", left, right);
	}
	
	@Override
	public String evaluate(SExprContext context) throws SExprEvaluationException {
		String left = getLeft().evaluate(context);
		String right = getRight().evaluate(context);
		if (left == right || (left != null && left.equals(right))) {
			return falseValue; 
		} else {
			return trueValue;
		}
	}

}
