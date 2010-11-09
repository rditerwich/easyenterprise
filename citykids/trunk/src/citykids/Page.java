package citykids;

import com.google.gwt.user.client.ui.Composite;

public abstract class Page extends Composite {

	public final void show() {
		Citykids.pageFrame.setCurrentPage(this);
	}
	
	protected void beforeShow() {
	}
	
	protected void afterHide() {
	}
}
