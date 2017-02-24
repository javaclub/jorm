/*
 * @(#)PersistentCollection.java	2011-9-19
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.collection;

import java.util.Collection;

import com.github.javaclub.jorm.jdbc.JdbcException;

/**
 * Applications should never use classes in this package directly, unless extending the "framework" here.
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: PersistentCollection.java 2011-9-19 上午10:49:10 Exp $
 */
public interface PersistentCollection {
	
	boolean hasNext();

    Object next();
    
	/**
	 * Get all the associated elements.
	 *
	 * @return all elements that can be loaded.
	 * @throws JdbcException
	 */
	public <T> Collection<T> presentAll() throws JdbcException;
	
	/**
	 * Get the owning entity. Note that the owner is only
	 * set during the flush cycle, and when a new collection
	 * wrapper is created while loading an entity.
	 */
	public Object getOwner();
	/**
	 * Set the reference to the owning entity
	 */
	public void setOwner(Object entity);
	
	/**
	 * The collection's size
	 */
	public long count();

	/**
	 * Tests if the collection had been changed
	 *
	 * @return true if collection is changed, false otherwise.
	 */
	public boolean isDirty();
	
	/**
	 * Clear the dirty flag, after flushing changes
	 * to the database.
	 */
	public void clearDirty();
	
	/**
	 * Mark the collection as dirty
	 */
	public void dirty();
	
	/**
	 * Gets the persistent entity's Class.
	 *
	 * @return the persistent class type
	 */
	public Class<?> getPersistentClass();
	
}
