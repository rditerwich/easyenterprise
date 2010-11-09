package easyenterprise.gwtplayground.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MenuItem extends Composite {

	protected final SubMenu parent;
	protected final int parentIndex;
	protected final Widget widget;
	protected final MenuRenderer renderer;

	protected MenuItem(SubMenu parent, int parentIndex, Widget widget, MenuRenderer renderer) {
		this.parent = parent;
		this.parentIndex = parentIndex;
		this.renderer = renderer;
		this.widget = renderer.createItemWidget(this, widget);
	}
	
	public boolean isSelected() {
		return getMenu().selectedItem == this;
	}
	
	public void select() {
		if (parent != null) {
			MenuItem[] siblings = new MenuItem[parent.children.size() - 1];
			int index = 0;
			for (MenuItem sibling : parent.children) {
				if (sibling != this) {
					siblings[index++] = sibling;
				}
			}
			renderer.selected(parent, this);
		}
	}
		
	protected Menu getMenu() {
		return parent.getMenu();
	}

}
