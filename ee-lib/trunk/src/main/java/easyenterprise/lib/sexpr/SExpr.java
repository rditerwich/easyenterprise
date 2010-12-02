package easyenterprise.lib.sexpr;

import java.io.Serializable;

/**
 * Simple or String expressions.
 */
public abstract class SExpr implements Serializable {

	private static final long serialVersionUID = 1L;
	
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
	
	public abstract String evaluate(SExprContext context) throws SExprEvaluationException;

	public boolean evaluateBoolean(SExprContext context) throws SExprEvaluationException {
		String result = evaluate(context).trim();
		return !result.isEmpty() && !result.equals("false");
	}
	
	public double evaluateNumber(SExprContext context) throws SExprEvaluationException {
		try {
			String result = evaluate(context).trim();
			return Double.parseDouble(result);
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}
	
	@Override
	public final String toString() {
		OutputBuilder out = new OutputBuilder(false);
		toHtml(out);
		return out.toString();
	}
	
	public final String toHtml() {
		OutputBuilder out = new OutputBuilder(true);
		toHtml(out);
		return out.toString();
	}

	protected void toHtml(OutputBuilder out) {
	}
}