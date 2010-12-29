package easyenterprise.lib.sexpr;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class DefaultContext implements SExprContext, Serializable {

	private static final long serialVersionUID = 1L;
	
	private final DefaultContext parent;
	private final Map<String, String> variables = new TreeMap<String, String>();
	private final Map<String, SExprFunction> functions = new TreeMap<String, SExprFunction>();
	private final Set<String> constants = new TreeSet<String>();
	
	public DefaultContext() {
		this.parent = null;
	}
	
	public DefaultContext getParent() {
		return parent;
	}
	
	public DefaultContext(DefaultContext parent) {
		this.parent = parent;
	}
	
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
		name = normalizeVariableName(name);
		if (parent != null) {
			String variable = parent.getVariable(name);
			if (variable != null) {
				return variable;
			}
		}
		return variables.get(name);
	}

	@Override
	public SExprFunction getFunction(String name) {
		if (parent != null) {
			SExprFunction fun = parent.getFunction(name);
			if (fun != null) {
				return fun;
			}
		}
		return functions.get(name);
	}
	
	public void addFunctions(SExprFunction... functions) {
		for (SExprFunction function : functions) {
			this.functions.put(function.getName(), function);
		}
	}

	public void setVariable(String name, String value) {
		name = normalizeVariableName(name);
		variables.put(name, value);
	}
	
	protected String normalizeVariableName(String var) {
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < var.length(); i++) {
			char c = var.charAt(i);
			if (isValidVariableNameChar(c)) {
				out.append(c);
			}
		}
		return out.toString();
	}

	private boolean isValidVariableNameChar(char c) {
		return Character.isLetterOrDigit(c);
	}
}
