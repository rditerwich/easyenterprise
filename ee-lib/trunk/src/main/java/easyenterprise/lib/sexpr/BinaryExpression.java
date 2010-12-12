package easyenterprise.lib.sexpr;

import java.util.Arrays;
import java.util.List;

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
	public List<SExpr> getChildren() {
		return Arrays.asList(left, right);
	}
	
	@Override
	protected void toHtml(SExprOutputBuilder out) {
		out.toHtml(left);
		out.append(" " + operator + " ");
		out.toHtml(right);
	}
}
