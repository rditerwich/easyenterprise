package considium.subsidieplatform.presentation.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;

import considium.subsidieplatform.presentation.client.widgets.Page;

public class ThemaPage extends Page {

	public ThemaPage(EntryPoint entryPoint, String historyToken) {
		super(entryPoint, historyToken);
		createContent();
	}
	
	@Override
	public void show() {
		super.show();
		entryPoint.frame.setCenterWidget(this);
	}

	private void createContent() {
		
		FlowPanel rootPanel = new FlowPanel();
		rootPanel.add(Images.themas());
		
		((Panel)getWidget()).add(rootPanel);
	}
	
}
