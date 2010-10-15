package easyenterprise.lib.command.jpa;

import javax.persistence.EntityManager;

import easyenterprise.lib.cloner.CloneException;
import easyenterprise.lib.cloner.Cloner;
import easyenterprise.lib.cloner.View;
import easyenterprise.lib.command.AbstractCommandImpl;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandResult;

public abstract class TransactionalCommandImpl<T extends CommandResult, C extends Command<T>> extends AbstractCommandImpl<T, C> {
	
	public static EntityManager getEntityManager() {
		return JpaCommandWrapper.getEntityManager();
	}
	
	private View cloneView = getCloneView();

	@Override
	public void preExecute(C command) throws CommandException {
		// auto-start transaction
		getEntityManager().getTransaction().begin();
	};
	
	public View getCloneView() {
		return null;
	}
	
	@Override
	public T postExecute(C command, T result) throws CommandException {
		if (cloneView != null) {
			try {
				result = Cloner.clone(result, cloneView);
			} catch (CloneException e) {
				throw new CommandException("Couldn't clone " + result.getClass().getSimpleName() + " class: " + e.getMessage(), e);
			}
		}
		return result;
	};
}
