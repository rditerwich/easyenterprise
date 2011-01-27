package easyenterprise.lib.gwt.client.widgets;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

public class HtmlTag extends ComplexPanel {

	public HtmlTag(String tag, String text) {
		this(tag);
		setText(text);
	}
	
	public HtmlTag(String tag, Widget... widgets) {
		setElement(Document.get().createElement(tag));
		for (int i = 0; i < widgets.length; i++) {
			add(widgets[i]);
		}
	}
	
  @Override
  public void add(Widget w) {
    add(w, getElement());
  }
  
  public void setText(String text) {
    getElement().setInnerText(text);
  }
}
