/*
 * @(#)ProxyFactoryFactory.java	2011-9-1
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.bytecode;

import com.github.javaclub.jorm.proxy.ProxyFactory;

/**
 * An interface for factories of {@link ProxyFactory proxy factory} instances.
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: ProxyFactoryFactory.java 2011-9-1 下午01:52:21 Exp $
 */
public interface ProxyFactoryFactory {

	/**
	 * Build a proxy factory specifically for handling runtime
	 * lazy loading.
	 *
	 * @return The lazy-load proxy factory.
	 */
	public ProxyFactory buildProxyFactory();

	/**
	 * Build a proxy factory for basic proxy concerns.  The return
	 * should be capable of properly handling newInstance() calls.
	 * <p/>
	 * Should build basic proxies essentially equivalent to JDK proxies in
	 * terms of capabilities, but should be able to deal with abstract super
	 * classes in addition to proxy interfaces.
	 * <p/>
	 * Must pass in either superClass or interfaces (or both).
	 *
	 * @param superClass The abstract super class (or null if none).
	 * @param interfaces Interfaces to be proxied (or null if none).
	 * @return The proxy class
	 */
	@SuppressWarnings("unchecked")
	public BasicProxyFactory buildBasicProxyFactory(Class superClass, Class[] interfaces);
}
