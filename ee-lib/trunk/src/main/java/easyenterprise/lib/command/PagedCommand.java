package easyenterprise.lib.command;

import java.util.List;

public abstract class PagedCommand<T> implements Command<PagedCommand.Result<T>> {

	private static final long serialVersionUID = 1L;
	
	public int startIndex;
	public int pageSize;
	
	public static interface Result<T> extends CommandResult {
		List<T> getResult();
	}
}
