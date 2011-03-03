package easyenterprise.lib.gwt.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.thirdparty.guava.common.base.Preconditions;
import com.google.gwt.user.client.rpc.AsyncCallback;

import easyenterprise.lib.command.PagedCommand;
import easyenterprise.lib.command.gwt.GwtCommandFacade;

public class PagedData<T> {
	private static final int PREFETCH_FACTOR = 2;

	private final int defaultPageSize;

	private boolean lastSeen;
	private int currentPageSize;
	
	private List<Integer> pageOffsets = new ArrayList<Integer>();

	List<T> data = new ArrayList<T>();

	private PagedCommand<T> command;

	private final Listener listener;
	
	public interface Listener {
		void dataAvailable();
	}
	
	public PagedData(int defaultPageSize, Listener listener) {
		this.defaultPageSize = defaultPageSize;
		this.currentPageSize = defaultPageSize;
		pageOffsets.add(0);  // The first page starts at 0.
		
		this.listener = listener;
	}
	
	public void flush() {
		currentPageSize = defaultPageSize; // TODO should we reset??
		pageOffsets.clear();
		pageOffsets.add(0);

		data.clear();
		
		lastSeen = false;
	}
	
	/**
	 * The command used to retrieve more data.
	 * @param command
	 */
	public void setCommand(PagedCommand<T> command) {
		this.command = command;
		flush();
	}
	
	public void previousPage() {
		if (!isFirstPage()) {
			currentPageSize = previousPageSize();
			pageOffsets.remove(pageOffsets.size() - 1); // pop page.
		}
	}
	
	public void nextPage() {
		if (!isLastPage()) {
			pageOffsets.add(currentPageOffset() + currentPageSize);
			
			// Trigger fetch if necessary.
			getSize();
		}
	}

	
	public boolean isFirstPage() {
		return currentPageOffset() == 0;
	}
	
	public boolean isLastPage() {
		return !lastSeen || currentPageOffset() + currentPageSize >= data.size();
	}
	
	public int getCurrentPageSize() {
		return currentPageSize;
	}
	
	public void setCurrentPageSize(int size) {
		this.currentPageSize = size;
	}

	/**
	 * 
	 * @return The number of elements available for the current page.
	 */
	public int getSize() {
		int available = data.size() - currentPageOffset();
		assert available >= 0;
		
		if (lastSeen) {
			return available;
		}
		
		// ensure buffersize > 2xlast page size, always keep one in reserve for lastseen.
		int shortage = PREFETCH_FACTOR * pageSizeHint() + 1 - available;
		if (shortage > 0) {
			fetchMoreData(shortage);
		}
		
		// We always fetch more than necessary to be able to set lastseen.
		if (available > 0) {
			return available - 1;
		} else {
			return available;
		}
	}

	public T get(int relativeIndex) {
		Preconditions.checkElementIndex(relativeIndex, data.size() - currentPageOffset());
		
		return data.get(currentPageOffset() + relativeIndex);
	}

	public void needMore() {
		fetchMoreData(previousPageSize()); // TODO How much to get now? other option: defaultpagesize?
	}
	
	private void fetchMoreData(int nr) {
		Preconditions.checkNotNull(command, "A command is required to fetch data");
		
		// Invoke command to get more.
		final PagedCommand<T> usedCommand = command;
		command.startIndex = data.size();
		command.pageSize = nr;
		GwtCommandFacade.execute(command, new AsyncCallback<PagedCommand.Result<T>>() {
			public void onFailure(Throwable caught) {
				// TODO ??
			}

			public void onSuccess(PagedCommand.Result<T> result) {
				if (usedCommand.equals(command)) {
					lastSeen = result.getResult().size() < usedCommand.pageSize;
					data.addAll(result.getResult());
					listener.dataAvailable();
				}
			}
		});
	}
	
	private int pageSizeHint() {
		return previousPageSize() > 0? previousPageSize() : defaultPageSize;
	}
	
	private int previousPageSize() {
		return currentPageOffset() - previousPageOffset();
	}

	private int currentPageOffset() {
		return pageOffsets.get(pageOffsets.size() - 1);
	}
	
	private int previousPageOffset() {
		if (pageOffsets.size() < 2) {
			return 0;
		}
		return pageOffsets.get(pageOffsets.size() - 2);
	}

}
