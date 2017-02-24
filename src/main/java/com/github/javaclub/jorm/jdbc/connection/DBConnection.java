/*
 * @(#)DBConnection.java	2010-4-20
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.jdbc.connection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.javaclub.jorm.Environment;
import com.github.javaclub.jorm.JormException;
import com.github.javaclub.jorm.config.JdbcConfigXmlParser;
import com.github.javaclub.jorm.jdbc.DBUtil;
import com.github.javaclub.jorm.jdbc.JdbcException;

/**
 * Base class for all database connection implementations.
 * 
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: DBConnection.java 1314 2012-01-08 12:03:50Z gerald.chen.hz $
 */
public abstract class DBConnection {
	/** Logger for this class */
	protected static final Log LOG = LogFactory.getLog(DBConnection.class);

	private static ConcurrentMap<String, DBConnection> connectionCache = new ConcurrentHashMap<String, DBConnection>();

	public static DBConnection getImplementation() {
		String defaultConnectionName = JdbcConfigXmlParser.getDefaultConnectionName();
		return getImplementation(defaultConnectionName);
	}

	public static DBConnection getImplementation(String providerName) {
		DBConnection conn = connectionCache.get(providerName);
		if (null != conn) {
			return conn;
		}

		Connection jdbcConnection = null;
		try {
			Properties jdbcPropertity = JdbcConfigXmlParser.getJdbcPropertity(providerName);
			jdbcPropertity.setProperty(Environment.PROVIDER, providerName);
			conn = (DBConnection) Class.forName(jdbcPropertity.getProperty(Environment.IMPLENTATION)).newInstance();
			conn.setJdbcProperties(jdbcPropertity);
			conn.init();
			jdbcConnection = conn.getConnection();// testing get a connection
			if(LOG.isDebugEnabled()) {
				listDatabaseInfo(jdbcConnection);
			}
			connectionCache.put(providerName, conn);
		} catch (Exception e) {
			e.printStackTrace();
			if (LOG.isWarnEnabled()) {
				LOG.warn("Error creating the database connection implementation instance. ", e);
			}
			throw new JormException("Error creating the database connection implementation instance. ", e);
		} finally {
			DBUtil.closeQuietly(jdbcConnection);
			if(LOG.isInfoEnabled()) {
				LOG.info("Memory after initialized: free/total="
						+ (Runtime.getRuntime().freeMemory() / 1024f) + "KB/"
						+ (Runtime.getRuntime().totalMemory() / 1024f) + "KB \t Memory occupancy rate="
						+ (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
						/ (float) Runtime.getRuntime().totalMemory());
			}
			System.gc();System.gc();System.gc();
			if(LOG.isInfoEnabled()) {
				LOG.info("Memory after garbage collection: free/total="
						+ (Runtime.getRuntime().freeMemory() / 1024f) + "KB/"
						+ (Runtime.getRuntime().totalMemory() / 1024f) + "KB \t Memory occupancy rate="
						+ (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
						/ (float) Runtime.getRuntime().totalMemory());
			}
		}
		return conn;
	}
	
	/**
	 * Inits the implementation. 
	 * Connection pools may use this method to init the connections from the
	 * database, while non-pooled implementation can provide an empty method
	 * block if no other initialization is necessary.
	 * <br>
	 * Please note that this method will be called just once, at system startup. 
	 * 
	 * @throws JormException
	 */
	public abstract void init() throws JormException;
	
	/**
	 * Gets a connection.
	 * Connection pools' normal behaviour will be to once connection
	 * from the pool, while non-pooled implementations will want to
	 * go to the database and get the connection in time the method
	 * is called.
	 * 
	 * @return Connection
	 */
	public abstract Connection getConnection() throws JdbcException;
	
	/**
	 * Releases a connection.
	 * Connection pools will want to put the connection back to the pool list,
	 * while non-pooled implementations should call <code>close()</code> directly
	 * in the connection object.
	 * 
	 * @param conn The connection to release
	 */
	public abstract void releaseConnection(Connection conn);
	
	/**
	 * Close all open connections.
	 * 
	 * @throws Exception
	 */
	public abstract void realReleaseAllConnections() throws JdbcException;
	
	public abstract Properties getJdbcProperties();
	
	public abstract void setJdbcProperties(Properties jdbcProperties);
	
	public abstract boolean isOriginalConfigurationUsed();
	
	public static void listDatabaseInfo(Connection connection) {
		try {
			DatabaseMetaData dbMetaData = connection.getMetaData();
			LOG.debug("database product name = " + dbMetaData.getDatabaseProductName());
			LOG.debug("database product version = " + dbMetaData.getDatabaseProductVersion());
			LOG.debug("database version = " + dbMetaData.getDatabaseMajorVersion() + "." + dbMetaData.getDatabaseMinorVersion());
			LOG.debug("database url = " + dbMetaData.getURL());
			LOG.debug("JDBC driver version = " + dbMetaData.getDriverMajorVersion() + "." + dbMetaData.getDriverMinorVersion());
			LOG.debug("database user name = " + dbMetaData.getUserName());
			LOG.debug("supports transactions = " + dbMetaData.supportsTransactions());
			LOG.debug("supports multiple transactions = " + dbMetaData.supportsMultipleTransactions());
			LOG.debug("supports transaction isolation level TRANSACTION_READ_COMMITTED = " + dbMetaData.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED));
			LOG.debug("supports transaction isolation level TRANSACTION_READ_UNCOMMITTED = " + dbMetaData.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_UNCOMMITTED));
			LOG.debug("supports transaction isolation level TRANSACTION_REPEATABLE_READ = " + dbMetaData.supportsTransactionIsolationLevel(Connection.TRANSACTION_REPEATABLE_READ));
			LOG.debug("supports transaction isolation level TRANSACTION_SERIALIZABLE = " + dbMetaData.supportsTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE));
			LOG.debug("supports result set TYPE_FORWARD_ONLY = "+ dbMetaData.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY));
			LOG.debug("supports result set TYPE_SCROLL_INSENSITIVE = " + dbMetaData.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE));
			LOG.debug("supports result set TYPE_SCROLL_SENSITIVE = " + dbMetaData.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE));
			LOG.debug("supports result set holdability CLOSE_CURSORS_AT_COMMIT = " + dbMetaData.supportsResultSetHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT));
			LOG.debug("supports result set holdability HOLD_CURSORS_OVER_COMMIT = " + dbMetaData.supportsResultSetHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT));
			LOG.debug("stores lower case identifiers = " + dbMetaData.storesLowerCaseIdentifiers());
			LOG.debug("stores lower case quoted identifiers = " + dbMetaData.storesLowerCaseQuotedIdentifiers());
			LOG.debug("stores upper case identifiers = " + dbMetaData.storesUpperCaseIdentifiers());
			LOG.debug("stores upper case quoted identifiers = " + dbMetaData.storesUpperCaseQuotedIdentifiers());
			LOG.debug("stores mixed case identifiers = " + dbMetaData.storesMixedCaseIdentifiers());
			LOG.debug("stores mixed case quoted identifiers = " + dbMetaData.storesMixedCaseQuotedIdentifiers());
			LOG.debug("Catalog term = " + dbMetaData.getCatalogTerm());
			LOG.debug("Schema term = " + dbMetaData.getSchemaTerm());
			LOG.debug("supports savepoints = " + dbMetaData.supportsSavepoints());
			LOG.debug("supports batchUpdates = " + dbMetaData.supportsBatchUpdates());
			LOG.debug("maximum concurrent connections = " + dbMetaData.getMaxConnections());

			String identifierQuoteString = dbMetaData.getIdentifierQuoteString();
			if (identifierQuoteString != null && identifierQuoteString.equals(" "))
				identifierQuoteString = "";

			LOG.debug("identifier quote string = '" + identifierQuoteString + "'");
			LOG.debug("supports generated keys = " + dbMetaData.supportsGetGeneratedKeys());
			LOG.debug("search string escape = " + dbMetaData.getSearchStringEscape());
		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
		}
	}

	public static Map<String, DBConnection> getConnectionCache() {
		return connectionCache;
	}
	
	public static void main(String[] args) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("1", new Integer(1));
		map.put("2", new Integer(2));
		
		map.remove("1");
		map.remove("1");
		
		try {
			Integer.parseInt("iii");
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			System.out.println("KKKKKKKKKKK");
			System.out.println();
		}
	}

}
