package easyenterprise.lib.command;

import easyenterprise.lib.cloner.CloneException;
import easyenterprise.lib.cloner.Cloner;
import easyenterprise.lib.cloner.View;

public class CommandService {

	public static <T> T clone(T object, View view) throws CommandException {
		try {
	    return Cloner.clone(object, view);
    } catch (CloneException e) {
	    throw new CommandException(e);
    }
	}
}
