package easyenterprise.lib.command;

import java.util.List;

import com.google.common.base.Objects;

public abstract class PagedCommand<T> implements Command<PagedCommand.Result<T>> {

	private static final long serialVersionUID = 1L;
	
	public int startIndex;
	public int pageSize;
	
	public static interface Result<T> extends CommandResult {
		List<T> getResult();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PagedCommand) {
			PagedCommand<?> other = (PagedCommand<?>) obj;

			return startIndex == other.startIndex && pageSize == other.pageSize;
		}
		
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(startIndex, pageSize);
	}
}
