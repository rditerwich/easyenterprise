package easyenterprise.lib.command;

public class CommandValidationException extends CommandException {

	private static final long serialVersionUID = 1L;
	
	public static void validate(boolean condition) throws CommandValidationException {
		if (!condition) {
			throw new CommandValidationException();
		}
	}
}
