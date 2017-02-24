/*
 * @(#)FailureLog.java	2011-9-1
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * FailureLog
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: FailureLog.java 2011-9-1 10:49:46 Exp $
 */
public class FailureLog extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog( FailureLog.class );

	private static final String MESSAGE = "An assertion failure occured" +
			" (this may indicate a bug in Jorm)";

	public FailureLog(String s) {
		super( s );
		log.error( MESSAGE, this );
	}

	public FailureLog(String s, Throwable t) {
		super( s, t );
		log.error( MESSAGE, t );
	}
}
