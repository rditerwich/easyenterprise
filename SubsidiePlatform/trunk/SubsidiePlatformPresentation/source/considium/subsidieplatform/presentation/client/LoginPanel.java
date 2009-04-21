package considium.subsidieplatform.presentation.client;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import considium.subsidieplatform.presentation.client.labels.Labels;

public class LoginPanel extends VerticalPanel {

	TextBox userBox = new TextBox(); 
	PasswordTextBox passwordBox = new  PasswordTextBox(); 
	
	public LoginPanel() {
		add(new Label(Labels.instance.login()));
		Grid grid = new Grid(2, 2); 
		grid.setWidget(0, 0, new Label(Labels.instance.user()));
		grid.setWidget(0, 1, userBox);
		grid.setWidget(1, 0, new Label(Labels.instance.password()));
		grid.setWidget(1, 1, passwordBox);
		add(grid);
		
	}
}
