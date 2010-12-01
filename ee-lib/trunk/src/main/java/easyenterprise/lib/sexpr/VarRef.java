package easyenterprise.lib.sexpr;

public class VarRef extends SExpr {
	
	public final String var;
	
	public VarRef(String expression, int startPos, int endPos, String var) {
		super(expression, startPos, endPos);
		this.var = var;
	}

	@Override
	protected void toHtml(OutputBuilder out) {
		out.spanStart("var");
		out.text("#").text(var);
		out.spanEnd();
	}
}
