package easyenterprise.lib.command.gwt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import easyenterprise.lib.command.PagedCommand;

public abstract class GwtPagedCommandView<T> implements Iterable<T> {

	private final PagedCommand<T> command;
	private final int viewSize;
	private final int defaultFetchSize;
	private final int expandLimit;
	private int startIndex;
	private List<T> results = new ArrayList<T>();
	
	public GwtPagedCommandView(PagedCommand<T> command, int viewSize) {
		this.command = command;
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
		
		return null;
	}
	
	@Override
	public Iterator<T> iterator() {
		return null;
	}

	protected void resultRetrieved(PagedCommand.Result<T> result, int startIndex) {
		this.startIndex = startIndex;
		results.clear();
		results.addAll(result.getResult());
	}
	
	protected abstract void fetch(PagedCommand<T> command);
}
