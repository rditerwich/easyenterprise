package easyenterprise.lib.sexpr;

import java.util.List;

public class BuiltinFunctions {

	public static final SExprFunction replace = new AbstractFunction("replace", 3, 3) {
		public String evaluate(List<String> parameters) {
			return null;
		}
	};
	
	public static SExprFunction[] functions = new SExprFunction[] {
		replace
	};
}
