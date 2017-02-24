/*
 * @(#)BytecodeProvider.java	2011-9-1
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.bytecode;


/**
 * BytecodeProvider
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: BytecodeProvider.java 2011-9-1 下午02:23:59 Exp $
 */
public interface BytecodeProvider {

	/**
	 * Retrieve the specific factory for this provider capable of
	 * generating run-time proxies for lazy-loading purposes.
	 *
	 * @return The provider specifc factory.
	 */
	public ProxyFactoryFactory getProxyFactoryFactory();
}
