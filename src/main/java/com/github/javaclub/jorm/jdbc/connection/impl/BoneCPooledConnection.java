/*
 * @(#)BoneCPooledConnection.java	2011-9-22
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.connection.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.github.javaclub.jorm.Environment;
import com.github.javaclub.jorm.JormException;
import com.github.javaclub.jorm.common.AssertUtil;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.jdbc.DBUtil;
import com.github.javaclub.jorm.jdbc.JdbcException;
import com.github.javaclub.jorm.jdbc.connection.DBConnection;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

/**
 * BoneCPooledConnection
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: BoneCPooledConnection.java 2011-9-22 下午12:23:10 Exp $
 * @since 1.0.6
 */
public class BoneCPooledConnection extends DBConnection {
	
	private BoneCP bonecp;
	private Properties jdbcProperties;
	
	public boolean isOriginalConfigurationUsed() {
		String val = getJdbcProperties().getProperty(Environment.POOL_CFG_BONECP);
		return Strings.isNotEmpty(val);
	}

	public void init() throws JormException {
		Connection conn = null;
		try {
			String driverClass = getJdbcProperties().getProperty(Environment.DRIVER_CLASS);
			Class.forName(driverClass);
			
			BoneCPConfig config = null;
			if(isOriginalConfigurationUsed()) {
				config = new BoneCPConfig();
				String bonecpXmlFile = getJdbcProperties().getProperty(Environment.POOL_CFG_BONECP);
				AssertUtil.notEmpty(bonecpXmlFile, "The BoneCP xml configuration file path can't be empty.");
				if(bonecpXmlFile.startsWith("classpath:")) {
					bonecpXmlFile = bonecpXmlFile.substring(10);
				}
				config.setConfigFile(bonecpXmlFile);
			} else {
				config = new BoneCPConfig();	// create a new configuration object
			 	config.setJdbcUrl(getJdbcProperties().getProperty(Environment.JDBC_URL));	// set the JDBC url
				config.setUsername(getJdbcProperties().getProperty(Environment.USERNAME));	// set the username
				config.setPassword(getJdbcProperties().getProperty(Environment.PASSWORD));
				// 设置分区  分区数为3
	            config.setPartitionCount(3);
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
				// 设置每个分区中的最小连接数 10
	            config.setMinConnectionsPerPartition(poolMinSize);
				// 设置每个分区中的最大连接数 
				config.setMaxConnectionsPerPartition(poolMaxSize);
			}
			this.bonecp = new BoneCP(config);
			if(LOG.isInfoEnabled()) {
				LOG.info("Fetching a test connnection.");
			}
			conn = bonecp.getConnection();
		} catch (Throwable t) {
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
			return bonecp.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("getConnection()", e);
			throw new JdbcException(e);
		}
	}
	
	public void releaseConnection(Connection conn) {
		DBUtil.closeQuietly(conn);
	}

	public void realReleaseAllConnections() throws JdbcException {
		try {
			bonecp.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
			throw new JdbcException(e);
		}
	}
	
	public Properties getJdbcProperties() {
		return this.jdbcProperties;
	}

	public void setJdbcProperties(Properties jdbcProperties) {
		this.jdbcProperties = jdbcProperties;
	}

}
