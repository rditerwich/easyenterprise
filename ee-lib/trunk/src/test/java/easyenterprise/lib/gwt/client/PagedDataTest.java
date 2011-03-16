package easyenterprise.lib.gwt.client;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import easyenterprise.lib.gwt.client.PagedData.Callback;
import easyenterprise.lib.gwt.client.PagedData.Listener;
import easyenterprise.lib.util.CollectionUtil;
import easyenterprise.lib.util.LineWriter;
import easyenterprise.lib.util.Paging;
import easyenterprise.lib.util.TestUtil;

@SuppressWarnings("serial")
public class PagedDataTest {
	private LineWriter output = new LineWriter(System.out);
	
	private static final int STATIC_PAGESIZE = 5;
	private PagedData<Integer, Integer> pagedData;
	
	
	
	@Test
	public void basicStatic() {
		pagedData = new PagedData<Integer, Integer>(STATIC_PAGESIZE, defaultDataSource(25), new Listener() {
			public void dataChanged() {
				output.writeln("Data Changed");
				for (int i = 0; i < pagedData.getSize(); i++) {
					Integer data = pagedData.get(i);
					output.writeln("  i: " + data);
				}
			}
		});
		pagedData.flush();
		for (int i = 0; i < 5; i++) {
			output.writeln("nextPage()");
			pagedData.nextPage();
		}
		
		TestUtil.assertOutput(output, 
				"PagedCommand(0,11)",
				"Data Changed",
				"  i: 0",
				"  i: 1",
				"  i: 2",
				"  i: 3",
				"  i: 4",
				"nextPage()",
				"Data Changed",
				"PagedCommand(11,5)",
				"  i: 5",
				"  i: 6",
				"  i: 7",
				"  i: 8",
				"  i: 9",
				"nextPage()",
				"Data Changed",
				"PagedCommand(16,5)",
				"  i: 10",
				"  i: 11",
				"  i: 12",
				"  i: 13",
				"  i: 14",
				"nextPage()",
				"Data Changed",
				"PagedCommand(21,5)",
				"  i: 15",
				"  i: 16",
				"  i: 17",
				"  i: 18",
				"  i: 19",
				"nextPage()",
				"Data Changed",
				"  i: 20",
				"  i: 21",
				"  i: 22",
				"  i: 23",
				"  i: 24",
				"nextPage()"
				);
	}
	
	@Test
	public void basicDynamic() {
		final Integer[] pageSizes = new Integer[] { 5, 8, 7, 3, 10 };
		final Integer[] pageNr = new Integer[1];
		pageNr[0] = 0;
		pagedData = new PagedData<Integer, Integer>(STATIC_PAGESIZE, defaultDataSource(25), new Listener() {
			public void dataChanged() {
				Integer pageSize = pageSizes[pageNr[0]];
				int bufferSize = pagedData.getBufferSize();
				output.writeln("Data Changed, p: " + pageNr[0] + " bufsize: " + bufferSize);
				for (int i = 0; i < bufferSize; i++) {
					Integer data = pagedData.get(i);
					output.writeln("  i: " + data);
					if (i >= pageSize - 1) {
						pagedData.setSize(pageSize);
						output.writeln("  setSize: " + pageSize);
						break;
					}
				}
			}
		});
		output.writeln("setCommand()");
		pagedData.flush();
		for (int i = 0; i < 5; i++) {
			pageNr[0]++;
			output.writeln("nextPage()");
			pagedData.nextPage();
		}
		
		
		TestUtil.assertOutput(output, 
				"setCommand()",
				"PagedCommand(0,11)",
				"Data Changed, p: 0 bufsize: 10",
				"  i: 0",
				"  i: 1",
				"  i: 2",
				"  i: 3",
				"  i: 4",
				"  setSize: 5",
				"nextPage()",
				"PagedCommand(11,5)",
				"Data Changed, p: 1 bufsize: 10",
				"  i: 5",
				"  i: 6",
				"  i: 7",
				"  i: 8",
				"  i: 9",
				"  i: 10",
				"  i: 11",
				"  i: 12",
				"  setSize: 8",
				"nextPage()",
				"PagedCommand(16,14)",
				"Data Changed, p: 2 bufsize: 12",
				"  i: 13",
				"  i: 14",
				"  i: 15",
				"  i: 16",
				"  i: 17",
				"  i: 18",
				"  i: 19",
				"  setSize: 7",
				"Data Changed, p: 2 bufsize: 12",
				"  i: 13",
				"  i: 14",
				"  i: 15",
				"  i: 16",
				"  i: 17",
				"  i: 18",
				"  i: 19",
				"  setSize: 7",
				"nextPage()",
				"Data Changed, p: 3 bufsize: 5",
				"  i: 20",
				"  i: 21",
				"  i: 22",
				"  setSize: 3",
				"nextPage()",
				"Data Changed, p: 4 bufsize: 2",
				"  i: 23",
				"  i: 24",
				"nextPage()"
				);
	}
	
