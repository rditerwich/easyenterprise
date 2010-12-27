package easyenterprise.lib.sexpr;

public class Constant extends SExpr {
	
	private static final long serialVersionUID = 1L;
	
	public final String value;
	
	public static String constant(String value) {
		return new Constant(value).toString();
	}
	
	public Constant(String value) {
		super(value, 0, value.length());
		this.value = value;
	}
	
	public Constant(String expression, int startPos, int endPos, String value) {
		super(expression, startPos, endPos);
		this.value = value;
	}

	@Override
	public String evaluate(SExprContext context) {
		return value;
	}
	
	@Override
	protected void toHtml(SExprOutputBuilder out) {
		for (int i = value.length() - 1; i >= 0; i--) {
			if (SExprParser.isSep(value.charAt(i))) {
				char quote = '\"';
				for (; i >= 0; i--) {
					if (value.charAt(i) == quote) {
						quote = '\'';
						break;
					}
				}
				out.append(quote + value + quote);
				return;
			}
		}
		out.append(value);
	}
}
