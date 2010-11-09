package easyenterprise.gwtplayground.client;

import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Menu extends SubMenu {

	protected final ComplexPanel container;
	protected MenuItem selectedItem;

	public Menu(Widget widget) {
		this(widget, new MenuRenderer());
	}
	
	public Menu(Widget widget, MenuRenderer renderer) {
		super(null, -1, widget, true, renderer);
		this.container = new HorizontalPanel();
		this.container.add(super.widget);
		super.initWidget(container);
	}
	
	@Override
	protected void initWidget(Widget widget) {
	}
	
	public MenuItem getSelectedItem() {
		return selectedItem;
	}
	
	public void setSelectedItem(MenuItem item) {
		item.select();
	}

	protected Menu getMenu() {
		return this;
	}
}
