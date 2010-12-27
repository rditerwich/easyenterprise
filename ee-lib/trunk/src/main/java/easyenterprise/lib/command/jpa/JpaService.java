package easyenterprise.lib.command.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandWrapper;

public class JpaService extends CommandWrapper {

	private static final ThreadLocal<JpaCommandState> stateLocal = new ThreadLocal<JpaCommandState>() {
		protected JpaCommandState initialValue() {
			return new JpaCommandState();
		};
	};

	private static EntityManagerFactory globalEntityManagerFactory;
	private final EntityManagerFactory entityManagerFactory;
	
	public JpaService(CommandWrapper delegate, EntityManagerFactory entityManagerFactory) {
		super(delegate);
		this.entityManagerFactory = entityManagerFactory;
	}
	
	public static void commit() {
		commit(stateLocal.get());
	}
	
	public static void rollback() {
		rollback(stateLocal.get());
	}
	
	/**
	 * Get an entity manager for the current thread.  Must only be called from either a runnable within {@link #runInTransaction(Runnable)} or from a command in {@link #executeImpl(CommandImpl)}.
	 * @return
	 */
	public static EntityManager getEntityManager() {
		JpaCommandState state = stateLocal.get();
		if (state.entityManager == null) {
			if (state.entityManagerFactory != null) {
				state.entityManager = state.entityManagerFactory.createEntityManager();
			} else if (globalEntityManagerFactory != null) {
				state.entityManager = globalEntityManagerFactory.createEntityManager();
			} else {
				throw new RuntimeException("No entity manager specified");
			}
		}
		if (state.entityManager != null && !state.entityManager.getTransaction().isActive()) {
			state.entityManager.getTransaction().begin();
		}			
		return state.entityManager;
	}
	
	public static void setGlobalEntityManagerFactory(EntityManagerFactory factory) {
		globalEntityManagerFactory = factory;
	}
	
	public static void runInTransaction(Runnable r) {
		JpaCommandState oldState = stateLocal.get();
		
		// create new state
		JpaCommandState state = new JpaCommandState();
		stateLocal.set(state);
		try {
			r.run();
		} catch (Throwable e) {
			rollback(state);
			throw new RuntimeException("Exception during transaction.  Rolling back.", e);
		} finally {
			// auto-commit
			commit(state);
			// restore old state
			stateLocal.set(oldState);
		}
	}

	public <T extends CommandResult, I extends CommandImpl<T>> T executeImpl(I command) throws CommandException {
		JpaCommandState oldState = stateLocal.get();
		
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
		if (state.entityManager != null) {
			if (state.entityManager.getTransaction().isActive()) {
				state.entityManager.getTransaction().commit();
			}
			state.entityManager.close();
			state.entityManager = null;
		}
	}

	private static void rollback(JpaCommandState state) {
		// rollback on exception
		if (state.entityManager != null) {
			if (state.entityManager.getTransaction().isActive()) {
				state.entityManager.getTransaction().rollback();
			}
			state.entityManager.close();
			state.entityManager = null;
		}
	}
}

final class JpaCommandState {
	EntityManagerFactory entityManagerFactory;
	EntityManager entityManager;
}
