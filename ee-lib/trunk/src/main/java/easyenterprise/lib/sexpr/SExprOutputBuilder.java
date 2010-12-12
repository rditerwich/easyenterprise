package easyenterprise.lib.sexpr;

import easyenterprise.lib.sexpr.SExpr.Styles;

class SExprOutputBuilder {
	
	private final StringBuilder out;
	private final SExprOutputBuilder parent;
	private final SExpr expr;
	private final boolean html;
	private final Styles styles;
	private boolean hasOpenSpan;
	private int lastLength;
	
	SExprOutputBuilder(StringBuilder out, SExprOutputBuilder parent, SExpr expr, boolean html, SExpr.Styles styles) {
		this.out = out;
		this.parent = parent;
		this.expr = expr;
		this.html = html;
		this.styles = styles;
	}
	
	public boolean isEmpty() {
		return out.length() == 0;
	}
	
	public void toHtml(SExpr child) {
		SExprOutputBuilder childOut = get(child);
		child.toHtml(childOut);
		childOut.closeSpan();
	}

	public SExprOutputBuilder get(SExpr child) {
		return new SExprOutputBuilder(out, this, child, html, styles);
	}
	
	public String toString() {
		closeSpan();
		return out.toString();
	}
	
	public SExprOutputBuilder append(String text) {
		return append(expr != null ? styles.getStyle(expr) : "", text);
	}
	
	public SExprOutputBuilder append(String attr, String text) {
		if (!text.isEmpty()) {
			if (parent != null) {
				parent.closeSpan();
			}
			if (lastLength != out.length()) {
				closeSpan();
			}
			openSpan(attr);
			if (html) {
				text = text.replaceAll("<", "&lt;").replaceAll("\n", "<br/>").replaceAll(" ", "&nbsp;");
			}
			out.append(text);
			lastLength = out.length();
		}
		return this;
	}
	
	private void openSpan(String attr) {
		if (html && !attr.trim().equals("")) {
			out.append("<span ").append(attr).append(">");
			hasOpenSpan = true;
		}
	}
	
	private void closeSpan() {
		if (hasOpenSpan) {
			out.append("</span>");
			hasOpenSpan = false;
		}
	}
}
