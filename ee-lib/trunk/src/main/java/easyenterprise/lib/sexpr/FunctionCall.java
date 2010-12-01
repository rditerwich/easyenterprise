package easyenterprise.lib.sexpr;

import java.util.List;

public class FunctionCall extends SExpr {

	public final String name;
	public final List<SExpr> parameters;

	public FunctionCall(String expression, int startPos, int endPos, String name, List<SExpr> parameters) {
		super(expression, startPos, endPos);
		this.name = name;
		this.parameters = parameters;
	}
	
	public String getName() {
		return name;
	}
	
	public List<SExpr> getParameters() {
		return parameters;
	}
	
	@Override
	protected void toHtml(OutputBuilder out) {
		out.spanStart("func");
		out.text(name);
		out.spanEnd();
		out.punctuation("(");
		String sep = "";
		for (SExpr parameter : parameters) {
			out.punctuation(sep);
			parameter.toHtml(out);
			sep = ", ";
		}
		out.punctuation(")");
	}
}
