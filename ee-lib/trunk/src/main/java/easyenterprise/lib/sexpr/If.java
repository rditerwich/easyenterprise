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
	protected void toHtml(SExprOutputBuilder out) {
		toHtml(out.isEmpty(), out);
	}
		
	protected void toHtml(boolean topLevel, SExprOutputBuilder out) {
		out.append("if ");
		out.toHtml(condition);
		out.append(" then ");
		out.toHtml(expr);
		if (elseExpr != null) {
			if (topLevel) {
				out.append("\n");
			}
			if (elseExpr instanceof If) {
				((If) elseExpr).toHtml(topLevel, out.get(elseExpr));
			} else {
				out.append(topLevel ? "else " : " else ");
				out.toHtml(elseExpr);
			}
		}
	}
}
