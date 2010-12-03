package easyenterprise.lib.sexpr;

public class IdentifierExpectedException extends SExprParseException {

	private static final long serialVersionUID = 1L;
	
	private final String identifier;

	public IdentifierExpectedException(String expression, int pos, String identifier) {
		super(expression, pos, pos, "Missing '" + identifier + "'");
		this.identifier = identifier;
	}
	
	public String getIdentifier() {
		return identifier;
	}
}
