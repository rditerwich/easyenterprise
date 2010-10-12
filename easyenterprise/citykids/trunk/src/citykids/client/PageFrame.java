package citykids.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class PageFrame extends Composite {
	interface Binder extends UiBinder<DockLayoutPanel, PageFrame> {}
	private static final Binder binder = GWT.create(Binder.class);
	
	private SimplePanel contentPanel;
	private Page currentPage;
	
	public PageFrame() {
		initWidget(binder.createAndBindUi(this));
	}
	
	void setCurrentPage(Page page) {
		if (currentPage != page) {
			if (currentPage != null) {
				currentPage.removeFromParent();
				currentPage.afterHide();
			}
			if (page != null) {
				page.beforeShow();
				contentPanel.add(page);
				currentPage = page;
			}
		}
	}
}
