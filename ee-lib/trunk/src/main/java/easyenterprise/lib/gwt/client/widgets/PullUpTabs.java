package easyenterprise.lib.gwt.client.widgets;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.gwt.client.Style;

public class PullUpTabs extends Composite implements RequiresResize, ProvidesResize {
	
	private enum Styles implements Style {
		MainPanel, Tabs, Tab, Panel;
	}
	
	public static final String stylePrefix = "ee-PullUpTabs-";
	
	private final LayoutPanel layoutPanel;
	private final List<TabPanel> tabPanels = new ArrayList<TabPanel>();
	private final double tabHeight;
	private final double tabSpace;
	private int currentTab = -1;
	private int width;
	private Widget mainPanel;

	public PullUpTabs(double tabHeight, double tabSpace) {
		this.tabHeight = tabHeight;
		this.tabSpace = tabSpace;
		initWidget(layoutPanel = new LayoutPanel());
		layoutPanel.setStylePrimaryName(stylePrefix + Styles.Tabs.name());
	}
	
	@Override
	public void onResize() {
		int newWidth = layoutPanel.getOffsetWidth();
		if (width != newWidth) {
			width = newWidth;
			renderTabs();
		}
	}
	
	public void setMainWidget(final Widget widget) {
		mainPanel = new SimplePanel() {{
			add(widget);
			setStylePrimaryName(stylePrefix + Styles.MainPanel.name());
			addDomHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					hideTab();
				}
			}, ClickEvent.getType());
		}};
		clearWidgets();
	}
	
	public void addTab(Widget tab, double tabWidth, Widget widget) {
		addTab(tab, tabWidth, 90, Unit.PCT, widget);
	}
	
	public void addTab(Widget tab, double tabWidth, double panelHeight, Unit panelHeightUnit, Widget widget) {
		tabPanels.add(new TabPanel(tab, tabWidth, panelHeight, panelHeightUnit, widget));
		clearWidgets();
	}

	private void clearWidgets() {
		while (layoutPanel.getWidgetCount() > 0) {
			layoutPanel.remove(0);
		}
	}
	
	public void showTab(int tabNr) {
		this.currentTab = tabNr;
		renderTabs();
		layoutPanel.animate(200);
	}
	
	public void showTab(Widget widget) {
		for (int tabnr = 0; tabnr < tabPanels.size(); tabnr++) {
			if (tabPanels.get(tabnr).widget == widget) {
				showTab(tabnr);
				break;
			}
		}
	}
	
	public void hideTab() {
		currentTab = -1;
		renderTabs();
	}
	
	private class TabPanel {
		private final FocusPanel tab;
		private final ScrollPanel panel;
		private final double tabWidth;
		private final double panelHeight;
		private final Unit panelHeightUnit;
		private final Widget widget;

		public TabPanel(final Widget tab, double tabWidth, double panelHeight, Unit panelHeightUnit, final Widget widget) {
			this.tabWidth = tabWidth;
			this.panelHeight = panelHeight;
			this.panelHeightUnit = panelHeightUnit;
			this.widget = widget;
			final int tabNr = tabPanels.size();
			this.panel = new ScrollPanel() {{
				setStylePrimaryName(stylePrefix + Styles.Panel.name());
				add(widget);
			}};
			this.tab = new FocusPanel() {{
				setStylePrimaryName(stylePrefix + Styles.Tab.name());
				add(tab);
				
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
//						DOM.setStyleAttribute(getElement(), "overflowY", "scroll");
//						DOM.setStyleAttribute(getElement(), "overflowX", "hidden");
						if (currentTab == tabNr) {
							hideTab();
						} else {
							showTab(tabNr);
						}
//						layoutPanel.animate(150);
					}
				});
			}};
		}
	}
	
	private void renderTabs() {
		if (tabPanels.isEmpty() && mainPanel == null) return;
		if (layoutPanel.getWidgetCount() == 0) {
			layoutPanel.add(mainPanel);
			for (TabPanel panel : tabPanels) {
				layoutPanel.add(panel.panel);
				layoutPanel.setWidgetLeftRight(panel.panel, 4, Unit.PCT, 4, Unit.PCT);
			}
			for (TabPanel panel : tabPanels) {
				layoutPanel.add(panel.tab);
			}
		}
		double totalWidth = 0; 
		for (int tabNr = 0; tabNr < tabPanels.size(); tabNr++) {
			TabPanel panel = tabPanels.get(tabNr);
			totalWidth += panel.tabWidth;
			if (tabNr > 0) totalWidth += tabSpace;
		}
		double left = Math.max((layoutPanel.getOffsetWidth() - totalWidth) / 2, 0);
		for (int tabNr = 0; tabNr < tabPanels.size(); tabNr++) {
			TabPanel panel = tabPanels.get(tabNr);
			if (tabNr == currentTab) {
				layoutPanel.setWidgetBottomHeight(panel.tab, panel.panelHeight, panel.panelHeightUnit, tabHeight, Unit.PX);
				layoutPanel.setWidgetBottomHeight(panel.panel, 0, Unit.PX, panel.panelHeight, panel.panelHeightUnit);
				panel.widget.setVisible(true);
			} else {
				layoutPanel.setWidgetBottomHeight(panel.tab, 0, Unit.PX, tabHeight, Unit.PX);
				layoutPanel.setWidgetBottomHeight(panel.panel, 0, Unit.PX, 0, panel.panelHeightUnit);
				panel.widget.setVisible(false);
			}
			layoutPanel.setWidgetLeftWidth(panel.tab, left, Unit.PX, panel.tabWidth, Unit.PX);
			left += panel.tabWidth + tabSpace;
		}
	}
}
