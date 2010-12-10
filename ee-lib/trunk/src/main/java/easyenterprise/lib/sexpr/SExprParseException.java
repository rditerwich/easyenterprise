package easyenterprise.lib.sexpr;

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
	
	public String toHtml() {
		SExprOutputBuilder out = new SExprOutputBuilder(true);
		String before = expression.substring(0, startPos);
		out.regularText(before);
		String error = expression.substring(startPos, endPos);
		if (error.isEmpty()) {
			error = "[..]";
		}
		out.error(error);
		String after = expression.substring(endPos);
		out.regularText(after);
		return out.toString();
	}
}
