package easyenterprise.lib.sexpr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SExprParser {

	private final SExprContext context;
	private String expression;
	private int curPos;
	private char curChar;
	private String value;
	
	public SExprParser(SExprContext context) {
		this.context = context;
	}
	
	public SExpr parse(String expression) throws SExprParseException {
		this.expression = expression;
		this.curPos = -1;
		this.value = "";
		next();
		return parse();
	}
	
	private SExpr parse() throws SExprParseException {
		skipWhiteSpace();
		int startPos = curPos;
		SExpr result = parseSingle(false);
		if (result == null) {
			return new Constant(expression, curPos, curPos, "");
		}

		while (true) {

			// look for binary expression
			skipWhiteSpace();
			if (eat("<")) {
				SExpr right = parseSingle(true);
				result = new SmallerThan(expression, startPos, curPos, result, right);
			}
			else if (eat(">")) {
				SExpr right = parseSingle(true);
				result = new GreaterThan(expression, startPos, curPos, result, right);
			}
			else if (eat("==")) {
				SExpr right = parseSingle(true);
				result = new Equals(expression, startPos, curPos, result, right);
			}
			else if (eat("!=")) {
				SExpr right = parseSingle(true);
				result = new NotEquals(expression, startPos, curPos, result, right);
			}
			
			SExpr next = parseSingle(false);
			if (next != null) {
				result = new Concat(expression, startPos, curPos, result, next);
			} else {
				return result;
			}
		}
	}
	
	private SExpr parseSingle(boolean mandatory) throws SExprParseException {
		skipWhiteSpace();
		SExpr expr = null;
		int startPos = curPos;
		if (parseQuotedString()) {
			return new Constant(expression, startPos, curPos, value);
		}
		expr = parseVarRef();
		if (expr == null) {
			expr = parseFunctionOrConstant();
		}
		if (expr == null && mandatory) {
			throw new ExpressionExpectedException(expression, curPos);
		}
		return expr;
	}
	
	private VarRef parseVarRef() throws SExprParseException {
		if (curChar != '#') {
			return null;
		}
		int startPos = curPos;
		next();
		if (!parseWord()) {
			throw new IdentifierOrNumberExpectedException(expression, curPos - 1);
		}
		return new VarRef(expression, startPos, curPos, value);
	}

	private SExpr parseFunctionOrConstant() throws SExprParseException {
		int startPos = curPos;
		if (!parseWord()) {
			return null;
		}
		if (curChar == '(') {
			SExprFunction function = context.getFunction(value);
			String name = value;
			if (function == null) {
				throw new FunctionNotFoundException(expression, startPos, curPos, name);
			}
			next();
			List<SExpr> parameters = parseParameters();
			if (parameters.size() < function.getMinParameters() || parameters.size() > function.getMaxParameters()) {
				throw new IncorrectNumberOfParameters(expression, startPos, curPos, function.getMinParameters(), function.getMaxParameters());
			}
			return new FunctionCall(expression, startPos, curPos, name, parameters);
		}
		return new Constant(expression, startPos, curPos, value);
	}
	
	private List<SExpr> parseParameters() throws SExprParseException {
		List<SExpr> result = Collections.emptyList();
		while (true) {
			if (result.isEmpty()) {
				result = new ArrayList<SExpr>();
			}
			result.add(parse());
			skipWhiteSpace();
			if (curChar == ')') {
				next();
				break;
			}
			if (curChar == ',') {
				next();
				continue;
			}
			throw new CharExpectedException(expression, curPos, ')');
		}
		return result;
	}

	private boolean parseQuotedString() throws SExprParseException {
		char sep = curChar;
		if (curChar != '"' && curChar != '\'') {
			return false;
		}
		StringBuilder value = new StringBuilder();
		for (next(); until(sep); next()) {
			value.append(curChar);
		}
		next();
		this.value = value.toString();
		return true;
	}
	
	private boolean parseWord() {
		StringBuilder value = new StringBuilder();
		for (; curChar != 0 && !isSep(); next()) {
			value.append(curChar);
		}
		this.value = value.toString();
		skipWhiteSpace();
		return !this.value.isEmpty();
	}

	private void skipWhiteSpace() {
		for (; Character.isWhitespace(curChar); next());
	}
	
	private boolean until(char endChar) throws CharExpectedException {
		if (curChar == 0) {
			throw new CharExpectedException(expression, curPos, endChar);
		}
		return curChar != endChar;
	}
	
	private boolean eat(String text) {
		for (int i = 0; i < text.length(); i++) {
			if (curPos + i >= expression.length()) {
				return false;
			}
			if (expression.charAt(curPos + i) != text.charAt(i)) {
				return false;
			}
		}
		curPos += text.length();
		curChar = expression.charAt(curPos);
		return true;
	}
	
	private boolean next() {
		curPos++;
		if (curPos < expression.length()) {
			curChar = expression.charAt(curPos);
			return true;
		}
		curChar = 0;
		return false;
	}
	private boolean isSep() {
		return Character.isWhitespace(curChar)
		|| curChar == '#'
		|| curChar == ':'
		|| curChar == ','
		|| curChar == '('
		|| curChar == ')';
	}
}
