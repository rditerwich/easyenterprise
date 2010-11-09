package easyenterprise.lib.command;

import easyenterprise.lib.cloner.CloneException;

public class CommandException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public CommandException() {
	}
	
	public CommandException(String description) {
		super(description);
	}

	public CommandException(String description, Throwable cause) {
		super(description, cause);
	}

	public CommandException(CloneException cause) {
		super(cause);
  }
	
}
