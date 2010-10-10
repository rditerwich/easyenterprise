package easyenterprise.lib.command;

public class CommandNotImplementedException extends CommandException {

	private static final long serialVersionUID = 1L;

	public CommandNotImplementedException(Command<?> command) {
		super("Command not implemented: " + command);
	}
}
