package easyenterprise.lib.gwt.client.widgets;

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
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.sexpr.BinaryExpression;
import easyenterprise.lib.sexpr.Constant;
import easyenterprise.lib.sexpr.FunctionCall;
import easyenterprise.lib.sexpr.If;
import easyenterprise.lib.sexpr.SExpr;
import easyenterprise.lib.sexpr.SExprParseException;
import easyenterprise.lib.sexpr.SExprParser;
import easyenterprise.lib.sexpr.VarRef;

public class SExprEditor extends Composite {
	
	private VerticalPanel panel;
	private RichTextArea richText;
	private InlineHTML statusText;
	private HTML hiddenDiv;
	private String lastText;
	
	@SuppressWarnings("serial")
	public static final SExpr.Styles defaultStyles = new SExpr.Styles() {{
		put(SExpr.class, "style=\"font-weight:bold;color:red\"");
		put(BinaryExpression.class, "style=\"font-weight:bold;color:#990099\"");
		put(FunctionCall.class, "style=\"font-weight:bold;color:#990099\"");
		put(If.class, "style=\"font-weight:bold;color:#990099\"");
		put(VarRef.class, "style=\"font-weight:bold;color:blue\"");
		put(Constant.class, "style=\"font-weight:bold;color:black\"");
	}};
	
	public <richText> SExprEditor() {
		initWidget(panel = new VerticalPanel() {{
			add(richText = new RichTextArea() {{
				IFrameElement e = IFrameElement.as(getElement());
				e.setMarginHeight(0);
				e.setMarginWidth(0);

				addKeyUpHandler(new KeyUpHandler() {
					public void onKeyUp(KeyUpEvent event) {
						String text = richText.getText();
						if (!text.equals(lastText)) {
							lastText = text;
							onChanged();
						}
					}
				});
				addBlurHandler(new BlurHandler() {
					public void onBlur(BlurEvent event) {
						setExpression(richText.getText());
					}
				});
				add(hiddenDiv = new HTML() {{
					getElement().setAttribute("style", "visibility:hidden;position:absolute");
				}});
			}});
		}});
	}

	public void setExpression(String expression) {
		try {
			SExpr expr = new SExprParser().parse(expression);
			String html = expr.toHtml(true, defaultStyles);
			setEditorHeight();
			richText.setHTML(html);
			setStatus("");
		} catch (SExprParseException e) {
			richText.setHTML(expression);
			setStatus(e.toHtml(defaultStyles));
		}
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		setEditorHeight();
	}

	private void setEditorHeight() {
		String html = richText.getHTML();
		hiddenDiv.setHTML(html.isEmpty() ? "A" : html);
		richText.setHeight((4 + hiddenDiv.getOffsetHeight()) + "px");
	}
	
	private void onChanged() {
		BodyElement body = getSelection().getDocument().getBody();
		RangeEndPoint startPoint = getSelection().getRange().getStartPoint();
		int[] pos = new int[1];
		String text = parseHtml(body, startPoint, new StringBuilder(), pos).toString();
		char lastChar = text.isEmpty() ? 0 : text.charAt(text.length() - 1);
		try {
			SExpr expr = new SExprParser().parse(text.toString());
			String newHtml = expr.toHtml(true, defaultStyles);
			if (SExprParser.isSpace(lastChar)) newHtml += "&nbsp;";
			if (pos[0] == text.length()) {
				richText.getFormatter().selectAll();
				richText.getFormatter().insertHTML(newHtml);
				System.out.println(richText.getElement().getChildCount());
				setEditorHeight();
			}
			setStatus("");
//			setPosition(getSelection().getDocument().getBody(), pos[0]);
		} catch (SExprParseException e) {
			setStatus(e.toHtml(defaultStyles));
			setEditorHeight();
		}
	}
	
	private void setStatus(String html) {
		if (statusText != null) {
			statusText.removeFromParent();
		}
		if (!html.trim().isEmpty()) {
			statusText = new InlineHTML(html);
			panel.add(statusText);
		}
	}

	private StringBuilder parseHtml(Element parent, RangeEndPoint startPoint, StringBuilder text, int[] pos) {
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
				if (element.getTagName().toLowerCase().equals("br")) {
					text.append('\n');
				}
				parseHtml(element, startPoint, text, pos);
			}
		}
		return text;
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
						System.out.println("RANGE: " + getSelection().getRange().getCursor().getTextNode());
						getSelection().setRange(range);
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
		IFrameElement frame = richText.getElement().cast();
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