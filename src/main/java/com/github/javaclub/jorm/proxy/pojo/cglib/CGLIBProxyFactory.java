/*
 * @(#)CGLIBProxyFactory.java	2011-9-1
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.proxy.pojo.cglib;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Set;

import com.github.javaclub.jorm.JormException;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.proxy.JormProxy;
import com.github.javaclub.jorm.proxy.ProxyFactory;

/**
 * CGLIBProxyFactory
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: CGLIBProxyFactory.java 2011-9-1 上午11:54:49 Exp $
 */
@SuppressWarnings("unchecked")
public class CGLIBProxyFactory implements ProxyFactory {
	
	protected static final Class[] NO_CLASSES = new Class[0];

	private Class persistentClass;
	private Class[] interfaces;
	private Method getIdentifierMethod;
	private Method setIdentifierMethod;
	private Class factory;
	
	public void postInstantiate(Class persistentClass, Set interfaces,
			Method getIdentifierMethod, Method setIdentifierMethod)
			throws JormException {
		this.persistentClass = persistentClass;
		this.interfaces = (Class[]) interfaces.toArray(NO_CLASSES);
		this.getIdentifierMethod = getIdentifierMethod;
		this.setIdentifierMethod = setIdentifierMethod;
		factory = CGLIBLazyInitializer.getProxyFactory(persistentClass, this.interfaces);
	}


	public JormProxy getProxy(Serializable id, Session session)
			throws JormException {
		return CGLIBLazyInitializer.getProxy(
				factory, 
				persistentClass, 
				interfaces, 
				getIdentifierMethod, 
				setIdentifierMethod,
				id, 
				session);
	}

}
