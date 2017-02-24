/*
 * @(#)ProxyFactory.java	2011-8-29
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.proxy;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Set;

import com.github.javaclub.jorm.JormException;
import com.github.javaclub.jorm.Session;

/**
 * ProxyFactory
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: ProxyFactory.java 303 2011-09-06 10:47:38Z gerald.chen.hz $
 */
@SuppressWarnings("unchecked")
public interface ProxyFactory {

	/**
	 * Called immediately after instantiation of this factory.
	 * <p/>
	 * Essentially equivalent to contructor injection, but contracted
	 * here via interface.
	 *
	 * @param persistentClass The entity class for which to generate proxies;
	 * not always the same as the entityName.
	 * @param interfaces The interfaces to expose in the generated proxy;
	 * {@link JormProxy} is already included in this collection.
	 * @param getIdentifierMethod Reference to the identifier getter method;
	 * invocation on this method should not force initialization
	 * @param setIdentifierMethod Reference to the identifier setter method;
	 * invocation on this method should not force initialization
	 * @throws JormException
	 */
	public void postInstantiate(
	        Class persistentClass,
	        Set interfaces,
	        Method getIdentifierMethod,
	        Method setIdentifierMethod) throws JormException;

	public JormProxy getProxy(Serializable id, Session session) throws JormException;

}