	@Test
	public void firstPage() {
		final Integer[] pageSizes = new Integer[] { 5, 8, 7, 3, 10 };
		final Integer[] pageNr = new Integer[1];
		pageNr[0] = 0;
		pagedData = new PagedData<Integer, Integer>(STATIC_PAGESIZE, defaultDataSource(25), new Listener() {
			public void dataChanged() {
				Integer pageSize = pageSizes[pageNr[0]];
				int bufferSize = pagedData.getBufferSize();
				for (int i = 0; i < bufferSize; i++) {
					if (i >= pageSize - 1) {
						pagedData.setSize(pageSize);
						break;
					}
				}
			}
		});
		output.writeln("setCommand()");
		pagedData.flush();
		
		output.writeln("isFirstPage: " + pagedData.isFirstPage());
		output.writeln("nextPage()");
		pagedData.nextPage();
		output.writeln("isFirstPage: " + pagedData.isFirstPage());
		output.writeln("previousPage()");
		pagedData.previousPage();
		output.writeln("isFirstPage: " + pagedData.isFirstPage());
		
		TestUtil.assertOutput(output, 
				"setCommand()",
				"PagedCommand(0,11)",
				"isFirstPage: true",
				"nextPage()",
				"PagedCommand(11,5)",
				"isFirstPage: false",
				"previousPage()",
				"isFirstPage: true"
			);
	}
	
	@Test
	public void lastPage() {
		final Integer[] pageSizes = new Integer[] { 5, 8, 7, 3, 10 };
		final Integer[] pageNr = new Integer[1];
		pageNr[0] = 0;
		pagedData = new PagedData<Integer, Integer>(STATIC_PAGESIZE, defaultDataSource(25), new Listener() {
			public void dataChanged() {
				Integer pageSize = pageSizes[pageNr[0]];
				int bufferSize = pagedData.getBufferSize();
				for (int i = 0; i < bufferSize; i++) {
					if (i >= pageSize - 1) {
						pagedData.setSize(pageSize);
						break;
					}
				}
			}
		});
		output.writeln("setCommand()");
		pagedData.flush();
		
		output.writeln("isLastPage: " + pagedData.isLastPage());
		output.writeln("nextPage()");
		pageNr[0]++;
		pagedData.nextPage();
		output.writeln("isLastPage: " + pagedData.isLastPage());
		output.writeln("previousPage()");
		pageNr[0]--;
		pagedData.previousPage();
		output.writeln("isLastPage: " + pagedData.isLastPage());
		
		output.writeln("nextPage()");
		pageNr[0]++;
		pagedData.nextPage();
		output.writeln("nextPage()");
		pageNr[0]++;
		pagedData.nextPage();
		output.writeln("nextPage()");
		pageNr[0]++;
		pagedData.nextPage();
		output.writeln("nextPage()");
		pageNr[0]++;
		pagedData.nextPage();
		output.writeln("isLastPage: " + pagedData.isLastPage());
		
		output.writeln(pagedData.pageOffsets);

		TestUtil.assertOutput(output, 
				"setCommand()",
				"PagedCommand(0,11)",
				"isLastPage: false",
				"nextPage()",
				"PagedCommand(11,5)",
				"isLastPage: false",
				"previousPage()",
				"isLastPage: false",
				"nextPage()",
				"nextPage()",
				"PagedCommand(16,14)",
				"nextPage()",
				"nextPage()",
				"isLastPage: true",
				"[0, 5, 13, 20, 23]"
				);
	}
	
