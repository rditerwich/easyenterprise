package easyenterprise.lib.command;

public class CommandException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public CommandException() {
	}
	
	public CommandException(String description) {
		super(description);
	}

}
