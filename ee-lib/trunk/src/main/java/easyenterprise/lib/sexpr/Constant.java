package easyenterprise.lib.sexpr;

public class Constant extends SExpr {
	
	public final String value;
	
	public Constant(String expression, int startPos, int endPos, String value) {
		super(expression, startPos, endPos);
		this.value = value;
	}
	
	@Override
	protected void toHtml(OutputBuilder out) {
		out.spanStart("const");
		out.text(value);
		out.spanEnd();
	}
}
