package easyenterprise.lib.sexpr;

import java.util.List;

public interface SExprFunction {

	String getName();
	int getMinParameters();
	int getMaxParameters();
	String evaluate(List<String> parameters);
}
