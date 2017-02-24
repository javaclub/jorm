/*
 * @(#)IsolatedWork.java	2011-9-8
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.work;

import java.sql.Connection;

import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.jdbc.JdbcException;

/**
 * IsolatedWork, the method {@link #doWork(Connection)} and {@link #doWork(Session)}
 * Only one is needed to be implmentation specified by {@link #getPreference()}.
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: IsolatedWork.java 345 2011-09-13 02:51:02Z gerald.chen.hz $
 */
public interface IsolatedWork {
	
	enum Preference {
		SESSION,
		CONNECTION
	}
	
	/**
	 * Perform the actual work to be done.
	 *
	 * @param connection The JDBC connection to use.
	 * @throws JdbcException
	 */
	public void doWork(Connection connection) throws JdbcException;
	
	/**
	 * Perform the actual work, using JdbcSession.
	 *
	 * @param session
	 * @throws JdbcException
	 */
	public void doWork(Session session) throws JdbcException;
	
	public Preference getPreference();
}
