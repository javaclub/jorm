/*
 * @(#)ConnectionManager.java	2010-4-20
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.jdbc.connection;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.javaclub.jorm.jdbc.DBUtil;
import com.github.javaclub.jorm.jdbc.SessionFactory;

/**
 * ConnectionManager for the default connection.
 * 
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: ConnectionManager.java 217 2011-08-06 14:06:47Z gerald.chen.hz@gmail.com $
 */
public class ConnectionManager {

	private static final Log LOG = LogFactory.getLog(SessionFactory.class);
	private static ThreadLocal<ConnectionManager> userData = new ThreadLocal<ConnectionManager>();
	private static ConcurrentMap<String, Connection> cached = new ConcurrentHashMap<String, Connection>();

	private Connection conn;

	public static ConnectionManager get() {
		ConnectionManager manager = (ConnectionManager) userData.get();

		if (manager == null) {
			manager = new ConnectionManager();
			userData.set(manager);
		}

		return manager;
	}

	public static void set(ConnectionManager manager) {
		userData.set(manager);
	}

	/**
	 * Checks if there is a DefaultConnectionManager already set
	 * 
	 * @return <code>true</code> if there is an execution context
	 * @see #get()
	 */
	public static boolean exists() {
		return (userData.get() != null);
	}

	/**
	 * Sets a connection
	 * 
	 * @param conn The connection to use
	 */
	public void setConnection(Connection conn) {
		this.conn = conn;
	}

	/**
	 * Gets the current thread's connection
	 * 
	 * @return Connection
	 */
	public static Connection getConnection() {
		return getConnection(true);
	}
	
	public static synchronized Connection getConnection(String providerName) {
		Connection connection = DBConnection.getImplementation(providerName).getConnection();
		if(null != connection) {
			cached.put(providerName + ":" + connection.toString(), connection);
		}
		return connection;
	}

	public static Connection getConnection(boolean validate) {
		ConnectionManager manager = get();
		Connection c = manager.conn;

		if (validate && c == null) {
			c = DBConnection.getImplementation().getConnection();
			manager.setConnection(c);
			set(manager);
		}

		return c;
	}
	
	/**
	 * Close the specified connection.
	 *
	 * @param conn the specified connection.
	 */
	public void close(Connection conn) {
		DBUtil.closeQuietly(conn);
	}
	
	public static void destory() {
		if(LOG.isInfoEnabled()) {
			LOG.info("Destroying connections started...");
		}
		try {
			Connection connection = null;
			for (Map.Entry<String, Connection> entry : cached.entrySet()) {
				connection = entry.getValue();
				if(null != connection) {
					DBUtil.closeQuietly(connection);
					connection = null;
				}
			}
		} finally {
			cached.clear();
		}
		
		ConnectionManager manager = get();
		if(null != manager) {
			if(null != manager.conn) {
				DBUtil.closeQuietly(manager.conn);
			}
		}
		if(LOG.isInfoEnabled()) {
			LOG.info("Destroying connections ended...");
		}
	}

	public static void main(String[] args) {

	}

}
