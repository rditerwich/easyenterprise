package considium.subsidieplatform.presentation.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import considium.subsidieplatform.presentation.client.labels.Labels;

public class UserProfilePanel extends VerticalPanel {

	public UserProfilePanel(final User user) {
		setStyleName("Panel");
		setVisible(false);

		add(new Label(Labels.instance.profile()) {{
			setStyleName("PanelHeader");
		}});

		add(new VerticalPanel() {{
			setStyleName("PanelBody");
			if (user != null) {
				String image = user.getImage();
				if (!image.startsWith("http://")) {
					image = "/images/" + image;
				}
				final String finalImage = image;
				add(new HorizontalPanel() {{
					add(new VerticalPanel() {{
						add(new FlowPanel() {{
							setStyleName("Title");
							add(new Label(user.getFirstName() + " " + user.getLastName()));
						}});
						add(new Label(user.getJob() + " at " + user.getOrganization()));
						add(new Label(user.getAddress()));
						add(new Label(user.getPostalcode() + " " + user.getCity()));
						add(new Label(user.getEmail()));
					}});
					add(new Image(finalImage));
				}});
			}
			add(new Hyperlink("profiel aanpassen", "edit"));
		}});
	}
}
