package easyenterprise.lib.sexpr;

import easyenterprise.lib.sexpr.SExpr.Styles;

public class SExprParseException extends Exception {

	private static final long serialVersionUID = 1L;

	private final String expression;
	private final int startPos;
	private final int endPos;

	public SExprParseException(String expression, int startPos, int endPos, String message) {
		super(message);
		this.expression = expression;
		this.startPos = startPos;
		this.endPos = endPos;
	}

	public String getExpression() {
		return expression;
	}
	
	public int getStartPos() {
		return startPos;
	}
	
	public int getEndPos() {
		return endPos;
	}
	
	public String toHtml(Styles styles) {
		SExprOutputBuilder out = new SExprOutputBuilder(new StringBuilder(), null, null, true, styles);
		out.append(styles.getStyle(Error.instance), getMessage());
		out.append(" : ");
		String before = expression.substring(0, startPos);
		out.append(before);
		String error = expression.substring(startPos, endPos);
		if (error.isEmpty()) {
			error = "[..]";
		}
		out.append(styles.getStyle(Error.instance), error);
		String after = expression.substring(endPos);
		out.append(after);
		return out.toString();
	}
}
