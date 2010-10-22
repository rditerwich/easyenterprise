package citykids;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;

public class PlanningChart extends Composite {
	
	private String header;
	private FlexTable table;
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
			add(new FlowPanel() {{
				add(new FlexTable() {{
					table = this;
					setStyleName("planning-chart");
					getColumnFormatter().setStyleName(0, "left-bar");
					getCellFormatter().setStyleName(0, 0, "header");
				}});
			}});
			add(new FlowPanel() {{
				add(new FlexTable() {{
					table = this;
					setStyleName("planning-chart");
					getColumnFormatter().setStyleName(0, "left-bar");
					getCellFormatter().setStyleName(0, 0, "header");
				}});
			}});
		}});
	}
	
	public void fill() {
		table.setText(0, 0, header);
		int index = 2;
		for (PlanningGroup group : groups) {
			table.getRowFormatter().setStyleName(index, "group-header");
			table.setText(index, 0, group.header);
			for (int i = 0; i < 24; i++) {
				table.setText(index, i + 1, "" + i);
			}
			index++;
			for (PlanningLine line : group.lines) {
				table.setText(index, 0, line.header);
				for (int i = 0; i < 24; i++) {
					table.setText(index, i + 1, "");
					if (index == 3 && i == 5) {
						table.setWidget(index, i + 1, new FlowPanel() {{
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
