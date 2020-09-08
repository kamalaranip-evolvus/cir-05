/**
 * 
 */
package com.evolvus.dds.h2hconnectweb.dao.impl;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;

/**
 * Core generic JPA DAO implementation. Provides implementation for common
 * methods.
 * 
 * @author kumaresanb
 * @param <T>
 */
public abstract class AbstractDaoImpl<T extends Serializable> {
	/**
	 * Class on which DAO operations needs to be provided.
	 */
	private Class<T> clazz;

	/**
	 * EntityManager for the JPA implementation.
	 */
	@PersistenceContext
	protected EntityManager entityManager;

	/**
	 * Default Constructor. Protected constructor of the class. This is to
	 * prevent instantiation of the abstract class. Added as part of Sonar
	 * violations check.
	 */
	protected AbstractDaoImpl() {
		/*
		 * Protected constructor to prevent instantiation.
		 */
	}

	/**
	 * @Method delete
	 * @param @param entity
	 * @return void
	 * @author kumaresanb
	 * @date Sep 25, 2012
	 */
	public void delete(final T entity) {
		entityManager.remove(entity);
	}

	/**
	 * @Method deleteById
	 * @param @param entityId
	 * @return void
	 * @author kumaresanb
	 * @date Sep 25, 2012
	 */
	public void deleteById(final Long entityId) {
		final T entity = findById(entityId);

		delete(entity);
	}

	/**
	 * @Method findAll
	 * @param @return
	 * @return List<T>
	 * @author kumaresanb
	 * @date Sep 25, 2012
	 */
	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		return entityManager.createQuery("from " + clazz.getName())
				.getResultList();
	}

	/**
	 * @Method findById
	 * @param @param id
	 * @param @return
	 * @return T
	 * @author kumaresanb
	 * @date Sep 25, 2012
	 */
	public T findById(final Long entityId) {
		return entityManager.find(clazz, entityId);
	}

	/**
	 * Access method for the <code>clazz</code> attribute.
	 * 
	 * @return Class on which operations are to be performed.
	 */
	public Class<T> getClazz() {
		return clazz;
	}

	/**
	 * Access method for the <code>EntityManager</code> used by the JPA
	 * implementation.
	 * 
	 * @return EntityManager implementation being used.
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * @Method save
	 * @param @param entity
	 * @return void
	 * @author kumaresanb
	 * @date Sep 25, 2012
	 */
	public void save(final T entity) {
		entityManager.persist(entity);
	}

	/**
	 * @Method setClazz
	 * @param clazzToSet
	 * @return void
	 * @author root
	 * @date Sep 25, 2012
	 */
	public void setClazz(final Class<T> clazzToSet) {
		this.clazz = clazzToSet;
	}

	/**
	 * Set the <code>entityManager</code> to be used for the implementation.
	 * 
	 * @param entityManager
	 */
	public void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * @Method update
	 * @param @param entity
	 * @return void
	 * @author kumaresanb
	 * @date Sep 25, 2012
	 */
	public void update(final T entity) {
		entityManager.merge(entity);
	}

	public void save(final List<T> entities) {
		for (T entity : entities) {
			save(entity);
		}
	}

	public void update(final Iterable<T> entities) {
		for (T entity : entities) {
			update(entity);
		}
	}

	public void update(final List<T> entities) {
		for (T entity : entities) {
			update(entity);
		}
	}
	public void clear()
	{
		entityManager.flush();
		entityManager.clear();
	}

}
