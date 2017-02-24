/*
 * @(#)SimpleConnection.java	2010-4-21
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.jdbc.connection.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import com.github.javaclub.jorm.Environment;
import com.github.javaclub.jorm.JormException;
import com.github.javaclub.jorm.jdbc.DBUtil;
import com.github.javaclub.jorm.jdbc.JdbcException;
import com.github.javaclub.jorm.jdbc.connection.DBConnection;

/**
 * Non-pooled connection implementation. This class will ask a new conneciton to
 * the database on every <code>getConnection()</code> class. Uses of this class
 * include systems where a connection pool is not permited or the connections'
 * life time is too short, not justifying to have a connection pool.
 * 
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: SimpleConnection.java 442 2011-09-22 11:02:08Z gerald.chen.hz $
 */
public class SimpleConnection extends DBConnection {

	private Properties jdbcProperties;
	
	public boolean isOriginalConfigurationUsed() {
		return false;
	}

	public void init() throws JormException {
		try {
			Class.forName(getJdbcProperties().getProperty(Environment.DRIVER_CLASS));

			// Try to validate the connection url
			Connection conn = this.getConnection();

			if (conn != null) {
				this.releaseConnection(conn);
			}

		} catch (Throwable t) {
			t.printStackTrace();
			throw new JormException(t.getMessage(), t);
		}
	}

	public synchronized Connection getConnection() throws JdbcException {
		try {
			Properties jdbcProps = getJdbcProperties();
			return DriverManager.getConnection(jdbcProps.getProperty(Environment.JDBC_URL), jdbcProps
					.getProperty(Environment.USERNAME),jdbcProps.getProperty(Environment.PASSWORD));
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("getConnection()", e);
			throw new JdbcException(e);
		}
	}

	public void realReleaseAllConnections() throws JdbcException {
		DBConnection.getConnectionCache().remove(getJdbcProperties().getProperty(Environment.PROVIDER));
	}

	public void releaseConnection(Connection conn) {
		if(null == conn) {
			return;
		}
		DBUtil.closeQuietly(conn);
	}

	public Properties getJdbcProperties() {
		return jdbcProperties;
	}

	public void setJdbcProperties(Properties jdbcProperties) {
		this.jdbcProperties = jdbcProperties;
	}

}
