/*
 * @(#)DataSourceConnection.java	2010-4-21
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.jdbc.connection.impl;

import java.sql.Connection;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.github.javaclub.jorm.Environment;
import com.github.javaclub.jorm.JormException;
import com.github.javaclub.jorm.jdbc.DBUtil;
import com.github.javaclub.jorm.jdbc.JdbcException;
import com.github.javaclub.jorm.jdbc.connection.DBConnection;

/**
 * DataSource connection implementation. The datasourcename should be set in the
 * key <code>system.jdbc.datasource.name</code> at system.properties.
 * 
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: DataSourceConnection.java 442 2011-09-22 11:02:08Z gerald.chen.hz $
 */
public class DataSourceConnection extends DBConnection {

	private DataSource ds;
	private Properties jdbcProperties;
	
	public boolean isOriginalConfigurationUsed() {
		return false;
	}
	
	public void init() throws JormException {
		try {
			Context context = new InitialContext();
			this.ds = (DataSource) context.lookup(getJdbcProperties().getProperty(Environment.JNDI_URL));
		} catch (Throwable t) {
			t.printStackTrace();
			throw new JormException(t.getMessage(), t);
		}
	}

	public synchronized Connection getConnection() throws JdbcException {
		try {
			return this.ds.getConnection();
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
