package easyenterprise.lib.sexpr;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Simple or String expressions.
 */
public abstract class SExpr implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String falseValue = "false";
	public static final String trueValue = "true";

	public static final SExpr empty = new Constant("", 0, 0, "");
	
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
	
	public List<SExpr> getChildren() {
		return Collections.emptyList();
	}
	
	public abstract String evaluate(SExprContext context) throws SExprEvaluationException;

	public boolean evaluateBoolean(SExprContext context) throws SExprEvaluationException {
		String result = evaluate(context).trim();
		return !result.isEmpty() && !result.equals(falseValue);
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
		SExprOutputBuilder out = new SExprOutputBuilder(false);
		toHtml(out);
		return out.toString();
	}
	
	public final String toHtml() {
		SExprOutputBuilder out = new SExprOutputBuilder(true);
		toHtml(out);
		return out.toString();
	}
	
	public final String toHtml(Map<Class<? extends SExpr>, String> styles) {
		StringBuilder out = new StringBuilder();
		toHtml(styles, 0, out);
		return out.toString();
	}

	protected void toHtml(SExprOutputBuilder out) {
	}
	
	protected int toHtml(Map<Class<? extends SExpr>, String> styles, int pos, StringBuilder out) {
		if (pos < startPos) {
			out.append(getHtml(pos, startPos));
			pos = startPos; 
		}
		for (SExpr child : getChildren()) {
			if (pos < child.startPos) {
				out.append("<span style=\"" + getStyle(styles) + "\">");
				out.append(getHtml(pos, child.startPos));
				pos = child.startPos; 
				out.append("</span>");
			}
			pos = child.toHtml(styles, pos, out);
		}
		if (pos < endPos) {
			out.append("<span style=\"" + getStyle(styles) + "\">");
			out.append(getHtml(pos, endPos));
			pos = endPos; 
			out.append("</span>");
		}
		return pos;
	}
	
	private String getHtml(int start, int end) {
		return expression.substring(start, end).replaceAll(" ", "&#160;").replaceAll("\u00a0", "&#160").replaceAll("\n", "<br/>");
	}
	
	private String getStyle(Map<Class<? extends SExpr>, String> styles) {
		String style = styles.get(getClass());
		if (style == null) {
			style = styles.get(SExpr.class);
		}
		if (style == null) {
			style = "black";
		}
		return style;
	}
}