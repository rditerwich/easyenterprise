package easyenterprise.lib.gwt.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

import easyenterprise.lib.sexpr.DefaultContext;
import easyenterprise.lib.sexpr.SExpr;
import easyenterprise.lib.sexpr.SExprFunction;
import easyenterprise.lib.util.SortedList;

public class SExprExprEditor extends Composite {

	private final SortedList<Item> items;
	private final List<SExprExprEditor> children = new ArrayList<SExprExprEditor>();
	private ListBox listBox;
	private HorizontalPanel parameterWidgets;
	private Item currentItem;
	
	public SExprExprEditor(final SortedList<Item> items) {
		this.items = items;
		initWidget(new HorizontalPanel() {{
			
			add(new ListBox() {{
				listBox = this;
				
				addChangeHandler(new ChangeHandler() {
					public void onChange(ChangeEvent event) {
						currentItem = items.get(getSelectedIndex());
						updateParameters();
					}
				});
			}});
			add(new HorizontalPanel() {{
				parameterWidgets = this;
				
			}});
		}});
		updateItems();
	}

	private void updateItems() {
		listBox.clear();
		for (Item item : items) {
			listBox.addItem(item.toString());
		}
		if (currentItem == null) currentItem = new ChooseItem();
		listBox.setSelectedIndex(items.getIndexOf(currentItem));
		updateParameters();
	}
	
	private void updateParameters() {
		parameterWidgets.clear();
		if (currentItem != null) {
			if ("".equals(currentItem.getName())) {
				parameterWidgets.add(new TextBox() {{
					setText(currentItem.getName());
					addBlurHandler(new BlurHandler() {
						public void onBlur(BlurEvent event) {
							Item item = currentItem.setName(getText());
							items.add(item);
							currentItem = item;
							updateItems();
						}
					});
				}});
			}
			children.clear();
			for (int i = 0; i < currentItem.getParameterCount(); i++) {
				SExprExprEditor editor = new SExprExprEditor(items);
				children.add(editor);
				parameterWidgets.add(editor);
			}
		}
	}
	
	public static SortedList<Item> getItems(DefaultContext context) {
		final SortedList<Item> items = new SortedList<Item>();
		items.add(new ChooseItem());
		for (String con : context.constants) {
			items.add(new ConstItem(con));
		}
		items.add(new VariableItem(""));
		for (String var : context.variables.keySet()) {
			items.add(new VariableItem(var));
		}
		items.add(new VariableItem(""));
		for (SExprFunction fun : context.functions.values()) {
			items.add(new FunctionCallItem(fun));
		}
		return items;
	}
	
	public static abstract class Item implements Comparable<Item> {
		public String getName() {
			return null;
		}
		public int getParameterCount() {
			return 0;
		}
		public Item setName(String name) {
			return this;
		}
		public abstract String getOrder();

		public int compareTo(Item other) {
			return getOrder().compareTo(other.getOrder());
		}
	}
	
	public static class ChooseItem extends Item {
		@Override
		public String getOrder() {
			return "A";
		}
		@Override
		public String toString() {
			return "choose...";
		}
	}
	
	public static class ConstItem extends Item {
		private final String value;
		public ConstItem(String value) {
			this.value = value;
		}
		@Override
		public String getName() {
			return value;
		}
		@Override
		public String getOrder() {
			return "B" + getName();
		}
		@Override
		public String toString() {
			return value.isEmpty() ? "..." : value;
		}
	}
	
	public static class VariableItem extends Item {
		private final String var;
		public VariableItem(String var) {
			this.var = var;
		}
		@Override
		public String getName() {
			return var;
		}
		public VariableItem setName(String name) {
			return new VariableItem(name);
		}
		@Override
		public String getOrder() {
			return "C" + getName();
		}
		@Override
		public String toString() {
			return "#" + (var.isEmpty() ? "..." : var);
		}
	}
	
	public static class FunctionCallItem extends Item {
		private final SExprFunction fun;
		
		FunctionCallItem(SExprFunction fun) {
			this.fun = fun;
		}
		@Override
		public String getOrder() {
			return "D" + toString();
		}
		@Override
		public String toString() {
			return fun.getName() + "(...)";
		}
		@Override
		public int getParameterCount() {
			return fun.getMaxParameters();
		}
	}
}
