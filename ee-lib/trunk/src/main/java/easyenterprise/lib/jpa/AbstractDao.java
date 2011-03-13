package easyenterprise.lib.jpa;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class AbstractDao {

	private final String persistenceUnitName;

	private final EntityManagerFactory factory;

	private final ThreadLocal<EntityManager> entityManager = new ThreadLocal<EntityManager>();
	
	protected AbstractDao(String persistenceUnitName, Map<String, String> properties) {
		this.persistenceUnitName = persistenceUnitName;
		this.factory = Persistence.createEntityManagerFactory(persistenceUnitName, properties);
	}

	public String getPersistenceUnitName() {
		return persistenceUnitName;
	}
	
	public EntityManagerFactory getEntityManagerFactory() {
		return factory;
	}
	
	public EntityManager getEntityManager() {
		EntityManager em = entityManager.get();
		if (em == null) {
			em = factory.createEntityManager();
			entityManager.set(em);
		}
		if (!em.getTransaction().isActive()) {
			em.getTransaction().begin();
		}
		return em;
	}
	
	public EntityManager startTransaction() {
		return startTransaction(null);
	}
	
	/**
	 * Starts a transaction explicitly. A transaction is started implicitly
	 * when {@link #getEntityManager()} is called.
	 * @param properties
	 * @return
	 */
	public EntityManager startTransaction(Map<String, String> properties) {
		EntityManager em = entityManager.get();
		if (em != null && em.getTransaction().isActive()) {
			em.getTransaction().commit();
		}
		if (properties != null) {
			em = factory.createEntityManager(properties);
		} else {
			em = factory.createEntityManager();
		}
		em.getTransaction().begin();
		entityManager.set(em);
		return em;
	}
	
	public void commitTransaction() {
		EntityManager em = entityManager.get();
		if (em != null) {
			if (em.getTransaction().isActive()) {
				em.flush();
				em.getTransaction().commit();
			}
			em.clear();
			em.close();
		}
		entityManager.set(null);
	}
	
	public void rollbackTransaction() {
		EntityManager em = entityManager.get();
		if (em != null) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			em.clear();
			em.close();
		}
		entityManager.set(null);
	}
}
