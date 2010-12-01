package easyenterprise.lib.sexpr;

import org.junit.Assert;
import org.junit.Test;

public class TestSExprParser {

	@Test
	public void test() throws SExprParseException {
		assertExpr("#name", "#name");
		assertExpr("name #name", "name   #name");
		assertExpr("name", "name");
		assertExpr("name", "'name'");
		assertExpr("name", "\"name\"");
		assertExpr("fun(12)", "fun(12)");
		assertExpr("fun(12)", "fun ( 12)");
		assertExpr("fun(12, )", "fun ( 12  , )");
		assertExpr("fun(12, #1)", "fun ( 12  ,  #1)");
	}
	
	private void assertExpr(String expected, String expr) throws SExprParseException {
		String actual = new SExprParser().parse(expr).toString();
		System.out.println(expr + " -> " + actual);
		Assert.assertEquals(expected, actual);
	}
}
