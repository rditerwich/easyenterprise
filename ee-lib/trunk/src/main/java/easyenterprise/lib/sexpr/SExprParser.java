package easyenterprise.lib.sexpr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SExprParser {

	private String expression;
	private int curPos;
	private char curChar;
	private String value;
	
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
		SExpr result = parseSingle();
		if (result == null) {
			return new Constant(expression, curPos, curPos, "");
		}
		SExpr next = parseSingle();
		if (next != null) {
			List<SExpr> exprs = new ArrayList<SExpr>();
			exprs.add(result);
			for (; next != null; next = parseSingle()) {
				exprs.add(next);
			}
			result = new ConcatExpr(expression, startPos, curPos, exprs);
		}
		return result;
	}
	
	private SExpr parseSingle() throws SExprParseException {
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
		return expr;
	}
	
	private VarRef parseVarRef() throws SExprParseException {
		if (curChar != '#') {
			return null;
		}
		int startPos = curPos;
		next();
		if (!parseWord()) {
			throw new IdentifierOrNumberExpectedException(curPos);
		}
		return new VarRef(expression, startPos, curPos, value);
	}

	private SExpr parseFunctionOrConstant() throws SExprParseException {
		int startPos = curPos;
		if (!parseWord()) {
			return null;
		}
		if (curChar == '(') {
			next();
			return new FunctionCall(expression, startPos, curPos, value, parseParameters());
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
			throw new CharExpectedException(')', curPos);
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
			throw new CharExpectedException(endChar, curPos);
		}
		return curChar != endChar;
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
