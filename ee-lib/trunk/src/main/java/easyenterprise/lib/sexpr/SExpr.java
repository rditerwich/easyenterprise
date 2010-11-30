package easyenterprise.lib.sexpr;

public class SExpr {

	private final String expression;
	private final int startPos;
	private final int endPos;

	protected SExpr(String expression, int startPos, int endPos) {
		this.expression = expression;
		this.startPos = startPos;
		this.endPos = endPos;
	}

	public String getImage() {
		return expression.substring(startPos, endPos);
	}

	public int getImageLength() {
		return endPos - startPos;
	}

	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		toString(out);
		return out.toString();
	}
	
	protected void toString(StringBuilder out) {
	}
	
}
