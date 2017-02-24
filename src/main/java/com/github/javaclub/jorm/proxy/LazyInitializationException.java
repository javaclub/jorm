/*
 * @(#)LazyInitializationException.java	2011-9-1
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.proxy;

import org.apache.commons.logging.LogFactory;
import com.github.javaclub.jorm.JormException;

/**
 * LazyInitializationException
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: LazyInitializationException.java 2011-9-1 上午11:34:49 Exp $
 */
public class LazyInitializationException extends JormException {

	private static final long serialVersionUID = 1L;

	public LazyInitializationException(String msg) {
		super(msg);
		LogFactory.getLog(LazyInitializationException.class).error(msg, this);
	}
}
