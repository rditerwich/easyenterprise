package easyenterprise.lib.command;

public interface CommandImpl<T extends CommandResult> extends Command<T> {

	T execute() throws CommandException;
}
