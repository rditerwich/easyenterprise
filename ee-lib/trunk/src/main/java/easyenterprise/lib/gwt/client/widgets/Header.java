package easyenterprise.lib.gwt.client.widgets;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class Header extends Widget implements HasText {

	public Header(int level, String text) {
		setElement(Document.get().createHElement(level));
		setText(text);
	}
	
  public void setText(String text) {
    getElement().setInnerText(text);
  }

	@Override
	public String getText() {
		return getElement().getInnerText();
	}
}
