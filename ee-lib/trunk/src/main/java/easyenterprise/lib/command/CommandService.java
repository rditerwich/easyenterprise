package easyenterprise.lib.command;

public interface CommandService {
	<T extends CommandResult, C extends Command<T>> T execute(C command) throws CommandException;
}