	@Test
	public void requestMore() {
		final Integer[] pageSizes = new Integer[] { 5, 8, 20, 3, 10 };
		final Integer[] pageNr = new Integer[1];
		pageNr[0] = 0;
		pagedData = new PagedData<Integer, Integer>(STATIC_PAGESIZE, defaultDataSource(40), new Listener() {
			public void dataChanged() {
				Integer pageSize = pageSizes[pageNr[0]];
				int bufferSize = pagedData.getBufferSize();
				output.writeln("Data Changed, p: " + pageNr[0] + " bufsize: " + bufferSize);
				int i = 0;
				for (i = 0; i < bufferSize; i++) {
					Integer data = pagedData.get(i);
					output.writeln("  i: " + data);
					if (i >= pageSize - 1) {
						pagedData.setSize(pageSize);
						output.writeln("  setSize: " + pageSize);
						break;
					}
				}
				if (i < pageSize - 1) {
					output.writeln("  requestMore(), i: " + i);
					pagedData.requestMore();
				}
			}
		});
		output.writeln("setCommand()");
		pagedData.flush();
		for (int i = 0; i < 2; i++) {
			pageNr[0]++;
			output.writeln("nextPage()");
			pagedData.nextPage();
		}
		
		output.writeln(pagedData.pageOffsets);

		TestUtil.assertOutput(output, 
				"setCommand()",
				"PagedCommand(0,11)",
				"Data Changed, p: 0 bufsize: 10",
				"  i: 0",
				"  i: 1",
				"  i: 2",
				"  i: 3",
				"  i: 4",
				"  setSize: 5",
				"nextPage()",
				"PagedCommand(11,5)",
				"Data Changed, p: 1 bufsize: 10",
				"  i: 5",
				"  i: 6",
				"  i: 7",
				"  i: 8",
				"  i: 9",
				"  i: 10",
				"  i: 11",
				"  i: 12",
				"  setSize: 8",
				"nextPage()",
				"PagedCommand(16,14)",
				"Data Changed, p: 2 bufsize: 16",
				"  i: 13",
				"  i: 14",
				"  i: 15",
				"  i: 16",
				"  i: 17",
				"  i: 18",
				"  i: 19",
				"  i: 20",
				"  i: 21",
				"  i: 22",
				"  i: 23",
				"  i: 24",
				"  i: 25",
				"  i: 26",
				"  i: 27",
				"  i: 28",
				"  requestMore(), i: 16",
				"PagedCommand(30,8)",
				"Data Changed, p: 2 bufsize: 24",
				"  i: 13",
				"  i: 14",
				"  i: 15",
				"  i: 16",
				"  i: 17",
				"  i: 18",
				"  i: 19",
				"  i: 20",
				"  i: 21",
				"  i: 22",
				"  i: 23",
				"  i: 24",
				"  i: 25",
				"  i: 26",
				"  i: 27",
				"  i: 28",
				"  i: 29",
				"  i: 30",
				"  i: 31",
				"  i: 32",
				"  setSize: 20",
				"Data Changed, p: 2 bufsize: 24",
				"  i: 13",
				"  i: 14",
				"  i: 15",
				"  i: 16",
				"  i: 17",
				"  i: 18",
				"  i: 19",
				"  i: 20",
				"  i: 21",
				"  i: 22",
				"  i: 23",
				"  i: 24",
				"  i: 25",
				"  i: 26",
				"  i: 27",
				"  i: 28",
				"  i: 29",
				"  i: 30",
				"  i: 31",
				"  i: 32",
				"  setSize: 20",
				"[0, 5, 13]"
				);
	}
	
	@Test
	public void flush() {
		// TODO
	}
	
	private PagedData.DataSource<Integer, Integer> defaultDataSource(final int maxIndex) {
		return new PagedData.DataSource<Integer, Integer>() {
			public void fetchData(Paging paging, Callback<Integer, Integer> callback) {
				output.writeln("PagedCommand(" + paging.getPageStart() + "," + paging.getPageSize() + ")");

				Map<Integer, Integer> result = new LinkedHashMap<Integer, Integer>();
				for (int i = paging.getPageStart(); i < paging.getPageStart() + paging.getPageSize() && i < maxIndex; i++) {
					result.put(i, i);
				}

				callback.dataFetched(CollectionUtil.asList(result.entrySet()));
			}
		};
	}

}
