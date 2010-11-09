package easyenterprise.lib.command;

import easyenterprise.lib.cloner.View;

public interface CommandImpl<T extends CommandResult, C extends Command<T>> {

	View getView();
	
	void preExecute(C command) throws CommandException;
	
	T execute(C command) throws CommandException;
	
	T postExecute(C command, T result, CommandException e) throws CommandException;
}
