package considium.subsidieplatform.presentation.client;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import considium.subsidieplatform.presentation.client.labels.Labels;

public class UserProfilePanel extends VerticalPanel {

	User user;
	TextBox userBox = new TextBox(); 
	PasswordTextBox passwordBox = new  PasswordTextBox(); 
	
	@Override
	protected void onAttach() {
		add(new Label(Labels.instance.login()));
		FlexTable table = new FlexTable();
		
		if (user != null) {
			String image = user.getImage();
			if (!image.startsWith("http://")) {
				image = "/images/" + image;
			}
			table.setWidget(0, 0, new Image(image));
			table.setWidget(0, 1, new Label(user.getFirstName() + " " + user.getLastName()));
			table.setWidget(1, 0, new Label(user.getEmail()));
			table.getFlexCellFormatter().setRowSpan(0, 0, 3);
			table.setBorderWidth(1);
		}
		
		add(table);
		super.onAttach();
	}
}
