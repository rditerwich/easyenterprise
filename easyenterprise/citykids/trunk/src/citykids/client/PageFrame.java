package citykids.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class PageFrame extends Composite {
	interface Binder extends UiBinder<DockLayoutPanel, PageFrame> {}
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField Anchor login;
	@UiField SimplePanel contentPanel;
	
	private Page currentPage;
	
	public PageFrame() {
		initWidget(binder.createAndBindUi(this));
		login.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (currentPage == null) {
					Citykids.loginPage.show();
				} else {
					setCurrentPage(null);
				}
			}
		});
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
			}
			currentPage = page;
		}
	}
}
