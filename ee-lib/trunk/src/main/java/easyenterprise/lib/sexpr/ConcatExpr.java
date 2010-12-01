package easyenterprise.lib.sexpr;

import java.util.List;

public class ConcatExpr extends SExpr {

	private final List<SExpr> children;

	public ConcatExpr(String expression, int startPos, int endPos, List<SExpr> exprs) {
		super(expression, startPos, endPos);
		this.children = exprs;
	}

	public List<SExpr> getChildren() {
		return children;
	}
	
	@Override
	protected void toHtml(OutputBuilder out) {
		String sep = "";
		for (SExpr child : children) {
			child.toHtml(out);
			out.text(sep);
			sep = " ";
		}
	}

}
