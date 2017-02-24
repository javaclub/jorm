/*
 * @(#)BasicProxyFactory.java	2011-8-30
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.bytecode;

/**
 * A proxy factory for "basic proxy" generation
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: BasicProxyFactory.java 2011-8-30 下午09:07:22 Exp $
 */
public interface BasicProxyFactory {

	/**
	 * Get a proxy reference.
	 *
	 * @return A proxy reference.
	 */
	public Object getProxy();
}
