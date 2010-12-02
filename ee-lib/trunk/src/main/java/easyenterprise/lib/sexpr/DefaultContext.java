package easyenterprise.lib.sexpr;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultContext implements SExprContext, Serializable {

	private static final long serialVersionUID = 1L;
	
	public final Map<String, String> variables = new LinkedHashMap<String, String>();
	public final Map<String, SExprFunction> functions = new LinkedHashMap<String, SExprFunction>();
	
	@Override
	public String getVariable(String name) {
		return variables.get(name);
	}

	@Override
	public SExprFunction getFunction(String name) {
		return functions.get(name);
	}
	
	public void addFunctions(SExprFunction... functions) {
		for (SExprFunction function : functions) {
			this.functions.put(function.getName(), function);
		}
	}

	public void setVariable(String name, String value) {
		variables.put(name, value);
	}
}
