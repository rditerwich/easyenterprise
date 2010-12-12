package easyenterprise.lib.sexpr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import easyenterprise.lib.sexpr.SExpr.Styles;

public class TestSExprParser {

	private DefaultContext context = new DefaultContext();
	
	@Before
	public void init() {
		context.addFunctions(BuiltinFunctions.functions);
	}
	
	@Test
	public void test() throws SExprParseException, SExprEvaluationException {
//		assertExpr("#name", "#name");
//		assertExpr("name #name", "name   #name");
//		assertExpr("name", "name");
//		assertExpr("name", "'name'");
//		assertExpr("name", "\"name\"");
//		assertExpr("replace(12, 12, 12)", "replace(12,    12,12)");
//		assertExpr("fun(12)", "fun ( 12)");
//		assertExpr("fun(12, )", "fun ( 12  , )");
//		assertExpr("fun(12, #1)", "fun ( 12  ,  #1)");
//		assertExpr("name < name", "name < name");
		
		context.setVariable("name", "John");
		assertEval("John", "#name");
		assertEval("true", "12 > 11");
		assertEval("true", "12 != 11");
		assertEval("true", "John == #name");
		assertEval("true", "'John' == #name");
		assertEval("reallytrue", "really ++ ('John' == #name)");
		assertEval("right", "replace(left, 'l.*f', righ)");
		assertEval("li(v)i(n)g i(s) easy", "replace('living is easy', 'i(.)', 'i($1)')");
		assertEval("13", "if '' then 12 else if #name then 13");
	}
	
	@Test
	public void testWhitespace() throws SExprParseException {
		String input = "  #name  \n ++ 12";
		SExpr expr = new SExprParser().parse(input);
		Styles styles = new Styles();
		styles.put(Concat.class, "color:purple;font-weight:bold");
		styles.put(VarRef.class, "color:black;font-weight:bold;font-style:italic");
		String html = expr.toHtml(true, styles);
		System.out.println(html);
	}
	
	private void assertExpr(String expected, String expr) throws SExprParseException {
			DefaultContext context = new DefaultContext();
			context.addFunctions(BuiltinFunctions.functions);
			context.addFunctions(new AbstractFunction("fun", 1, 2) {
				public String evaluate(List<String> parameters) {
					return "have fun";
				}
			});
			String actual = new SExprParser().parse(expr).toString();
			System.out.println(expr + " -> " + actual);
			Assert.assertEquals(expected, actual);
	}
	
	private void assertEval(String expected, String expr) throws SExprParseException, SExprEvaluationException {
		String actual = new SExprParser().parse(expr).evaluate(context);
		System.out.println(expr + " -> " + actual);
		Assert.assertEquals(expected, actual);
	}
}
