package easyenterprise.lib.gwt.client;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gwt.user.client.rpc.AsyncCallback;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.command.PagedCommand;
import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.command.gwt.GwtCommandServiceAsync;
import easyenterprise.lib.gwt.client.PagedData.Listener;
import easyenterprise.lib.util.LineWriter;
import easyenterprise.lib.util.TestUtil;

@SuppressWarnings("serial")
public class PagedDataTest {
	private LineWriter output = new LineWriter(System.out);
	
	private static final int STATIC_PAGESIZE = 5;
	private PagedData<Integer> pagedData;
	
	
	
	@Test
	public void basicStatic() {
		pagedData = new PagedData<Integer>(STATIC_PAGESIZE, new Listener() {
			public void dataChanged() {
				output.writeln("Data Changed");
				for (int i = 0; i < pagedData.getSize(); i++) {
					Integer data = pagedData.get(i);
					output.writeln("  i: " + data);
				}
			}
		});
		pagedData.setCommand(defaultCommand(25));
		for (int i = 0; i < 5; i++) {
			output.writeln("nextPage()");
			pagedData.nextPage();
		}
		
		TestUtil.assertOutput(output, 
				"Data Changed",
				"PagedCommand(0,11)",
				"Data Changed",
				"  i: 0",
				"  i: 1",
				"  i: 2",
				"  i: 3",
				"  i: 4",
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
		pagedData = new PagedData<Integer>(STATIC_PAGESIZE, new Listener() {
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
		pagedData.setCommand(defaultCommand(25));
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
		pagedData = new PagedData<Integer>(STATIC_PAGESIZE, new Listener() {
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
		pagedData.setCommand(defaultCommand(25));
		
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
		pagedData = new PagedData<Integer>(STATIC_PAGESIZE, new Listener() {
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
		pagedData.setCommand(defaultCommand(25));
		
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
		pagedData = new PagedData<Integer>(STATIC_PAGESIZE, new Listener() {
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
		pagedData.setCommand(defaultCommand(40));
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
	public void resetCommand() {
		// TODO
	}
	
	@Test
	public void flush() {
		// TODO
	}
	
	@BeforeClass
	public static void setupOnce() {
		GwtCommandFacade.setAsyncCommandService(new GwtCommandServiceAsync() {
			public <T extends CommandResult> void execute(Command<T> command, AsyncCallback<T> callback) {
				try {
					callback.onSuccess(((CommandImpl<T>)command).execute());
				} catch (CommandException e) {
					Assert.fail(e.toString());
					e.printStackTrace();
				}
			}
		});
	}
	
	private abstract class PagedCommandImpl<T> extends PagedCommand<T> implements CommandImpl<PagedCommand.Result<T>> {
		public void checkValid() throws CommandValidationException {
		}
		
	}
	

	private PagedCommandImpl<Integer> defaultCommand(final int maxIndex) {
		return new PagedCommandImpl<Integer> () {
			public PagedCommand.Result<Integer> execute() throws CommandException {
				output.writeln("PagedCommand(" + startIndex + "," + pageSize + ")");
				return new Result<Integer>() {
					public List<Integer> getResult() {
						List<Integer> result = new ArrayList<Integer>();
						for (int i = startIndex; i < startIndex + pageSize && i < maxIndex; i++) {
							result.add(i);
						}
						return result;
					}
				};
			}
		};
	}
	

}
