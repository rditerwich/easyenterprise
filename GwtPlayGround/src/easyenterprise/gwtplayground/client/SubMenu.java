package easyenterprise.gwtplayground.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class SubMenu extends MenuItem {

	protected final List<MenuItem> children = new ArrayList<MenuItem>();
	protected final MenuRenderer renderer;
	protected final Panel menuPanel;
	protected final boolean collapsable;

	protected SubMenu(SubMenu parent, int parentIndex, Widget widget, boolean collapsable, MenuRenderer renderer) {
		super(parent, parentIndex, widget, renderer);
		this.collapsable = collapsable;
		this.renderer = renderer;
		this.menuPanel = renderer.createMenuPanel(this);
	}
	
	public SubMenu addSubMenu(Widget widget) {
		return addSubMenu(widget, true);
	}
	
	public SubMenu addSubMenu(Widget widget, boolean collapsable) {
		SubMenu submenu = new SubMenu(this, children.size(), widget, collapsable, renderer);
		children.add(submenu);
		menuPanel.add(submenu.widget);
		return submenu;
	}
	
	public MenuItem addMenuItem(Widget widget) {
		MenuItem item = new MenuItem(this, children.size(), widget, renderer);
		children.add(item);
		menuPanel.add(item.widget);
		return item;
	}
	
	public List<MenuItem> getChildren() {
		return Collections.unmodifiableList(children);
	}
	
	public boolean isCollapsable() {
		return collapsable;
	}
	
	public boolean isVisible() {
		return menuPanel.getParent() != null;
	}

	@Override
	public void select() {
		super.select();
		hideChildren();
		show();
		renderer.displayed(this);
	}
	
	protected void hideChildren() {
		
		// find first visible parent
		if (!isVisible() && parent != null) {
			parent.hideChildren();
		}
		// now hide all after this visible submenu
		if (isVisible()) {
			ComplexPanel container = getMenu().container;
			int widgetIndex = container.getWidgetIndex(menuPanel);
			if (widgetIndex != -1) {
				for (int index = container.getWidgetCount() - 1; index > widgetIndex; index--) {
					container.remove(index);
				}
			}
		}
	}
	
	protected void show() {

		if (!isVisible()) {
			
			// show parent as well
			if (parent != null) {
				parent.show();
			}
			
			// and show this menu
			getMenu().container.add(menuPanel);
		}
	}
}
