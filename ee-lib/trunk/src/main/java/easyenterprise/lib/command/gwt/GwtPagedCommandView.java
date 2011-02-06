package easyenterprise.lib.command.gwt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GwtPagedCommandView<T> implements Iterable<T> {

	private final int viewSize;
	private final int defaultFetchSize;
	private final int expandLimit;
	private int startIndex;
	private List<T> results = new ArrayList<T>();
	
	public GwtPagedCommandView(int viewSize) {
		this.viewSize = viewSize;
		this.defaultFetchSize = viewSize * 2;
		this.expandLimit = viewSize / 3;
	}
	
	public T getResult(int index) {
		int endIndex = startIndex + results.size();
		if (index < endIndex) {
			return results.get(index - startIndex);
		}
		int fetchStart = index;
		int fetchSize = this.defaultFetchSize;
		if (index - endIndex <= expandLimit) {
			fetchStart = endIndex;
		} else if (startIndex - (index + fetchSize) <= expandLimit) {
			fetchSize = startIndex - index;
		}
		startIndex = index;
		
		return null;
	}
	
	@Override
	public Iterator<T> iterator() {
		return null;
	}

}
