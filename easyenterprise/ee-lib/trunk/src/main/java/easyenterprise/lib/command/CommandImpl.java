package easyenterprise.lib.command;

public interface CommandImpl<T extends CommandResult, C extends Command<T>> {

	void preExecute(C command) throws CommandException;
	
	T execute(C command) throws CommandException;
	
	T postExecute(C command, T result, CommandException e) throws CommandException;
}
