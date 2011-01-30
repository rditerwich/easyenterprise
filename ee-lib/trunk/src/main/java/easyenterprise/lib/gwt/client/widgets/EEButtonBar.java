package easyenterprise.lib.gwt.client.widgets;

import com.google.gwt.user.client.ui.HorizontalPanel;

import easyenterprise.lib.gwt.client.Style;

public class EEButtonBar extends HorizontalPanel {

	enum Styles implements Style { ButtonBar; 
		
		public String toString() {
			return "ee-" + super.toString();
		}
	} 

	public EEButtonBar() {
		setStylePrimaryName(Styles.ButtonBar.toString());
	}
}
