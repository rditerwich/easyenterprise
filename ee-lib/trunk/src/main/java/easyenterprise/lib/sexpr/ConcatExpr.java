package easyenterprise.lib.sexpr;

import java.util.List;

public class ConcatExpr extends SExpr {

	private static final long serialVersionUID = 1L;
	
	private final List<SExpr> children;

	public ConcatExpr(String expression, int startPos, int endPos, List<SExpr> exprs) {
		super(expression, startPos, endPos);
		this.children = exprs;
	}

	public List<SExpr> getChildren() {
		return children;
	}
	
	@Override
	public String evaluate(SExprContext context) throws SExprEvaluationException {
		StringBuilder builder = new StringBuilder();
		for (SExpr child : children) {
			builder.append(child.evaluate(context));
		}
		return builder.toString();
	}
	
	@Override
	protected void toHtml(OutputBuilder out) {
		String sep = "";
		for (SExpr child : children) {
			out.text(sep);
			child.toHtml(out);
			sep = " ";
		}
	}

}
