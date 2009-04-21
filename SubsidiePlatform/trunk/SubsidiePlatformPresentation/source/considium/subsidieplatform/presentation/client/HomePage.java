package considium.subsidieplatform.presentation.client;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Panel;

import considium.subsidieplatform.presentation.client.widgets.Page;

public class HomePage extends Page {

	public HomePage(EntryPoint entryPoint, String historyToken) {
		super(entryPoint, historyToken);
		createContent();
	}
	
	@Override
	public void show() {
		super.show();
		entryPoint.frame.setCenterWidget(this);
	}

	private void createContent() {
		
		DockPanel rootPanel = new DockPanel();
		rootPanel.add(new LoginPanel(), DockPanel.NORTH);
		
		User user = new User();
		user.setFirstName("Ruud");
		user.setLastName("Diterwich");
		user.setEmail("ruud@diterwich.com");
		user.setImage("http://media.linkedin.com/mpr/mpr/shrink_80_80/p/2/000/000/203/0bb766d.jpg");
		UserProfilePanel profilePanel = new UserProfilePanel();
		profilePanel.user = user;
		
		rootPanel.add(profilePanel, DockPanel.SOUTH);
		
		((Panel)getWidget()).add(rootPanel);
	}
	
}
