package easyenterprise.lib.sexpr;

class OutputBuilder {
	private final StringBuilder out = new StringBuilder();
	private final boolean html;
	
	OutputBuilder(boolean html) {
		this.html = html;
	}
	public String toString() {
		return out.toString();
	}
	
	public OutputBuilder text(String text) {
		return text(null, text);
	}
	
	public OutputBuilder text(String className, String text) {
		if (!text.isEmpty()) {
			if (html) {
				if (className != null) {
					spanStart(className);
				}
				out.append(text.replaceAll("<", "&lt;").replaceAll("\n", "<br/>"));
				if (className != null) {
					spanEnd();
				}
			} else {
				out.append(text);
			}
		}
		return this;
	}
	
	public void punctuation(String punctuation) {
		text("punct", punctuation);
	}
	
	public void regularText(String text) {
		text("regular", text);
	}
	
	public void error(String text) {
		text("error", text);
	}
	
	private void spanStart(String className) {
		if (html) out.append("<span class=\"").append("sexpr-").append(className).append("\">");
	}
	
	private void spanEnd() {
		if (html) out.append("</span>");
	}
}
