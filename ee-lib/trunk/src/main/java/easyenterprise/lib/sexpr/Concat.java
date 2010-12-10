package easyenterprise.lib.sexpr;

public class Concat extends BinaryExpression {

	private static final long serialVersionUID = 1L;

	public Concat(String expression, int startPos, int endPos, SExpr left, SExpr right) {
		super(expression, startPos, endPos, "++", left, right);
	}
	
	@Override
	public String evaluate(SExprContext context) throws SExprEvaluationException {
		return getLeft().evaluate(context) + getRight().evaluate(context);
	}

}
