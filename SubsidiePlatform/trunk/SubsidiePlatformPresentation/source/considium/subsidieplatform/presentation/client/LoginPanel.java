package considium.subsidieplatform.presentation.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import considium.subsidieplatform.presentation.client.labels.Labels;

public class LoginPanel extends VerticalPanel {

	TextBox userBox = new TextBox(); 
	PasswordTextBox passwordBox = new  PasswordTextBox(); 
	
	public LoginPanel(final EntryPoint entryPoint, final Widget widgetToShow) {
		setStyleName("Panel");
		Label headerLabel = new Label(Labels.instance.login());
		headerLabel.setStyleName("PanelHeader");
		add(headerLabel);
		Grid grid = new Grid(2, 2); 
		grid.setStyleName("PanelBody");
		grid.setWidget(0, 0, new Label(Labels.instance.user()));
		grid.setWidget(0, 1, userBox);
		grid.setWidget(1, 0, new Label(Labels.instance.password()));
		grid.setWidget(1, 1, passwordBox);
		Button button = new Button("login");
		add(grid);
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				LoginPanel.this.setVisible(false);
				widgetToShow.setVisible(true);
				entryPoint.frame.rootPanel.add(Images.imageBundle.leftMenu().createImage(), DockPanel.WEST);
			}
			
		});
		add(button);
	}
}
