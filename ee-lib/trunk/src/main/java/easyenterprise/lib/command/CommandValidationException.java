package easyenterprise.lib.command;

public class CommandValidationException extends CommandException {

	private static final long serialVersionUID = 1L;
	
	public CommandValidationException() {
	}
	
	public CommandValidationException(String message) {
		super(message);
	}

	public static void validate(boolean condition) throws CommandValidationException {
		if (!condition) {
			throw new CommandValidationException();
		}
	}
	
	public static void validate(boolean condition, String message) throws CommandValidationException {
		if (!condition) {
			throw new CommandValidationException(message);
		}
	}
}
