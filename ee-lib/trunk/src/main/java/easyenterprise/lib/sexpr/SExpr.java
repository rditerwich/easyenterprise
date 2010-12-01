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
	
	protected void spanEnd(StringBuilder out) {
		out.append("</span>");
	}
	
	protected class OutputBuilder {
		private final StringBuilder out = new StringBuilder();
		private final boolean html;
		OutputBuilder(boolean html) {
			this.html = html;
		}
		public String toString() {
			return out.toString();
		}
		public OutputBuilder text(String text) {
			if (html) {
				text = text.replace("<", "&lt;");
			}
			out.append(text);
			return this;
		}
		
		protected void punctuation(String punctuation) {
			if (!punctuation.isEmpty()) {
				spanStart("punct");
				text(punctuation);
				spanEnd();
			}
		}
		
		protected void spanStart(String className) {
			if (html) out.append("<span class=\"").append("sexpr-").append(className).append("\">");
		}
		
		protected void spanEnd() {
			if (html) out.append("</span>");
		}
	}
}
