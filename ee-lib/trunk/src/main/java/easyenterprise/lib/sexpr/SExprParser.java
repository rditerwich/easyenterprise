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
	private int length = 0;
	
	public SExprParser(SExprContext context) {
		this.context = context;
	}
	
	public SExpr parse(String expression) throws SExprParseException {
		this.expression = expression;
		this.length = expression.length();
		this.curPos = -1;
		next();
		this.value = "";
		SExpr expr = parse();
		if (curPos < length) {
			throw new InvalidExpression(expression, curPos, length);
		}
		return expr;
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
			if (eat('<')) {
				SExpr right = parseSingle(true);
				result = new SmallerThan(expression, startPos, curPos, result, right);
			}
			else if (eat('>')) {
				SExpr right = parseSingle(true);
				result = new GreaterThan(expression, startPos, curPos, result, right);
			}
			else if (eat('=','=')) {
				SExpr right = parseSingle(true);
				result = new Equals(expression, startPos, curPos, result, right);
			}
			else if (eat('!','=')) {
				SExpr right = parseSingle(true);
				result = new NotEquals(expression, startPos, curPos, result, right);
			}
			else if (eat('+','+')) {
				SExpr right = parseSingle(true);
				result = new Concat(expression, startPos, curPos, result, right);
			} else {
				return result;
			}
		}
	}
	
	private SExpr parseSingle(boolean mandatory) throws SExprParseException {
		skipWhiteSpace();
		int startPos = curPos;
		if (parseQuotedString()) {
			return new Constant(expression, startPos, curPos, value);
		}
		SExpr expr = parseVarRef();
		if (expr == null) {
			expr = parseIf();
		}
		if (expr == null) {
			expr = parseFunctionOrConstant();
		}
		if (expr == null) {
			expr = parseBracedExpression();
		}
		if (expr == null && mandatory) {
			throw new ExpressionExpectedException(expression, curPos);
		}
		return expr;
	}
	
	private SExpr parseIf() throws SExprParseException {
		int startPos = curPos;
		if (eat("if")) {
			SExpr condition = parse();
			skipWhiteSpace();
			if (!eat("then")) {
				throw new IdentifierExpectedException(expression, curPos, "then");
			}
			skipWhiteSpace();
			SExpr expr = parse();
			skipWhiteSpace();
			SExpr elseExpr = null;
			if (eat("else")) {
				skipWhiteSpace();
				elseExpr = parse();
			}
			return new If(expression, startPos, curPos, condition, expr, elseExpr);
		}
		
		return null;
	}

	private VarRef parseVarRef() throws SExprParseException {
		int startPos = curPos;
		if (eat('#')) {
			if (!parseWord()) {
				throw new IdentifierOrNumberExpectedException(expression, curPos - 1);
			}
			return new VarRef(expression, startPos, curPos, value);
		}
		return null;
	}

	private SExpr parseFunctionOrConstant() throws SExprParseException {
		int startPos = curPos;
		if (parseWord()) {
			if (eat('(')) {
				SExprFunction function = context.getFunction(value);
				String name = value;
				if (function == null) {
					throw new FunctionNotFoundException(expression, startPos, curPos - 1, name);
				}
				List<SExpr> parameters = parseParameters();
				if (parameters.size() < function.getMinParameters() || parameters.size() > function.getMaxParameters()) {
					throw new IncorrectNumberOfParameters(expression, startPos, curPos, function.getMinParameters(), function.getMaxParameters());
				}
				return new FunctionCall(expression, startPos, curPos, name, parameters);
			} else {
				return new Constant(expression, startPos, curPos, value);
			}
		}
		return null;
	}
	
	private List<SExpr> parseParameters() throws SExprParseException {
		List<SExpr> result = Collections.emptyList();
		while (true) {
			if (result.isEmpty()) {
				result = new ArrayList<SExpr>();
			}
			result.add(parse());
			skipWhiteSpace();
			if (eat(')')) break;
			if (eat(',')) continue;
			throw new CharExpectedException(expression, curPos, ')');
		}
		return result;
	}
	
	private SExpr parseBracedExpression() throws SExprParseException {
		if (eat('(')) {
			SExpr result = parse();
			skipWhiteSpace();
			if (!eat(')')) {
				throw new CharExpectedException(expression, curPos, ')');
			}
			return result;
		}
		return null;
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
		int newPos = curPos;
		for (int i = 0; i < text.length(); i++, newPos++) {
			if (newPos >= length) {
				return false;
			}
			if (expression.charAt(newPos) != text.charAt(i)) {
				return false;
			}
		}
		if (newPos < length && Character.isJavaIdentifierPart(expression.charAt(newPos))) {
			return false;
		}
		curPos = newPos;
		curChar = curPos < length ? expression.charAt(curPos) : 0;
		return true;
	}
	
	private boolean eat(char c) {
		if (curChar == c) {
			next();
			return true;
		}
		return false;
	}
	
	private boolean eat(char c1, char c2) {
		if (curChar == c1 && curPos + 1 < length && expression.charAt(curPos + 1) == c2) {
			next();
			next();
			return true;
		}
		return false;
	}
	
	private boolean next() {
		curPos++;
		if (curPos < length) {
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
		|| curChar == '+'
		|| curChar == '!'
		|| curChar == '='
		|| curChar == '('
		|| curChar == ')';
	}
}
