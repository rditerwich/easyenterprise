package easyenterprise.lib.gwt.client.widgets;

import com.google.gwt.user.client.ui.HorizontalPanel;

public class EERibbon extends HorizontalPanel {

	enum Styles { eeRibbon }
	
	private EERibbonPanel currentPanel;
	
	public EERibbon() {
		setStylePrimaryName(Styles.eeRibbon.toString());
	}
	
	public EERibbonPanel getCurrent() {
		return currentPanel;
	}
	
	public void setCurrent(EERibbonPanel panel) {
		if (currentPanel != null) {
			currentPanel.setCurrent(false);
		}
	}
}
