/*
 * @(#)LazyInitializer.java	2011-8-29
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.proxy;

import java.io.Serializable;

import com.github.javaclub.jorm.JormException;
import com.github.javaclub.jorm.Session;

/**
 * LazyInitializer
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: LazyInitializer.java 272 2011-08-30 13:12:08Z gerald.chen.hz $
 */
public interface LazyInitializer {

	public void initialize() throws JormException;

	public Serializable getIdentifier();

	public void setIdentifier(Serializable id);

	public Class<?> getPersistentClass();

	public boolean isUninitialized();

	public Object getImplementation();

	public abstract Object getImplementation(Session session) throws JormException;

	public void setImplementation(Object target);

	public Session getSession();

	public void setSession(Session session) throws JormException;

	public void unsetSession();
	
}
