package citykids.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;

public class LoginPage extends Page {

	interface Binder extends UiBinder<HTMLPanel, LoginPage> {}
	private static final Binder binder = GWT.create(Binder.class);

	@UiField Button loginButton;
	
	public LoginPage() {
		initWidget(binder.createAndBindUi(this));
	}

}
