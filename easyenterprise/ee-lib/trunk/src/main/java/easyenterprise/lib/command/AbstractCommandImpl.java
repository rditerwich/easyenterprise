package easyenterprise.lib.command;

import easyenterprise.lib.cloner.CloneException;
import easyenterprise.lib.cloner.Cloner;
import easyenterprise.lib.cloner.View;

public abstract class AbstractCommandImpl<T extends CommandResult, C extends Command<T>> implements CommandImpl<T, C>{
	
	private View view = getView();

	public View getView() {
		return null;
	}
	
	public void preExecute(C command) throws CommandException {
	}

	public T postExecute(C command, T result, CommandException e) throws CommandException {
		if (e != null) {
			return postExecute(command, e);
		}
		if (view != null && !view.equals(View.NULL)) {
			return postExecute(command, result, view);
		} 
		return postExecute(command, result);
	}

	public T postExecute(C command, T result, View view) throws CommandException {
		try {
			return postExecute(command, Cloner.clone(result, view));
		} catch (CloneException e) {
			throw new CommandException("Couldn't clone " + result.getClass().getSimpleName() + " class: " + e.getMessage(), e);
		}
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
