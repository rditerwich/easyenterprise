package easyenterprise.lib.sexpr;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class DefaultContext implements SExprContext, Serializable {

	private static final long serialVersionUID = 1L;
	
	public final Map<String, String> variables = new TreeMap<String, String>();
	public final Map<String, SExprFunction> functions = new TreeMap<String, SExprFunction>();
	public final Set<String> constants = new TreeSet<String>();
	
	public Set<String> getVariablesNames() {
		return variables.keySet();
	}
	
	public Set<String> getFunctionNames() {
		return functions.keySet();
	}
	
	public Set<String> getConstants() {
		return constants;
	}
	
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
