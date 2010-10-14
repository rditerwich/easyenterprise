package easyenterprise.lib.command;

import java.io.Serializable;

public abstract class PagedCommand<T extends Serializable> implements Command<T> {

	private static final long serialVersionUID = 1L;
	
	public int startIndex;
	public int pageSize;
}
