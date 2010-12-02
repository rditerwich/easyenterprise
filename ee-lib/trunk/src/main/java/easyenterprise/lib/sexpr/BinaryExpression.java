package easyenterprise.lib.sexpr;

public abstract class BinaryExpression extends SExpr {
	
	private static final long serialVersionUID = 1L;
	private final String operator;
	private final SExpr left;
	private final SExpr right;

	protected BinaryExpression(String expression, int startPos, int endPos, String operator, SExpr left, SExpr right) {
		super(expression, startPos, endPos);
		this.operator = operator;
		this.left = left;
		this.right = right;
	}
	
	public SExpr getLeft() {
		return left;
	}
	
	public SExpr getRight() {
		return right;
	}
	
	@Override
	protected void toHtml(OutputBuilder out) {
		left.toHtml(out);
		out.punctuation(operator);
		right.toHtml(out);
	}

}
