package easyenterprise.lib.gwt.ui;


import com.bfr.client.selection.Range;
import com.bfr.client.selection.RangeEndPoint;
import com.bfr.client.selection.Selection;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Text;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RichTextArea;

import easyenterprise.lib.sexpr.SExpr;
import easyenterprise.lib.sexpr.SExprParseException;
import easyenterprise.lib.sexpr.SExprParser;

public class SExprEditor extends Composite {

	
	private final RichTextArea richText = new RichTextArea() {
		public int singleLineHeight = 10;
		private String lastText = "";
		public void onBrowserEvent(com.google.gwt.user.client.Event event) {
			super.onBrowserEvent(event);
			if (event.getTypeInt() == Event.ONKEYUP) {
				String text = richText.getText();
				if (!text.equals(lastText)) {
					lastText = text;
					changed();
				}
			}
			if (true) return;
			String text = richText.getText();
			if (text.equals(lastText)) return;
			lastText = text;
			int height = 1;
			for (int i = text.length() - 2; i >= 0; i--) {
//				System.out.print((int)text.charAt(i) + " ");
				if (text.charAt(i) == '\n') height++;
			}
			System.out.println("Found newlines: " + height);
			//richText.setHeight((singleLineHeight*height) + "px");
			//System.out.println();
			RangeEndPoint pos = getSelection().getRange().getStartPoint();
			try {
				SExpr expr = new SExprParser().parse(richText.getText());
				System.out.println(expr.toHtml());
//				richText.setText(text)HTML(expr.toHtml());
				
			} catch (SExprParseException e) {
				System.out.println(e.toHtml());
//				richText.setHTML(e.toHtml());
			}
			System.out.println("Setting cursor to " + pos.getOffset());
			RangeEndPoint endPoint = new RangeEndPoint();
			endPoint.setOffset(2);
			pos.setOffset(2);
			getSelection().getRange().setStartPoint(pos);
			parse();
		}
		
		private void changed() {
			findCursorPos(getSelection().getRange().getCursor());
			
			RangeEndPoint cursor = getSelection().getRange().getCursor();
			Text textNode = cursor.getTextNode();
			System.out.println("on change " + cursor.getOffset());
			Range range = new Range(getSelection().getDocument());
			RangeEndPoint startPoint = new RangeEndPoint();
//					startPoint.setOffset(2);
//					range.getStartPoint().setOffset(2);
			cursor.setOffset(2);
			cursor.setTextNode(textNode);
			range.setCursor(cursor);
			getSelection().setRange(range);
		}
		protected void onLoad() {
			this.singleLineHeight = getOffsetHeight();
			System.out.println(singleLineHeight);
		};
	};
	
	private int findCursorPos(RangeEndPoint rangeEndPoint) {
		System.out.println(rangeEndPoint.getTextNode().getParentElement().getParentElement().getParentElement());
		System.out.println(getSelection().getDocument().getBody());
		return 0;
	}
	
	private final RichTextArea richText2 = new RichTextArea();
	private final FlowPanel formattedPanel = new FlowPanel();
	private final Label errorLabel = new Label();
	
	public SExprEditor() {
		richText.setHeight("2em");
		richText.setHTML("<b>12++12</b><i>HI</i>");
		richText.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
//				richText.getFormatter().selectAll();
//				richText.getFormatter().removeFormat();
			}
		});
		richText.addBlurHandler(new BlurHandler() {
			public void onBlur(BlurEvent event) {
				
			}
		});

//		richText.addKeyPressHandler(new KeyPressHandler() {
//			public void onKeyPress(KeyPressEvent event) {
//				System.out.println("on change");
//				Range range = getSelection().getRange();
//				RangeEndPoint pos = range.getStartPoint();
//				RangeEndPoint endPoint = new RangeEndPoint();
//				endPoint.setOffset(3);
//				pos.setOffset(3);
//				System.out.println("Setting cursor to " + pos.getOffset());
//				getSelection().setRange(new Range(getSelection().getDocument()));
//
//			}
//		});
		initWidget(new FlowPanel() {{
			add(new FlowPanel() {{
				add(richText);
			}});
			add(richText2);
			add(formattedPanel);
			add(errorLabel);
		}});
//		richText.setHTML("<html><b><span style=\"color:red\" class=\"sexpr-error\">This is a problem</span></b></html>");
	}
	
	private void parse() {
		formattedPanel.clear();
		try {
			richText.getFormatter().removeFormat();
			errorLabel.setText("OK");
			SExpr expr = new SExprParser().parse(richText.getText());
			richText2.setHTML(expr.toHtml());
			formattedPanel.add(new HTMLPanel(expr.toHtml()));
			
		} catch (SExprParseException e) {
			formattedPanel.add(new HTMLPanel(e.toHtml()));
			errorLabel.setText(e.getMessage());
		}
	}
	
	private Selection getSelection() {
		Selection res = null;
		try {
			JavaScriptObject window = getWindow();
			res = Selection.getSelection(window);
		} catch (Exception ex) {
			GWT.log("Error getting the selection", ex);
		}
		return res;
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
