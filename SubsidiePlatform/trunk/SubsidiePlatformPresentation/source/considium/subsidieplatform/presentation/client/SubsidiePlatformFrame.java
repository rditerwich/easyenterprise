package considium.subsidieplatform.presentation.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import considium.subsidieplatform.presentation.client.widgets.Page;

public class SubsidiePlatformFrame extends Page {

	DockPanel rootPanel;
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
		centerWidget.setStyleName("PageBody");
	}
	
	
	private void createContent() {
		
		HorizontalPanel headerPanel = new HorizontalPanel();
		headerPanel.add(Images.mboRaadLogo());
		
		MenuBar menu = new MenuBar(false);
//		menu.setStyleName("gwt-MenuBar");
		headerPanel.add(menu);
		
		menu.addItem("HOME", new Command() {
			public void execute() {
				entryPoint.homePage.show();
			}
		});
		menu.addItem("THEMA'S", new Command() {
			public void execute() {
				entryPoint.themaPage.show();
			}
		});
		menu.addItem("ZOEKEN", new Command() {
			public void execute() {
				entryPoint.searchPage.show();
			}
		});
		menu.addItem("COMMUNITY", new Command() {
			public void execute() {
				entryPoint.communityPage.show();
			}
		});
		menu.addItem("NEWS", new Command() {
			public void execute() {
				entryPoint.newsPage.show();
			}
		});
		headerPanel.add(new VerticalPanel() {{
			setStyleName("PageTitle");
			add(new Label("Subsidie"));
			add(new Label("Platform"));
		}});
		
		rootPanel = new DockPanel();
		rootPanel.add(headerPanel, DockPanel.NORTH);
		rootPanel.add(Images.imageBundle.niceImage().createImage(), DockPanel.NORTH);
		((Panel)getWidget()).add(rootPanel);
	}
	
}
