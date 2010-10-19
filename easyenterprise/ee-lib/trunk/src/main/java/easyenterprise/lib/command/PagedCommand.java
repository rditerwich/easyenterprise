package easyenterprise.lib.command;


public abstract class PagedCommand<T extends CommandResult> implements Command<T> {

	private static final long serialVersionUID = 1L;
	
	public int startIndex;
	public int pageSize;
}
