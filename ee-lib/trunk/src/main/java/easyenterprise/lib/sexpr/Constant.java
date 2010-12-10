package easyenterprise.lib.sexpr;

public class Constant extends SExpr {
	
	private static final long serialVersionUID = 1L;
	
	public final String value;
	
	public Constant(String expression, int startPos, int endPos, String value) {
		super(expression, startPos, endPos);
		this.value = value;
	}

	@Override
	public String evaluate(SExprContext context) {
		return value;
	}
	
	@Override
	protected void toHtml(SExprOutputBuilder out) {
		out.text("const", value);
	}
}
