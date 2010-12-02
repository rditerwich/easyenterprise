package easyenterprise.lib.sexpr;


public class If extends SExpr {

	private static final long serialVersionUID = 1L;
	
	private final SExpr condition;
	private final SExpr expr;
	private final SExpr elseExpr;

	public If(String expression, int startPos, int endPos, SExpr condition, SExpr expr, SExpr elseExpr) {
		super(expression, startPos, endPos);
		this.condition = condition;
		this.expr = expr;
		this.elseExpr = elseExpr;
	}

	public SExpr getCondition() {
		return condition;
	}
	
	public SExpr getExpr() {
		return expr;
	}

	/**
	 * 
	 * @return else-expression, or null
	 */
	public SExpr getElseExpr() {
		return elseExpr;
	}
	
	@Override
	public String evaluate(SExprContext context) throws SExprEvaluationException {
		if (condition.evaluateBoolean(context)) {
			return expr.evaluate(context);
		} else if (elseExpr != null) {
			return elseExpr.evaluate(context);
		}
		return "";
	}
	
	@Override
	protected void toHtml(OutputBuilder out) {
		out.punctuation("if ");
		condition.toHtml(out);
		out.punctuation(" then ");
		expr.toHtml(out);
		if (elseExpr != null) {
			out.punctuation(" else ");
			elseExpr.toHtml(out);
		}
	}
}
