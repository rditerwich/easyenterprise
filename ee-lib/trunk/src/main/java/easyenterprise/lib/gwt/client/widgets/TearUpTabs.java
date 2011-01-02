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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class TearUpTabs extends Composite implements RequiresResize, ProvidesResize {

	private final LayoutPanel layoutPanel;
	private final List<TabPanel> tabPanels = new ArrayList<TabPanel>();
	private final double tabHeight;
	private final double tabSpace;
	private int currentTab = -1;
	private int width;
	private Widget mainPanel;

	public TearUpTabs(double tabHeight, double tabSpace) {
		this.tabHeight = tabHeight;
		this.tabSpace = tabSpace;
		initWidget(layoutPanel = new LayoutPanel());
		layoutPanel.setStylePrimaryName("ee-TearUpTabs");
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
			setStylePrimaryName("ee-TearUpTabs-MainPanel");
		}};
		clearWidgets();
	}
	
	public void addTab(Widget tab, double tabWidth, Widget widget) {
		tabPanels.add(new TabPanel(tab, tabWidth, widget));
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
	}
	
	public void hideTab() {
		currentTab = -1;
		renderTabs();
	}
	
	private class TabPanel {
		private final FocusPanel tab;
		private final Widget panel;
		private final double tabWidth;

		public TabPanel(final Widget tab, double tabWidth, final Widget widget) {
			this.tabWidth = tabWidth;
			final int tabNr = tabPanels.size();
			this.panel = new SimplePanel() {{
				setStylePrimaryName("ee-TearUpTabs-Panel");
				add(widget);
			}};
			this.tab = new FocusPanel() {{
				setStylePrimaryName("ee-TearUpTabs-Tab");
				add(tab);
				
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						if (currentTab == tabNr) {
							hideTab();
						} else {
							showTab(tabNr);
						}
						layoutPanel.animate(150);
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
				layoutPanel.setWidgetLeftRight(panel.panel, 5, Unit.PCT, 5, Unit.PCT);
			}
			for (TabPanel panel : tabPanels) {
				layoutPanel.add(panel.tab);
			}
		}
		int height = layoutPanel.getOffsetHeight();
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
				layoutPanel.setWidgetTopHeight(panel.tab, tabHeight, Unit.PX, tabHeight, Unit.PX);
				layoutPanel.setWidgetTopBottom(panel.panel, 2 * tabHeight, Unit.PX, 0, Unit.PX);
			} else {
				layoutPanel.setWidgetTopHeight(panel.tab, height - tabHeight, Unit.PX, tabHeight, Unit.PX);
				layoutPanel.setWidgetBottomHeight(panel.panel, 0, Unit.PX, 0, Unit.PX);
			}
			layoutPanel.setWidgetLeftWidth(panel.tab, left, Unit.PX, panel.tabWidth, Unit.PX);
			left += panel.tabWidth + tabSpace;
		}
	}
}
