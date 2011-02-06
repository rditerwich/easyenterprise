package easyenterprise.lib.command.gwt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GwtPagedCommandView<T> implements Iterable<T> {

	private final int viewSize;
	private final int maxViewSize;
	private int startIndex;
	private List<T> results = new ArrayList<T>();
	
	public GwtPagedCommandView(int viewSize) {
		this.viewSize = viewSize;
		this.maxViewSize = viewSize * 2;
	}
	
	public T getResult(int index) {
		if (index < startIndex + results.size()) {
			return results.get(index - startIndex);
		}
		startIndex = index;
		return null;
	}
	
	@Override
	public Iterator<T> iterator() {
		return null;
	}

}
