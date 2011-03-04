package easyenterprise.lib.gwt.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.thirdparty.guava.common.base.Preconditions;
import com.google.gwt.user.client.rpc.AsyncCallback;

import easyenterprise.lib.command.PagedCommand;
import easyenterprise.lib.command.gwt.GwtCommandFacade;

/**
 * Represents data that is retrieved using a command as pages.  The size of the page can either be static (initialized with <code>defaultPageSize</code>) or more dynamic
 * where the user sets the page size where appropriate.  
 * 
 * A usage scenario with contant page sizes:
 * <pre>
 *pagedData = new PagedData(PAGESIZE, this);
 * 	
 *void dataChanged() {
 *    for (int i = 0; i < pagedData.getSize(); i++) {
 *        T data = pagedData.get(i);
 * 
 *        // ... use data.
 *    }
 *}
 * </pre>
 * 
 * A usage scenario with varying page sizes:
 * <pre>
 *pagedData = new PagedData(ESTIMATED_PAGESIZE, this);
 * 	
 *void dataChanged() {
 *    for (int i = 0; i < pagedData.getBufferSize(); i++) {
 *        T data = pagedData.get(i);
 * 
 *        // ... use data.
 *          
 *        if (pageFull()) {
 *            pagedData.setSize(i);
 *            break;
 *        }
 *    }
 * 
 *    if (!pageFull()) {
 *        pagedData.requestMore();
 *    }
 *}
 * </pre>
 * 
 * All non-getters except {@link #setSize(int)} can cause one or more dataChanged calls on the listener.
 * @author reinier
 *
 * @param <T>
 */
public class PagedData<T> {
	private static final int PREFETCH_FACTOR = 2;

	private final int defaultPageSize;

	private boolean lastSeen;
	private int size;
	
	private List<Integer> pageOffsets = new ArrayList<Integer>();

	List<T> data = new ArrayList<T>();

	private PagedCommand<T> command;

	private final Listener listener;
	
	public interface Listener {
		void dataChanged();
	}
	
	public PagedData(int defaultPageSize, Listener listener) {
		Preconditions.checkNotNull(listener);
		
		this.defaultPageSize = defaultPageSize;
		this.size = defaultPageSize;
		pageOffsets.add(0);  // The first page starts at 0.
		
		this.listener = listener;
	}
	
	public void flush() {
		size = defaultPageSize; // TODO should we do this??
		pageOffsets.clear();
		pageOffsets.add(0);

		data.clear();
		
		lastSeen = false;
		
		listener.dataChanged(); // We could do a getBufferSize() instead.
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
			size = previousPageSize();
			pageOffsets.remove(pageOffsets.size() - 1); // pop page.
			
			listener.dataChanged();
		}
	}
	
	public void nextPage() {
		if (!isLastPage()) {
			pageOffsets.add(currentPageOffset() + size);
			
			// Trigger fetch if necessary.
			getBufferSize();
			
			listener.dataChanged();
		}
	}

	
	public boolean isFirstPage() {
		return currentPageOffset() == 0;
	}
	
	public boolean isLastPage() {
		return lastSeen && currentPageOffset() + size >= data.size();
	}
	
	/**
	 * 
	 * @return the size of the current page.
	 */
	public int getSize() {
		int result = getBufferSize();
		if (result < size) {
			return result;
		}
		return size;
	}
	
	
	/**
	 * set the size of the current page.
	 * @param size
	 */
	public void setSize(int size) {
		Preconditions.checkArgument(size < data.size() - currentPageOffset());
		this.size = size;
	}
	
	public T get(int relativeIndex) {
		Preconditions.checkElementIndex(relativeIndex, data.size() - currentPageOffset());
		
		return data.get(currentPageOffset() + relativeIndex);
	}

	/**
	 * 
	 * @return The number of elements available for the current page.
	 */
	public int getBufferSize() {
		int available = data.size() - currentPageOffset();
		assert available >= 0;
		
		if (lastSeen) {
			return available;
		}
		
		// ensure buffersize > f x last page size, always keep one in reserve for lastseen.
		int shortage = PREFETCH_FACTOR * pageSizeHint() + 1 - available;
		if (shortage > 0) {
			boolean currentPageAvailable = available - 1 > size;
			fetchMoreData(shortage, currentPageAvailable);
		}
		
		// We always fetch more than necessary to be able to set lastseen.
		if (available > 0) {
			return available - 1;
		} else {
			return available;
		}
	}

	/**
	 * Request more data to be retrieved.  
	 * 
	 * <p>
	 * Note that it is not guaranteed to obtain more data.  For instance, if we are on the lastpage, no data will be retrieved <b>nor</b>
	 * will the listener be called!
	 */
	public void requestMore() {
		fetchMoreData(previousPageSize(), false); // TODO How much to get now? other option: defaultpagesize?
	}
	
	private void fetchMoreData(int nr, final boolean isPrefetch) {
		Preconditions.checkNotNull(command, "A command is required to fetch data");
		
		// Only fetch data if we have not seen last yet.
		if (!lastSeen) {
			
			final PagedCommand<T> usedCommand = command;
			command.startIndex = data.size();
			command.pageSize = nr;
			GwtCommandFacade.execute(command, new AsyncCallback<PagedCommand.Result<T>>() {
				public void onFailure(Throwable caught) {
					if (usedCommand.equals(command)) {
						flush();
						// TODO ??
					}
				}
	
				public void onSuccess(PagedCommand.Result<T> result) {
					if (usedCommand.equals(command)) {
						lastSeen = result.getResult().size() < usedCommand.pageSize;
						data.addAll(result.getResult());
						if (!isPrefetch) {
							listener.dataChanged();
						}
					}
				}
			});
		}
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
