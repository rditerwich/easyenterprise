package easyenterprise.lib.command;

import java.util.List;

import com.google.common.base.Objects;

import easyenterprise.lib.util.Paging;

public abstract class PagedCommand<T> implements Command<PagedCommand.Result<T>> {

	private static final long serialVersionUID = 1L;
	
	
	public Paging paging = Paging.NO_PAGING;
	
	public static interface Result<T> extends CommandResult {
		List<T> getResult();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PagedCommand) {
			PagedCommand<?> other = (PagedCommand<?>) obj;

			return Objects.equal(this.paging, other.paging);
		}
		
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(paging);
	}
}
