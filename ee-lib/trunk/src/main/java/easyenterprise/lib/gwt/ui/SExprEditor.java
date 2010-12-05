package easyenterprise.lib.gwt.ui;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
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
		public void onBrowserEvent(com.google.gwt.user.client.Event event) {
			String text = richText.getText();
			int height = 1;
			for (int i = text.length() - 2; i >= 0; i--) {
				System.out.print((int)text.charAt(i) + " ");
				if (text.charAt(i) == '\n') height++;
			}
			System.out.println("Found newlines: " + height);
			richText.setHeight((singleLineHeight*height) + "px");
			parse();
		}
		protected void onLoad() {
			this.singleLineHeight = getOffsetHeight();
			System.out.println(singleLineHeight);
		};
	};
	
	private final RichTextArea richText2 = new RichTextArea();
	private final FlowPanel formattedPanel = new FlowPanel();
	private final Label errorLabel = new Label();
	
	public SExprEditor() {
		richText.setHeight("2em");
		richText.setHTML("<b>12++12</b>");
		richText.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				richText.getFormatter().selectAll();
				richText.getFormatter().removeFormat();
			}
		});
		richText.addBlurHandler(new BlurHandler() {
			public void onBlur(BlurEvent event) {
				
			}
		});
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
}
