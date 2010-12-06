package easyenterprise.lib.gwt.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

import easyenterprise.lib.sexpr.DefaultContext;
import easyenterprise.lib.sexpr.FunctionCall;
import easyenterprise.lib.sexpr.SExpr;

public class SExprExprEditor extends Composite {

	private String name;
	private List<SExpr> children = new ArrayList<SExpr>();
	private HorizontalPanel parameterWidgets;
	private Item currentItem;
	
	public SExprExprEditor(final List<Item> items) {
		initWidget(new HorizontalPanel() {{
			
			setElement(DOM.createSpan());
			add(new ListBox() {{
				addItem("choose...");
				for (Item item : items) {
					addItem(item.toString());
					
				}
				addChangeHandler(new ChangeHandler() {
					public void onChange(ChangeEvent event) {
						currentItem = items.get(getSelectedIndex());
						updateParameters();
					}
				});
			}});
			add(new HorizontalPanel() {{
				parameterWidgets = this;
				setElement(DOM.createSpan());
				
			}});
		}});
	}
	
	private void updateParameters() {
		parameterWidgets.clear();
		if (currentItem != null) {
			if (currentItem.requestName()) {
				parameterWidgets.add(new TextBox() {{
					setText(name);
				}});
			}
		}
	}
	
	public static List<Item> getItems(DefaultContext context) {
		final List<SExprExprEditor.Item> items = new ArrayList<SExprExprEditor.Item>();
		return items;
	}
	
	public static class Item {
		public boolean requestName() {
			return false;
		}
	}
	
	public static class UnknownVariable extends Item {
		@Override
		public boolean requestName() {
			return true;
		}
		@Override
		public String toString() {
			return "#...";
		}
	}
	
	public static 	class FunctionCallItem extends Item {
		private final FunctionCall fun;
		
		FunctionCallItem(FunctionCall fun) {
			this.fun = fun;
		}
		@Override
		public String toString() {
			return fun.name + "(...)";
		}
	}
}
