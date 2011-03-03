package easyenterprise.lib.gwt.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class EERibbonPanel extends FlowPanel {

	enum Styles { eeRibbonPanel, selected }
	
	public EERibbonPanel() {
		setStylePrimaryName(Styles.eeRibbonPanel.toString());
		addDomHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				EERibbon ribbon = getRibbon();
				if (ribbon != null) {
					ribbon.setCurrent(EERibbonPanel.this);
				}
			}
		}, ClickEvent.getType());
	}

	public boolean isCurrent() {
		EERibbon ribbon = getRibbon();
		return ribbon != null && ribbon.getCurrent() == this;
	}
	
	public void setCurrent(boolean current) {
		EERibbon ribbon = getRibbon();
		if (ribbon != null && (ribbon.getCurrent() == this) != current) {
			ribbon.setCurrent(this);
		}
		if (current) addStyleDependentName(Styles.selected.toString());
		else removeStyleDependentName(Styles.selected.toString());
	}

	private EERibbon getRibbon() {
		for (Widget parent = getParent(); parent != null; parent = parent.getParent()) {
			if (parent instanceof EERibbon) {
				return (EERibbon) parent; 
			}
		}
		return null;
	}
}
