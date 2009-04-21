package considium.subsidieplatform.presentation.client;

import com.google.gwt.user.client.ui.RootPanel;

public class EntryPoint implements com.google.gwt.core.client.EntryPoint {

	public final SubsidiePlatformFrame frame = new SubsidiePlatformFrame(this, "");
	public final HomePage homePage = new HomePage(this, "home");
	
	@Override
	public void onModuleLoad() {
        RootPanel.get(null).add(frame);
        homePage.show();
	}

}
