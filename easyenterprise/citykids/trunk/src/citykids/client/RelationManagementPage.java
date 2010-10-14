package citykids.client;

import com.google.gwt.user.client.ui.FlowPanel;

import easyenterprise.lib.gwt.ui.Header;

public class RelationManagementPage extends Page {

	protected Header header;

	public RelationManagementPage() {
		initWidget(new FlowPanel() {{
			add(new Header(1, "Relaties") {{
				header = this;
			}});
		}});
	}
}
