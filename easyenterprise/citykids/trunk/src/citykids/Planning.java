package citykids;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;

public class Planning extends Composite {
	
	public Planning() {
		initWidget(new FlexTable() {{
			setStyleName("planning");
			getColumnFormatter().setStyleName(0, "left-bar");
			for (int i = 0; i < medewerkers.size(); i++) {
				setText(i, 0, medewerkers.get(i).toString());
				for (int j = 0; j < 20; j++) {
					setText(i, j + 1, "");
				}
			}
		}});
	}
	on
}
