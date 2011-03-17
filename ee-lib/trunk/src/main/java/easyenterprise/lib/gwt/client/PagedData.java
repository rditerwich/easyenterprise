package easyenterprise.lib.gwt.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Preconditions;

import easyenterprise.lib.util.Paging;
import easyenterprise.lib.util.SMap;

/**
 * Represents data that is retrieved using a command as pages.  The size of the page can either be static (initialized with <code>defaultPageSize</code>) or more dynamic
 * where the user sets the page size where appropriate.  
 * 
 * A usage scenario with contant page sizes:
 * <pre>
 *pagedData = new PagedData(PAGESIZE, this, this);
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
 *pagedData = new PagedData(ESTIMATED_PAGESIZE, this, this);
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
public class PagedData<K, V> {
	private static final int PREFETCH_FACTOR = 2;

	private final int defaultPageSize;

	private boolean lastSeen;
	private int size;
	
	// PP for tests.
	List<Integer> pageOffsets = new ArrayList<Integer>();

	private SMap<K, V> data = SMap.empty();

	private final List<Listener> listeners = new ArrayList<Listener>();

	private Paging requestedPage;

	private final DataSource<K, V> source;
	
	public interface Listener {
		void dataChanged();
	}
	
	public interface Callback<K, V> {
		void dataFetched(List<Map.Entry<K, V>> fetchedData);
	}
	
	public interface DataSource<K, V> {
		void fetchData(Paging page, Callback<K, V> callback);
	}
	
	public PagedData(int defaultPageSize, DataSource<K, V> source, Listener listener) {
		Preconditions.checkNotNull(source);
		Preconditions.checkNotNull(listener);
		
		this.defaultPageSize = defaultPageSize;
		this.size = defaultPageSize;
		pageOffsets.add(0);  // The first page starts at 0.
		
		this.source = source;
		this.listeners.add(listener);
	}
	
	public void addListener(Listener listener) {
		this.listeners.add(listener);
	}
	
	public void flush() {
		size = defaultPageSize; // TODO should we do this??
		pageOffsets.clear();
		pageOffsets.add(0);

		data = SMap.empty();

		lastSeen = false;
		
		requestedPage = null;
		
		getBufferSize();
	}
	
	public void previousPage() {
		if (!isFirstPage()) {
			size = previousPageSize();
			pageOffsets.remove(pageOffsets.size() - 1); // pop page.
			
			dataChanged();
		}
	}

	public void nextPage() {
		if (!isLastPage() && computeBufferSize() > 0) {
			pageOffsets.add(currentPageOffset() + getSize());
			
			dataChanged();
		}
	}
	
	public boolean isFirstPage() {
		return currentPageOffset() == 0;
	}
	
	public boolean isLastPage() {
		return lastSeen && currentPageOffset() + getSize() >= data.size();
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
		Preconditions.checkArgument(size <= computeBufferSize(), "size (" + size + ") must not be greater than buffersize: (" + computeBufferSize() + ")");
		this.size = size;
	}
	
	public V get(int relativeIndex) {
		Preconditions.checkElementIndex(relativeIndex, computeBufferSize());
		
		return data.get(currentPageOffset() + relativeIndex);
	}
	public K getKey(int relativeIndex) {
		Preconditions.checkElementIndex(relativeIndex, computeBufferSize());
		
		return data.getKey(currentPageOffset() + relativeIndex);
	}
	public Map.Entry<K, V> getEntry(int relativeIndex) {
		Preconditions.checkElementIndex(relativeIndex, computeBufferSize());
		
		return data.getEntry(currentPageOffset() + relativeIndex);
	}
	
	public void create(K key, V value) {
		flushPages(0);
		
		// Prepend data with new key/value.
		SMap<K, V> oldData = data;
		data = SMap.empty();
		data = data.add(key, value);
		data = data.addAll(oldData);
		
		dataChanged();
	}
	
	/**
	 * Only sets if key is contained in the buffer.
	 * @param key
	 * @param value
	 */
	public void set(K key, V value) {
		int index = data.indexOf(key);
		if (index != -1) {
			data = data.set(key, value);
			
			flushPages(index);
			
			dataChanged();
		}
	}
	
	public void remove(K key) {
		int keyIndex = data.indexOf(key);
		if (keyIndex >= 0) {
			data = data.removeKey(keyIndex);
			
			// Flush all page offsets from keyIndex.
			flushPages(keyIndex);
			
			if (keyIndex <= currentPageOffset() + getSize()) {
				dataChanged(); // TODO is this enough??
			}
		}
	}
	
	public int indexOnPage(K key) {
		int keyIndex = data.indexOf(key);
		if (keyIndex < currentPageOffset() || keyIndex > currentPageOffset() + getSize()) {
			return -1;
		}
		
		return keyIndex - currentPageOffset();
	}

	private void flushPages(int keyIndex) {
		// Flush pages until keyindex is contained within the current page, except for the first page.
		for (int i = pageOffsets.size() - 1; i > 0 && currentPageOffset() >= keyIndex; i++) {
			pageOffsets.remove(i);
		}
		size = defaultPageSize;
	}

	/**
	 * 
	 * @return The number of elements available for the current page.
	 */
	public int getBufferSize() {
		if (lastSeen) {
			return computeBufferSize();
		}
		
		// ensure buffersize > f x last page size, always keep one in reserve for lastseen.
		int shortage = PREFETCH_FACTOR * pageSizeHint() + 1 - getAvailable();
		if (shortage > 0) {
			fetchMoreData(shortage, false);
		}
		
		return computeBufferSize();
	}
	
	/**
	 * Request more data to be retrieved.  
	 * 
	 * <p>
	 * Note that it is not guaranteed to obtain more data.  For instance, if we are on the lastpage, no data will be retrieved <b>nor</b>
	 * will the listener be called!
	 */
	public void requestMore() {
		fetchMoreData(pageSizeHint(), true); // TODO How much to get now? other option: defaultpagesize?
	}
	
	
	private void fetchMoreData(int nr, final boolean forceDataChanged) {

		// Only fetch data if we have not seen last yet.
		if (!lastSeen) {
			
			final Paging page = Paging.get(data.size(), nr);
			
			// Only fetch if we did not just retrieve this page
			if (!alreadyRequested(page)) {
				requestedPage = Paging.get(data.size(), nr);
				assert requestedPage.equals(page);
	
				source.fetchData(page, new Callback<K, V>() {
					public void dataFetched(List<Entry<K, V>> fetchedData) {
						if (page.equals(requestedPage)) {
							// getAvailable() == 0 && fetched == 0 is a special case that I'm not sure how to more naturally express...
							boolean pageChanged = computeBufferSize() < size && fetchedData.size() > 0 || getAvailable() == 0 && fetchedData.size() == 0; 
							
							lastSeen = fetchedData.size() < page.getPageSize();
							for (Entry<K, V> entry : fetchedData) {
								data = data.add(entry.getKey(), entry.getValue());
							}
		
							if (forceDataChanged || pageChanged) {
								dataChanged();
							}
						}
					}
				});
			}
		}
	}
	
	private boolean alreadyRequested(Paging page) {
		return requestedPage != null && requestedPage.getPageStart() <= page.getPageStart() && requestedPage.getPageStart() + requestedPage.getPageSize() >= page.getPageStart() + page.getPageSize();
	}
	
	private void dataChanged() {
		for (Listener listener : listeners) {
			listener.dataChanged();
		}
	}

	private int computeBufferSize() {
		int available = getAvailable();
		return !lastSeen && available > 0 ? available - 1 : available;
	}
	
	private int getAvailable() {
		return data.size() - currentPageOffset();
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
