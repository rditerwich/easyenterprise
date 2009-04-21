package considium.subsidieplatform.presentation.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import considium.subsidieplatform.presentation.client.widgets.Page;

public class SubsidiePlatformFrame extends Page {

	private DockPanel rootPanel;
	private Widget centerWidget;

	public SubsidiePlatformFrame(EntryPoint entryPoint, String historyToken) {
		super(entryPoint, historyToken);
		createContent();
	}


	public void setCenterWidget(Widget centerWidget) {
		if (this.centerWidget != null) {
			rootPanel.remove(this.centerWidget);
		}
		this.centerWidget = centerWidget;
		rootPanel.add(this.centerWidget, DockPanel.CENTER);
	}
	
	
	private void createContent() {
		
		HorizontalPanel headerPanel = new HorizontalPanel();
		headerPanel.add(Images.mboRaadLogo());
		
		MenuBar menu = new MenuBar(false);
		headerPanel.add(menu);
		
		menu.addItem("home", new Command() {
			public void execute() {
				entryPoint.homePage.show();
			}
		});
		
		rootPanel = new DockPanel();
		rootPanel.add(headerPanel, DockPanel.NORTH);
		((Panel)getWidget()).add(rootPanel);
	}
	
}
