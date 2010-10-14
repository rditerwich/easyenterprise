package easyenterprise.lib.command.jpa;

import javax.persistence.EntityManager;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.CommandResult;

public abstract class TransactionalCommandImpl<T extends CommandResult, C extends Command<T>> implements CommandImpl<T, C> {

	public static EntityManager getEntityManager() {
		return JpaCommandWrapper.getEntityManager();
	}
	
}
