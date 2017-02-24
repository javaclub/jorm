/*
 * @(#)JDaoException.java	2010-4-20
 *
 * Copyright (c) 2009 by jadmin. All Rights Reserved.
 */

package com.github.javaclub.jorm.jdbc;

import java.io.Serializable;

/**
 * Exception for Jdao in Jawa.
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: JdbcException.java 85 2011-07-09 10:49:54Z gerald.chen.hz@gmail.com $
 */
public class JdbcException extends RuntimeException implements Serializable {
	
	/** desc */
	private static final long serialVersionUID = -5931150905900106418L;

	public JdbcException() {
		super();
	}

	public JdbcException(String message) {
		super(message);
	}
	
	public JdbcException(Throwable cause) {
        super(cause);
    }

	public JdbcException(String message, Throwable cause) {
		super(message, cause);
	}
}
