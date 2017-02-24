/*
 * @(#)Environment.java	2010-4-20
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.javaclub.jorm.bytecode.BytecodeProvider;
import com.github.javaclub.jorm.config.ConstantElement;
import com.github.javaclub.jorm.config.JdbcConfigXmlParser;

/**
 * Jdbc Environment.
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: Environment.java 525 2011-10-08 10:30:18Z gerald.chen.hz $
 */
public class Environment {
	
	/** Logger for this class */
	protected static final Log LOG = LogFactory.getLog(Environment.class);
	
	public static final String PROVIDER = "connection.provider.name";
	
	public static final String DIALECT = "connection.dialect";
	
	public static final String JDBC_URL = "connection.jdbcurl";
	
	public static final String DATABASE = "connection.database";
	
	public static final String USERNAME = "connection.username";
	
	public static final String PASSWORD = "connection.password";
	
	public static final String IMPLENTATION = "connection.implementation";
	
	public static final String DRIVER_CLASS = "connection.driver";
	
	public static final String POOL_MIN = "connection.pool.min";
	
	public static final String POOL_MAX = "connection.pool.max";
	
	public static final String IDLE_TIME = "connection.idle.time";
	
	public static final String TEST_SQL = "connection.test.sql";
	
	public static final String JNDI_URL = "jndi.url";
	
	public static final String SHOW_SQL = "show_sql";
	
	public static final String DBTYPE_DB2 = "DB2";
	
	public static final String DBTYPE_PSQL = "POSTGRESQL";
	
	public static final String DBTYPE_ORACLE = "ORACLE";
	
	public static final String DBTYPE_SQLSERVER = "SQLSERVER";
	
	public static final String DBTYPE_MYSQL = "MYSQL";
	
	public static final String DBTYPE_H2 = "H2";
	
	public static final String DBTYPE_HSQLDB = "HSQLDB";
	
	// for database pool framework
	public static final String POOL_CFG_C3P0 = "connection.pool.c3p0";
	public static final String POOL_CFG_DBCP = "connection.pool.dbcp";
	public static final String POOL_CFG_BONECP = "connection.pool.bonecp";
	public static final String POOL_CFG_PROXOOL = "connection.pool.proxool";
	
	private static final BytecodeProvider BYTECODE_PROVIDER_INSTANCE;
	
	static {
		ConstantElement constantElement = JdbcConfigXmlParser.constant("bytecode.provider");
		String byteprovider = constantElement != null ? constantElement.getValue() : null;
		BYTECODE_PROVIDER_INSTANCE = buildBytecodeProvider(byteprovider);
	}
	
	public static boolean support(String className) {
		boolean flag = false;
		try {
			Class.forName(className);
			flag = true;
		} catch (ClassNotFoundException e) {
			// do nothing
		}
		return flag;
	}
	
	public static BytecodeProvider getBytecodeProvider() {
		return BYTECODE_PROVIDER_INSTANCE;
	}
	
	private static BytecodeProvider buildBytecodeProvider(String providerName) {
		if ( "javassist".equals( providerName ) ) {
			return new com.github.javaclub.jorm.bytecode.javassist.BytecodeProviderImpl();
		} else if ( "cglib".equals( providerName ) ) {
			return new com.github.javaclub.jorm.bytecode.cglib.BytecodeProviderImpl();
		}

		if(LOG.isInfoEnabled()) {
			LOG.info( "unrecognized bytecode provider [" + providerName + "], using cglib by default." );
		}
		
		return new com.github.javaclub.jorm.bytecode.cglib.BytecodeProviderImpl();
	}
	
	// ==================== Database native properties =================
	public static final String SUPPORT_TRANSACTIONS = "supportsTransactions";
	public static final String SUPPORT_BATCH_UPDATE = "supportsBatchUpdates";
	public static final String SUPPORT_SAVEPOINTS = "supportsSavepoints";
	
	public static final String DB_NAME = "productName";
	public static final String PRODUCT_VERSION = "productVersion";// 可能含有其它的字符串
	public static final String DB_VERSION = "databaseVersion"; // MajorVersion.MinorVersion
	public static final String DB_ISOLATION_LEVEL = "defaultTransactionIsolation";
}
