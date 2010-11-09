package citykids;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;

public class PlanningChart extends Composite {
	
	private String header;
	private FlexTable tableLeft;
	private FlexTable tableRight;
	private Date fromDate;
	private Date toDate;
	
	private final List<PlanningGroup> groups;

	public String getHeader() {
		return header;
	}
	
	public void setHeader(String header) {
		this.header = header;
	}
	
	public PlanningChart(String header) {
		this.header = header;
		groups = new ArrayList<PlanningGroup>();
		initWidget(new FlowPanel() {{
			setStyleName("planning-chart");
			add(new FlowPanel() {{
				setStyleName("left");
				add(new FlexTable() {{
					tableLeft = this;
				}});
			}});
			add(new FlowPanel() {{
				setStyleName("right");
				add(new FlexTable() {{
					tableRight = this;
				}});
			}});
		}});
	}
	
	public void fill() {
		tableLeft.setText(0, 0, header);
		int index = 2;
		for (int i = 0; i < 24; i++) {
			tableRight.setText(index, i, "" + i);
		}
		for (PlanningGroup group : groups) {
			tableLeft.getRowFormatter().setStyleName(index, "group-header");
			tableLeft.setText(index, 0, group.header);
			index++;
			for (PlanningLine line : group.lines) {
				tableLeft.setText(index, 0, line.header);
				for (int i = 0; i < 24; i++) {
					tableRight.setText(index, i, "");
					if (index == 3 && i == 5) {
						tableRight.setWidget(index, i + 1, new FlowPanel() {{
							setStyleName("bar");
						}});
					}
				}
				index++;
			}
		}
	}
	
	public void add(PlanningGroup group) {
		this.groups.add(group);
		calculateStartIndices();
	}
	
	private void calculateStartIndices() {
		int startIndex = 2;
		for (PlanningGroup group : groups) {
			group.startIndex = startIndex;
			startIndex += 1 + group.lines.size();
		}
	}
}
