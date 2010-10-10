package easyenterprise.lib.command;

import java.io.Serializable;

public interface CommandImpl<T extends Serializable, C extends Command<T>> {

	T execute(C command) throws CommandException;
}
