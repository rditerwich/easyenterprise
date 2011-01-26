package easyenterprise.lib.gwt.client.widgets;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class InlinePanel extends ComplexPanel {

	public InlinePanel(Widget... widgets) {
		this(null, widgets);
	}
	
	public InlinePanel(String sep, Widget... widgets) {
		setElement(DOM.createSpan());
		for (int i = 0; i < widgets.length; i++) {
			if (i > 0 && sep != null) add(new InlineLabel(sep));
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
