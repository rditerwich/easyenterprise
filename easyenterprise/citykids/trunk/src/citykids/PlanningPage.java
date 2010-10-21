package citykids;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;

import easyenterprise.lib.gwt.ui.Header;
import easyenterprise.server.party.entity.Person;

public class PlanningPage extends Page {

	protected Header header;
	private GWTCanvas canvas;

	public PlanningPage() {
		initWidget(new FlowPanel() {{
			final FlowPanel panel = this;
			add(drawPlanning());
			add(new GWTCanvas() {{
				canvas = this;
				setLineWidth(2);
				setStrokeStyle(Color.GREEN);
				beginPath();
				moveTo(1,1);
				lineTo(1,50);
				lineTo(150,50);
				lineTo(150,1);
				closePath();
				stroke();
				beginPath();
				moveTo(0, 0);
				lineTo(panel.getOffsetWidth(), panel.getOffsetHeight());
				lineTo(300, 300);
				closePath();
				stroke();
			}});
		}});
	}
	
	private List<Person> generateMedewerkers() {
		ArrayList<Person> result = new ArrayList<Person>();
		result.add(new Person().setFirstName("Eva").setLastName("Thomson"));
		result.add(new Person().setFirstName("Tom").setLastName("Blokland"));
		return result;
	}
	
	private FlexTable drawPlanning() {
		final List<Person> medewerkers = generateMedewerkers();
		return new FlexTable() {{
			setStyleName("planning");
			getColumnFormatter().setStyleName(0, "left-bar");
			for (int i = 0; i < medewerkers.size(); i++) {
				setText(i, 0, medewerkers.get(i).toString());
				for (int j = 0; j < 20; j++) {
					setText(i, j + 1, "");
				}
			}
		}};
	}
	
}