package citykids.client;

import com.google.gwt.user.client.ui.Composite;

public abstract class Page extends Composite {

	protected final PageFrame pageFrame;

	public Page(PageFrame pageFrame) {
		this.pageFrame = pageFrame;
	}
	
	public final void show() {
		pageFrame.setCurrentPage(this);
	}
	
	protected void beforeShow() {
	}
	
	protected void afterHide() {
	}
}
