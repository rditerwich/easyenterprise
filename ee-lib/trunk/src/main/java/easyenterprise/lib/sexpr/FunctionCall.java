package easyenterprise.lib.sexpr;

import java.util.ArrayList;
import java.util.List;

public class FunctionCall extends SExpr {

	private static final long serialVersionUID = 1L;
	
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
	
	public void validate(SExprContext context, List<SExprParseException> validationMessages) {
		SExprFunction function = context.getFunction(name);
		if (function == null) {
			validationMessages.add(new FunctionNotFoundException(expression, startPos, endPos, name));
		}
		if (parameters.size() < function.getMinParameters() || parameters.size() > function.getMaxParameters()) {
			validationMessages.add(new IncorrectNumberOfParameters(expression, startPos, endPos, function.getMinParameters(), function.getMaxParameters()));
		}

	}
	
	@Override
	public String evaluate(SExprContext context) throws SExprEvaluationException {
		SExprFunction function = context.getFunction(name);
		if (function == null) {
			throw new SExprEvaluationException("Function '" + name + "' not found");
		}
		if (parameters.size() < function.getMinParameters() || parameters.size() > function.getMaxParameters()) {
			throw new SExprEvaluationException("Function '" + name + "' has wrong number of parameters. Found " + parameters.size() + ",  expected " + function.getMinParameters() + (function.getMinParameters() < function.getMaxParameters() ?  " to " + function.getMaxParameters(): ""));
		}
		List<String> evaluatedParameters = new ArrayList<String>(parameters.size());
		for (SExpr parameter : parameters) {
			evaluatedParameters.add(parameter.evaluate(context));
		}
		return function.evaluate(evaluatedParameters);
	}
	
	@Override
	protected void toHtml(SExprOutputBuilder out) {
		out.append(name + "(");
		String sep = "";
		for (SExpr parameter : parameters) {
			out.append(sep);
			out.toHtml(parameter);
			sep = ", ";
		}
		out.append(")");
	}
}
