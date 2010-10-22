package citykids;

import java.util.ArrayList;
import java.util.List;

public class PlanningGroup {

	final String header;
	final List<PlanningLine> lines;
	int startIndex;
	
	public PlanningGroup(String header) {
		this.header = header;
		this.lines = new ArrayList<PlanningLine>();
	}

	protected void add(PlanningLine line) {
		lines.add(line);
	}
}
