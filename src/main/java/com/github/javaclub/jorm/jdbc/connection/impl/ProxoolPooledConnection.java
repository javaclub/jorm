/*
 * @(#)ProxoolPooledConnection.java	2011-9-22
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.connection.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.github.javaclub.jorm.Environment;
import com.github.javaclub.jorm.JormException;
import com.github.javaclub.jorm.common.AssertUtil;
import com.github.javaclub.jorm.common.CommonUtil;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.common.xml.XmlUtil;
import com.github.javaclub.jorm.jdbc.DBUtil;
import com.github.javaclub.jorm.jdbc.JdbcException;
import com.github.javaclub.jorm.jdbc.connection.DBConnection;
import org.logicalcobwebs.proxool.ProxoolFacade;
import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;

/**
 * ProxoolPooledConnection for proxool.
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: ProxoolPooledConnection.java 2011-9-22 上午10:57:23 Exp $
 * @since 1.0.6
 */
public class ProxoolPooledConnection extends DBConnection {
	
	private String alias;
	private Properties jdbcProperties;
	private Properties proxoolProperties;
	
	public boolean isOriginalConfigurationUsed() {
		String val = getJdbcProperties().getProperty(Environment.POOL_CFG_PROXOOL);
		return Strings.isNotEmpty(val);
	}
	
	public void init() throws JormException {
		Connection conn = null;
		try {
			if(isOriginalConfigurationUsed()) {
				String proxoolXmlFile = getJdbcProperties().getProperty(Environment.POOL_CFG_PROXOOL);
				AssertUtil.notEmpty(proxoolXmlFile, "The proxool xml configuration file path can't be empty.");
				String xmlPath = CommonUtil.toAbsolutePath(proxoolXmlFile);
				JAXPConfigurator.configure(xmlPath, false);
				this.alias = XmlUtil.selectText(xmlPath, "//proxool/alias/text()");
				if(LOG.isInfoEnabled()) {
					LOG.info("Fetching a test connnection.");
				}
				conn = DriverManager.getConnection(getProxoolUrl());
			} else {
				Properties info = new Properties();
				info.setProperty("user", getJdbcProperties().getProperty(Environment.USERNAME));
				info.setProperty("password", getJdbcProperties().getProperty(Environment.PASSWORD));
				info.setProperty("proxool.minimum-connection-count", getJdbcProperties().getProperty(Environment.POOL_MIN));
				info.setProperty("proxool.maximum-connection-count", getJdbcProperties().getProperty(Environment.POOL_MAX));
				String testSql = getJdbcProperties().getProperty(Environment.TEST_SQL);
				if(Strings.isNotEmpty(testSql)) {
					info.setProperty("proxool.house-keeping-test-sql", testSql);
				}
				this.proxoolProperties = info;
				this.alias = "db_alias";
				
				String jdbcUrl = this.getProxoolUrl() + ":" 
						+ getJdbcProperties().getProperty(Environment.DRIVER_CLASS) + ":" 
						+ getJdbcProperties().getProperty(Environment.JDBC_URL);
				ProxoolFacade.registerConnectionPool(jdbcUrl, getProxoolProperties());
				
				if(LOG.isInfoEnabled()) {
					LOG.info("Fetching a test connnection.");
				}
				conn = DriverManager.getConnection(getProxoolUrl());
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
			return DriverManager.getConnection(this.getProxoolUrl());
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error("getConnection()", e);
			throw new JdbcException(e);
		}
	}
	
	public void realReleaseAllConnections() throws JdbcException {
		try {
			ProxoolFacade.removeConnectionPool(getAlias());
		} catch (Exception e) {
			e.printStackTrace();
			throw new JdbcException(e);
		}
	}

	public void releaseConnection(Connection conn) {
		if (null == conn) {
            return;
        }
		DBUtil.closeQuietly(conn);
	}

	public void setJdbcProperties(Properties jdbcProperties) {
		this.jdbcProperties = jdbcProperties;
	}
	
	public Properties getJdbcProperties() {
		return this.jdbcProperties;
	}
	
	protected final Properties getProxoolProperties() {
		return this.proxoolProperties;
	}
	
	protected final String getAlias() {
		return this.alias;
	}
	
	protected final String getProxoolUrl() {
		if(Strings.isEmpty(getAlias())) {
			throw new IllegalStateException("The pool alias is not initialized.");
		}
		return "proxool." + this.getAlias();
	}

}
