package considium.subsidieplatform.presentation.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

import considium.subsidieplatform.presentation.client.widgets.Page;

public class CommunityPage extends Page {

	public CommunityPage(EntryPoint entryPoint, String historyToken) {
		super(entryPoint, historyToken);
		createContent();
	}
	
	@Override
	public void show() {
		super.show();
		entryPoint.frame.setCenterWidget(this);
	}

	private void createContent() {
		
		FlowPanel rootPanel = new FlowPanel() {{

			add(new HorizontalPanel() {{
				add(new VerticalPanel() {{
					setStyleName("Panel");
					add(new Label("Community Updates") {{ setStyleName("Title"); }});
					add(new HorizontalPanel() {{
						add(new Hyperlink("Bart Vreedevoogd", "bart"));
						add(new Label(" heeft de subsidie toegewezen gekregen: "));
						add(new Hyperlink("WBSO", "wbso"));
					}});
					add(new HorizontalPanel() {{
						add(new Hyperlink("Bart Vreedevoogd", "bart"));
						add(new Label(" heeft de subsidie toegewezen gekregen: "));
						add(new Hyperlink("WBSO", "wbso"));
					}});
				}});
				add(Images.communityBar());
			}});
		}};
		
		
		((Panel)getWidget()).add(rootPanel);
	}
	
}
