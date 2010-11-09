package easyenterprise.gwtplayground.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;


public class MenuDemo {

	 public void onModuleLoad() {
		Menu menu = new Menu(widget("EasyEnterprise", null));
		SubMenu crm = menu.addSubMenu(widget("crm", new Image("crm-small.png")));
		crm.addMenuItem(widget("parties", null));
		crm.addMenuItem(widget("relations", null));
		crm.addMenuItem(widget("contacts", null));
		SubMenu accounting = menu.addSubMenu(widget("accounting", new Image("accounting-small.png")));
		SubMenu mappen = accounting.addSubMenu(widget("mappen", new Image("accounting-brown-small.png")));
		mappen.addMenuItem(widget("inkoop", null));
		mappen.addMenuItem(widget("verkoop", null));
		mappen.addMenuItem(widget("salarissen", null));
		SubMenu aangiftes = accounting.addSubMenu(widget("aangiftes", new Image("accounting-green-small.png")));
		aangiftes.addMenuItem(widget("BTW", null));
		aangiftes.addMenuItem(widget("Loonbelasting", null));
		RootPanel.get().add(menu);
	 }

	 public Widget widget(String text, Image image) {
		 HorizontalPanel panel = new HorizontalPanel();
		 if (image != null) {
			 panel.add(image);
		 }
		 Label label = new Label(text);
		 DOM.setStyleAttribute(label.getElement(), "fontFamily", "Verdana");
		 DOM.setStyleAttribute(label.getElement(), "fontWeight", "bold");
		 DOM.setStyleAttribute(label.getElement(), "fontSize", "10pt");
		 panel.add(label);
		 DOM.setStyleAttribute(panel.getElement(), "padding", "4px");
		 DOM.setStyleAttribute(panel.getElement(), "paddingRight", "4px");
		 return panel;
	 }
}
