/*
 * @(#)JormProxy.java	2011-8-29
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.proxy;

import java.io.Serializable;

/**
 * JormProxy
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: JormProxy.java 272 2011-08-30 13:12:08Z gerald.chen.hz $
 */
public interface JormProxy extends Serializable {

	/**
	 * Perform serialization-time write-replacement of this proxy.
	 *
	 * @return The serializable proxy replacement.
	 */
	public Object writeReplace();

	/**
	 * Get the underlying lazy initialization handler.
	 *
	 * @return The lazy initializer.
	 */
	public LazyInitializer getLazyInitializer();
}
