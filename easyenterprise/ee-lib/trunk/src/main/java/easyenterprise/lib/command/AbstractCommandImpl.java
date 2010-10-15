package easyenterprise.lib.command;

public abstract class AbstractCommandImpl<T extends CommandResult, C extends Command<T>> implements CommandImpl<T, C>{

	public void preExecute(C command) throws CommandException {
	}

	public T postExecute(C command, T result, CommandException e) throws CommandException {
		if (e != null) {
			return postExecute(command, e);
		}
		return postExecute(command, result);
	}

	public T postExecute(C command, T result) throws CommandException {
		return result;
	}
	
	public T postExecute(C command, CommandException e) throws CommandException {
		throw e;
	}
	
	static <T extends CommandResult, C extends Command<T>> T fullExecute(C command, CommandImpl<T, C> impl) throws CommandException {
		try {
			impl.preExecute(command);
			T result = impl.execute(command);
			return impl.postExecute(command, result, null);
		} catch (CommandException e) {
			return impl.postExecute(command, null, e);
		}
	}
}
