package easyenterprise.lib.sexpr;

public interface SExprContext {

	/**
	 * 
	 * @param name
	 * @return variable content, or null
	 */
	String getVariable(String name);
	
	/**
	 * @param name
	 * @return function, or null
	 */
	SExprFunction getFunction(String name);
}
