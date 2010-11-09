package easyenterprise.lib.command;

import easyenterprise.lib.cloner.View;

public interface CommandImpl<T extends CommandResult> extends Command<T> {

	View getView();
	
	void preExecute() throws CommandException;
	
	T execute() throws CommandException;
	
	T postExecute(T result, CommandException e) throws CommandException;
}
