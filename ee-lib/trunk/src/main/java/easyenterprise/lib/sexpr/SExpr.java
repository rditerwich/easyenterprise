package easyenterprise.lib.sexpr;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Simple or String expressions.
 */
public abstract class SExpr implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String falseValue = "false";
	public static final String trueValue = "true";

	public static final SExpr empty = new Constant("", 0, 0, "");

	public static final Styles emptyStyles = new Styles();
	
	protected final String expression;
	protected final int startPos;
	protected final int endPos;

	protected SExpr(String expression, int startPos, int endPos) {
		this.expression = expression;
		this.startPos = startPos;
		this.endPos = endPos;
	}

	public boolean isEmpty() {
		return startPos >= endPos;
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
		SExprOutputBuilder out = new SExprOutputBuilder(new StringBuilder(), null, this, false, emptyStyles);
		toHtml(out);
		return out.toString();
	}
	
	public final String toHtml(boolean reformat, Styles styles) {
		if (reformat) {
			SExprOutputBuilder out = new SExprOutputBuilder(new StringBuilder(), null, this, true, styles);
			toHtml(out);
			return out.toString();
		} else {
			StringBuilder out = new StringBuilder();
			toHtml(styles, 0, out);
			return out.toString();
		}
	}
	
	public static class Styles extends HashMap<Class<? extends SExpr>, String> {
		private static final long serialVersionUID = 1L;
		public String getStyle(SExpr expr) {
			for (Class<?> c = expr.getClass(); c != null; c = c.getSuperclass()) {
				String style = get(c);
				if (style != null) {
					return style;
				}
			}
			return "";
		}
	}
	
	protected void toHtml(SExprOutputBuilder out) {
	}
	
	private int toHtml(Styles styles, int pos, StringBuilder out) {
		if (pos < startPos) {
			out.append(getHtml(pos, startPos));
			pos = startPos; 
		}
		for (SExpr child : getChildren()) {
			if (pos < child.startPos) {
				out.append("<span style=\"" + styles.getStyle(this) + "\">");
				out.append(getHtml(pos, child.startPos));
				pos = child.startPos; 
				out.append("</span>");
			}
			pos = child.toHtml(styles, pos, out);
		}
		if (pos < endPos) {
			out.append("<span style=\"" + styles.getStyle(this) + "\">");
			out.append(getHtml(pos, endPos));
			pos = endPos; 
			out.append("</span>");
		}
		return pos;
	}
	
	private String getHtml(int start, int end) {
		return expression.substring(start, end).replaceAll(" ", "&#160;").replaceAll("\u00a0", "&#160").replaceAll("\n", "<br/>");
	}
	
}