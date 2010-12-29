package easyenterprise.lib.sexpr;

import static easyenterprise.lib.util.ObjectUtil.orElse;


public class VarRef extends SExpr {
	
	private static final long serialVersionUID = 1L;
	
	public final String var;
	
	public VarRef(String expression, int startPos, int endPos, String var) {
		super(expression, startPos, endPos);
		this.var = var;
	}

	@Override
	public String evaluate(SExprContext context) throws SExprEvaluationException {
		return orElse(context.getVariable(var), "");
	}
	
	@Override
	protected void toHtml(SExprOutputBuilder out) {
		out.append("#"+ var);
	}
}
