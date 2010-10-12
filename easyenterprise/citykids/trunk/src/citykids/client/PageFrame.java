package citykids.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;

public class PageFrame extends Composite {
	interface Binder extends UiBinder<DockLayoutPanel, PageFrame> {}
	private static final Binder binder = GWT.create(Binder.class);
	
	public PageFrame() {
		initWidget(binder.createAndBindUi(this));
	}
}
