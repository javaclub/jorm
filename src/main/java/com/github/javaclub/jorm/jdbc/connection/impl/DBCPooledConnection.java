/*
 * @(#)DBCPooledConnection.java	2011-9-21
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.connection.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import com.github.javaclub.jorm.Environment;
import com.github.javaclub.jorm.JormException;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.jdbc.DBUtil;
import com.github.javaclub.jorm.jdbc.JdbcException;
import com.github.javaclub.jorm.jdbc.connection.DBConnection;

/**
 * DBCPooledConnection
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: DBCPooledConnection.java 442 2011-09-22 11:02:08Z gerald.chen.hz $
 * @since 1.0.6
 */
public class DBCPooledConnection extends DBConnection {
	
	private BasicDataSource ds; 
	private Properties jdbcProperties;
	
	public boolean isOriginalConfigurationUsed() {
		String val = getJdbcProperties().getProperty(Environment.POOL_CFG_DBCP);
		return Strings.isNotEmpty(val);
	}

	public void init() throws JormException {
		if (null != this.ds) {
            try {      
            	this.ds.close();      
            } catch (Exception e) {      
            } finally {    
            	this.ds = null;
            }
        }
		
		Connection conn = null;
		try {      
            Properties p = new Properties();      
            p.setProperty("driverClassName", getJdbcProperties().getProperty(Environment.DRIVER_CLASS));
            p.setProperty("url", getJdbcProperties().getProperty(Environment.JDBC_URL)); 
            p.setProperty("username", getJdbcProperties().getProperty(Environment.USERNAME));
            p.setProperty("password", getJdbcProperties().getProperty(Environment.PASSWORD));     
            
            // 加载驱动并测试是否可以成功取得连接
			Class.forName(p.getProperty("driverClassName"));
			if(LOG.isInfoEnabled()) {
				LOG.info("Fetching a test connnection.");
			}
			conn = DriverManager.getConnection(p.getProperty("url"), p.getProperty("username"), p.getProperty("password"));
            
            String poolMinSize = "1"; String poolMaxSize = "28";
			try {
				Integer.parseInt(getJdbcProperties().getProperty(Environment.POOL_MIN));
			} catch (Exception e) {
				poolMinSize = "1";
			}
			try {
				Integer.parseInt(getJdbcProperties().getProperty(Environment.POOL_MAX));
			} catch (Exception e) {
				poolMaxSize = "28";
			}
			p.setProperty("initialSize", poolMinSize);   
            p.setProperty("maxActive", poolMaxSize);
            
            /* The maximum number of connections that can remain idle in the
             * pool, without extra ones being released, or negative for no limit.
             */
            p.setProperty("maxIdle", "10");      
            p.setProperty("maxWait", "30000");// 最长等待30秒
            if(!Strings.isEmpty(getJdbcProperties().getProperty(Environment.TEST_SQL))) {
            	p.setProperty("testOnBorrow", "true");
            }
            this.ds = (BasicDataSource) BasicDataSourceFactory.createDataSource(p);      
     
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
	
	public void releaseConnection(Connection conn) {
		if (null == conn) {
            return;
        }
		DBUtil.closeQuietly(conn);
	}

	public void realReleaseAllConnections() throws JdbcException {
		try {
			this.ds.close();
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
