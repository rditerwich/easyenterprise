package easyenterprise.lib.util;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;

public class EntityManagerDelegate implements EntityManager {

	protected final EntityManager entityManager;

	public EntityManagerDelegate(EntityManager delegate) {
		this.entityManager = delegate;
	}

	public void clear() {
		entityManager.clear();
	}

	public void close() {
		entityManager.close();
	}

	public boolean contains(Object arg0) {
		return entityManager.contains(arg0);
	}

	public <T> TypedQuery<T> createNamedQuery(String arg0, Class<T> arg1) {
		return entityManager.createNamedQuery(arg0, arg1);
	}

	public Query createNamedQuery(String arg0) {
		return entityManager.createNamedQuery(arg0);
	}

	public Query createNativeQuery(String arg0, Class arg1) {
		return entityManager.createNativeQuery(arg0, arg1);
	}

	public Query createNativeQuery(String arg0, String arg1) {
		return entityManager.createNativeQuery(arg0, arg1);
	}

	public Query createNativeQuery(String arg0) {
		return entityManager.createNativeQuery(arg0);
	}

	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> arg0) {
		return entityManager.createQuery(arg0);
	}

	public <T> TypedQuery<T> createQuery(String arg0, Class<T> arg1) {
		return entityManager.createQuery(arg0, arg1);
	}

	public Query createQuery(String arg0) {
		return entityManager.createQuery(arg0);
	}

	public void detach(Object arg0) {
		entityManager.detach(arg0);
	}

	public <T> T find(Class<T> arg0, Object arg1, LockModeType arg2,
			Map<String, Object> arg3) {
		return entityManager.find(arg0, arg1, arg2, arg3);
	}

	public <T> T find(Class<T> arg0, Object arg1, LockModeType arg2) {
		return entityManager.find(arg0, arg1, arg2);
	}

	public <T> T find(Class<T> arg0, Object arg1, Map<String, Object> arg2) {
		return entityManager.find(arg0, arg1, arg2);
	}

	public <T> T find(Class<T> arg0, Object arg1) {
		return entityManager.find(arg0, arg1);
	}

	public void flush() {
		entityManager.flush();
	}

	public CriteriaBuilder getCriteriaBuilder() {
		return entityManager.getCriteriaBuilder();
	}

	public Object getDelegate() {
		return entityManager.getDelegate();
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManager.getEntityManagerFactory();
	}

	public FlushModeType getFlushMode() {
		return entityManager.getFlushMode();
	}

	public LockModeType getLockMode(Object arg0) {
		return entityManager.getLockMode(arg0);
	}

	public Metamodel getMetamodel() {
		return entityManager.getMetamodel();
	}

	public Map<String, Object> getProperties() {
		return entityManager.getProperties();
	}

	public <T> T getReference(Class<T> arg0, Object arg1) {
		return entityManager.getReference(arg0, arg1);
	}

	public EntityTransaction getTransaction() {
		return entityManager.getTransaction();
	}

	public boolean isOpen() {
		return entityManager.isOpen();
	}

	public void joinTransaction() {
		entityManager.joinTransaction();
	}

	public void lock(Object arg0, LockModeType arg1, Map<String, Object> arg2) {
		entityManager.lock(arg0, arg1, arg2);
	}

	public void lock(Object arg0, LockModeType arg1) {
		entityManager.lock(arg0, arg1);
	}

	public <T> T merge(T arg0) {
		return entityManager.merge(arg0);
	}

	public void persist(Object arg0) {
		entityManager.persist(arg0);
	}

	public void refresh(Object arg0, LockModeType arg1, Map<String, Object> arg2) {
		entityManager.refresh(arg0, arg1, arg2);
	}

	public void refresh(Object arg0, LockModeType arg1) {
		entityManager.refresh(arg0, arg1);
	}

	public void refresh(Object arg0, Map<String, Object> arg1) {
		entityManager.refresh(arg0, arg1);
	}

	public void refresh(Object arg0) {
		entityManager.refresh(arg0);
	}

	public void remove(Object arg0) {
		entityManager.remove(arg0);
	}

	public void setFlushMode(FlushModeType arg0) {
		entityManager.setFlushMode(arg0);
	}

	public void setProperty(String arg0, Object arg1) {
		entityManager.setProperty(arg0, arg1);
	}

	public <T> T unwrap(Class<T> arg0) {
		return entityManager.unwrap(arg0);
	}

	
}
