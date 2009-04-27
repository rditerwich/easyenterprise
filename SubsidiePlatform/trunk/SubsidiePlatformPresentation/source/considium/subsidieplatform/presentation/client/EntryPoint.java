package considium.subsidieplatform.presentation.client;

import com.google.gwt.user.client.ui.RootPanel;

public class EntryPoint implements com.google.gwt.core.client.EntryPoint {

	public final SubsidiePlatformFrame frame = new SubsidiePlatformFrame(this, "");
	public final HomePage homePage = new HomePage(this, "home");
	public final ThemaPage themaPage = new ThemaPage(this, "thema");
	public final SearchPage searchPage = new SearchPage(this, "search");
	public final CommunityPage communityPage = new CommunityPage(this, "community");
	public final NewsPage newsPage = new NewsPage(this, "news");
	
	@Override
	public void onModuleLoad() {
        RootPanel.get(null).add(frame);
        homePage.show();
	}

}
