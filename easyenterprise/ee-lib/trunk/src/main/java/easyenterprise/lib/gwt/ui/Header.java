package easyenterprise.lib.gwt.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Widget;

public class Header extends Widget {

	public Header(int level, String text) {
		setElement(Document.get().createHElement(level));
		setText(text);
	}
	
  public void setText(String text) {
    getElement().setInnerText(text);
  }
}
