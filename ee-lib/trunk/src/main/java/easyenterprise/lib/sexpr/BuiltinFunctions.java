package easyenterprise.lib.sexpr;

import java.util.List;

public class BuiltinFunctions {

	public static final SExprFunction replace = new AbstractFunction("replace", 3, 3) {
		public String evaluate(List<String> parameters) {
			String value = parameters.get(0);
			String regex = parameters.get(1);
			String replacement = parameters.get(2);
			return value.replaceAll(regex, replacement);
		}
	};
	
	public static SExprFunction[] functions = new SExprFunction[] {
		replace
	};
}
