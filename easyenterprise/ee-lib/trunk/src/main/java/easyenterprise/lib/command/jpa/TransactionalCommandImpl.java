package easyenterprise.lib.command.jpa;

import easyenterprise.lib.command.AbstractCommandImpl;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandResult;

/**
 * Subclass this command impl to ensure that the command is executed in the 
 * context of a transaction.
 * 
 * @author Ruud Diterwich
 */
public abstract class TransactionalCommandImpl<T extends CommandResult, C extends Command<T>> extends AbstractCommandImpl<T, C> {
	
	@Override
	public void preExecute(C command) throws CommandException {
		// auto-start transaction
		JpaCommandService.getEntityManager().getTransaction().begin();
	};
}
