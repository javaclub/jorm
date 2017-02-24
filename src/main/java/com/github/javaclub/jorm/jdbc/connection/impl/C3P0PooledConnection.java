/*
 * @(#)C3P0PooledConnection.java	2010-4-21
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.jdbc.connection.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.github.javaclub.jorm.Environment;
import com.github.javaclub.jorm.JormException;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.jdbc.DBUtil;
import com.github.javaclub.jorm.jdbc.JdbcException;
import com.github.javaclub.jorm.jdbc.connection.DBConnection;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

/**
 * The c3p0 implmentation of DBConnection.
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: C3P0PooledConnection.java 442 2011-09-22 11:02:08Z gerald.chen.hz $
 */
public class C3P0PooledConnection extends DBConnection {
	
	private ComboPooledDataSource ds;
	private Properties jdbcProperties;
	
	public boolean isOriginalConfigurationUsed() {
		String val = getJdbcProperties().getProperty(Environment.POOL_CFG_C3P0);
		return Strings.isNotEmpty(val);
	}
	
	public void init() throws JormException {
		Connection conn = null;
		try {
			this.ds = new ComboPooledDataSource();
			String driver = getJdbcProperties().getProperty(Environment.DRIVER_CLASS);
			String url = getJdbcProperties().getProperty(Environment.JDBC_URL);
			String username = getJdbcProperties().getProperty(Environment.USERNAME);
			String password = getJdbcProperties().getProperty(Environment.PASSWORD);
			int poolMinSize = 1; int poolMaxSize = 28;
			try {
				poolMinSize = Integer.parseInt(getJdbcProperties().getProperty(Environment.POOL_MIN));
			} catch (Exception e) {
				poolMinSize = 1;
			}
			try {
				poolMaxSize = Integer.parseInt(getJdbcProperties().getProperty(Environment.POOL_MAX));
			} catch (Exception e) {
				poolMaxSize = 28;
			}
			String idleTime = getJdbcProperties().getProperty(Environment.IDLE_TIME);
			String querySql = getJdbcProperties().getProperty(Environment.TEST_SQL);
		
			// 加载驱动并测试是否可以成功取得连接
			Class.forName(driver);
			if(LOG.isInfoEnabled()) {
				LOG.info("Fetching a test connnection.");
			}
			conn = DriverManager.getConnection(url, username, password);
			
			this.ds.setDriverClass(driver);
			this.ds.setJdbcUrl(url);
			this.ds.setMinPoolSize(poolMinSize);
			this.ds.setMaxPoolSize(poolMaxSize);
			this.ds.setUser(username);
			this.ds.setPassword(password);
			if(!Strings.isEmpty(idleTime)) {
				this.ds.setIdleConnectionTestPeriod(Integer.parseInt(idleTime));
			}
			if(!Strings.isEmpty(querySql)) {
				this.ds.setPreferredTestQuery(querySql);
			}
		} catch(Throwable t) {
			t.printStackTrace();
			if(t instanceof JormException) {
				throw (JormException) t;
			} else {
				throw new JormException(t.getMessage(), t);
			}
		} finally {
			if(null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
				} finally {
					conn = null;
					if(LOG.isInfoEnabled()) {
						LOG.info("Closed the test connnection.");
					}
				}
			}
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
		try {
			DataSources.destroy(this.ds);
		} catch (Exception e) {
			throw new JdbcException(e);
		} finally {
			DBConnection.getConnectionCache().remove(getJdbcProperties().getProperty(Environment.PROVIDER));
		}
		
	}

	public void releaseConnection(Connection conn) {
		if (null == conn) {
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
