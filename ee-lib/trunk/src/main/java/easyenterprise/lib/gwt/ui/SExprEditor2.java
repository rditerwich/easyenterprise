package easyenterprise.lib.gwt.ui;

import java.util.HashMap;
import java.util.Map;

import com.bfr.client.selection.Range;
import com.bfr.client.selection.RangeEndPoint;
import com.bfr.client.selection.Selection;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.RichTextArea;

import easyenterprise.lib.sexpr.BinaryExpression;
import easyenterprise.lib.sexpr.Constant;
import easyenterprise.lib.sexpr.FunctionCall;
import easyenterprise.lib.sexpr.SExpr;
import easyenterprise.lib.sexpr.SExprParseException;
import easyenterprise.lib.sexpr.SExprParser;
import easyenterprise.lib.sexpr.VarRef;

public class SExprEditor2 extends RichTextArea {
	
	private String lastText;
	
	public static final Map<Class<? extends SExpr>, String> defaultStyles = new HashMap<Class<? extends SExpr>, String>() {{
		put(SExpr.class, "font-weight:bold;color:black");
		put(BinaryExpression.class, "font-weight:bold;color:#990099");
		put(FunctionCall.class, "font-weight:bold;color:#990099");
		put(VarRef.class, "font-weight:bold;color:blue");
		put(Constant.class, "font-weight:bold;color:black");
	}};
	
	public SExprEditor2() {
		setHTML("<b>Constant</b> ++ <i>#variable</i>");
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
		if (event.getTypeInt() == Event.ONKEYUP) {
			String text = getText();
			if (!text.equals(lastText)) {
				lastText = text;
				onChanged();
			}
		}	
	}
	
	private void onChanged() {
		BodyElement body = getSelection().getDocument().getBody();
		RangeEndPoint startPoint = getSelection().getRange().getStartPoint();
		StringBuilder text = new StringBuilder();
		int[] pos = new int[1];
//		System.out.println("Start point: "+ startPoint.getTextNode() + " offset " + startPoint.getOffset());
//		System.out.println("Body: "+ body);
		parseHtml(body, startPoint, text, pos);
		try {
//			System.out.println("old: " + text.toString());
			SExpr expr = new SExprParser().parse(text.toString());
			final String newHtml = expr.toHtml(defaultStyles);
//			System.out.println("new: " + newHtml);
//			setHTML(newHtml);
			setPosition(getSelection().getDocument().getBody(), pos[0]);
		} catch (SExprParseException e) {
			e.printStackTrace();
		}
		
//		System.out.println(text + " at " + pos[0]);
	}

	private void parseHtml(Element parent, RangeEndPoint startPoint, StringBuilder text, int[] pos) {
		for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
			if (node.getNodeType() == Node.TEXT_NODE) {
				if (node == startPoint.getTextNode()) {
					pos[0] = text.length() + startPoint.getOffset();
				}
				text.append(((Text) node).getData()); 
			}
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.getTagName().toLowerCase().equals("div")) {
					text.append('\n');
				}
				parseHtml(element, startPoint, text, pos);
			}
		}
	}
	
	private int setPosition(Element parent, int pos) {
//		System.out.println("parent: " + (parent.getParentNode().getParentNode() == getSelection().getDocument()));
		for (Node node = parent.getFirstChild(); node != null && pos >= 0; node = node.getNextSibling()) {
			if (node.getNodeType() == Node.TEXT_NODE) {
				Text textNode = (Text) node;
				int length = textNode.getData().length();
				if (pos < length) {
try {
		
						Range range = new Range(parent);
						range.setCursor(new RangeEndPoint(textNode, 3));
						getSelection().setRange(range);
	System.out.println("RANGE: " + getSelection().getRange().getCursor().getTextNode());
	//					System.out.println("SMAE: " + (textNode.getOwnerDocument() == getSelection().getRange().getDocument()));
	//					range.setCursor(new RangeEndPoint(textNode, 1));
	//					getSelection().getRange().surroundContents(textNode.getParentElement());					
						return -1;
} catch (Throwable e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
				}
				pos -= length;
			}
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.getTagName().toLowerCase().equals("div")) {
					pos--;
				}
				pos = setPosition(element, pos);
			}
		}
		return pos;
	}
	
	private Selection getSelection() {
		try {
			JavaScriptObject window = getWindow();
			return Selection.getSelection(window);
		} catch (Exception ex) {
			GWT.log("Error getting the selection", ex);
			return null;
		}
	}

	private JavaScriptObject getWindow() {
		IFrameElement frame = getElement().cast();
		return getWindow(frame);
	}

	public static native JavaScriptObject getWindow(IFrameElement iFrame)
	/*-{
	var iFrameWin = iFrame.contentWindow || iFrame.contentDocument;

	if( !iFrameWin.document ) 
	{
	  iFrameWin = iFrameWin.getParentNode(); //FBJS version of parentNode
	}
	return iFrameWin;
	}-*/;
}
