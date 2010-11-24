package easyenterprise.lib.command.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandWrapper;

public class JpaService extends CommandWrapper {

	private static final ThreadLocal<JpaCommandState> stateLocal = new ThreadLocal<JpaCommandState>();

	private final EntityManagerFactory entityManagerFactory;
	
	public JpaService(CommandWrapper delegate, EntityManagerFactory entityManagerFactory) {
		super(delegate);
		this.entityManagerFactory = entityManagerFactory;
	}
	
	public static void commit() {
		commit(getOrCreateState());
	}
	
	public static void rollback() {
		rollback(getOrCreateState());
	}
	
	public static EntityManager getEntityManager() {
		JpaCommandState state = getOrCreateState();
		if (state.entityManager == null) {
			state.entityManager = state.entityManagerFactory.createEntityManager();
		}
		if (state.entityManager != null && !state.entityManager.getTransaction().isActive()) {
			state.entityManager.getTransaction().begin();
		}			
		return state.entityManager;
	}

	public <T extends CommandResult, I extends CommandImpl<T>> T executeImpl(I command) throws CommandException {
		JpaCommandState oldState = getOrCreateState();
		
		// create new state
		JpaCommandState state = new JpaCommandState();
		state.entityManagerFactory = entityManagerFactory;
		stateLocal.set(state);
		try {
			return super.executeImpl(command);
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

	private static void commit(JpaCommandState state) {
		if (state.entityManager != null && state.entityManager.getTransaction().isActive()) {
			state.entityManager.getTransaction().commit();
		}
	}

	private static void rollback(JpaCommandState state) {
		// rollback on exception
		if (state.entityManager != null && state.entityManager.getTransaction().isActive()) {
			state.entityManager.getTransaction().rollback();
		}
	}

	private static JpaCommandState getOrCreateState() {
	  JpaCommandState state = stateLocal.get();
	  if (state == null) {
	  	state = new JpaCommandState();
	  	stateLocal.set(state);
	  }
	  return state;
  }
}

final class JpaCommandState {
	EntityManagerFactory entityManagerFactory;
	EntityManager entityManager;
}