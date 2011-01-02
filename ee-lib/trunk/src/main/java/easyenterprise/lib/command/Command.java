package easyenterprise.lib.command;

import java.io.Serializable;


public interface Command<T extends CommandResult> extends Serializable {

	public void checkValid() throws CommandValidationException;
}
