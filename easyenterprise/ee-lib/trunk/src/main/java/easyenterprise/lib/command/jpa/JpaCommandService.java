package easyenterprise.lib.command.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandService;
import easyenterprise.lib.command.CommandResult;

public class JpaCommandService implements CommandService {

	private static final ThreadLocal<JpaCommandState> stateLocal = new ThreadLocal<JpaCommandState>();

	private final CommandService delegate;
	private final EntityManagerFactory entityManagerFactory;
	
	public JpaCommandService(CommandService delegate, EntityManagerFactory entityManagerFactory) {
		this.delegate = delegate;
		this.entityManagerFactory = entityManagerFactory;
	}
	
	public <T extends CommandResult, C extends Command<T>> T execute(C command) throws CommandException {
		// preserve old state
		JpaCommandState oldState = stateLocal.get();
		
		// create new state
		JpaCommandState state = new JpaCommandState();
		state.entityManagerFactory = entityManagerFactory;
		stateLocal.set(state);
		try {
			return delegate.execute(command);
		} catch (CommandException e) {
			rollback(state);
			throw e;
		} catch (Throwable e) {
			rollback(state);
			throw new CommandException(e.getMessage(), e);
		} finally {
			// auto-commit
			commit(state);
			// restore old state
			stateLocal.set(oldState);
		}
	}

	private void commit(JpaCommandState state) {
		if (state.entityManager != null && state.entityManager.getTransaction().isActive()) {
			state.entityManager.getTransaction().commit();
		}
	}

	private void rollback(JpaCommandState state) {
		// rollback on exception
		if (state.entityManager != null && state.entityManager.getTransaction().isActive()) {
			state.entityManager.getTransaction().rollback();
		}
	}
	
	public static EntityManager getEntityManager() {
		JpaCommandState state = stateLocal.get();
		if (state.entityManager == null) {
			state.entityManager = state.entityManagerFactory.createEntityManager();
		}
		return state.entityManager;
	}
}

class JpaCommandState {
	EntityManagerFactory entityManagerFactory;
	EntityManager entityManager;
}
