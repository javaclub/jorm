/*
 * @(#)Jorm.java	2010-4-21
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm;

import java.sql.Connection;

import com.github.javaclub.jorm.config.JdbcConfigXmlParser;
import com.github.javaclub.jorm.jdbc.JdbcSession;
import com.github.javaclub.jorm.jdbc.SessionFactory;
import com.github.javaclub.jorm.jdbc.connection.ConnectionManager;
import com.github.javaclub.jorm.jdbc.sql.util.BasicFormatterImpl;
import com.github.javaclub.jorm.jdbc.sql.util.DDLFormatterImpl;
import com.github.javaclub.jorm.jdbc.sql.util.Formatter;
import com.github.javaclub.jorm.jdbc.sql.util.SqlUtil;
import com.github.javaclub.jorm.proxy.JormProxy;

/**
 * A utility class for Jorm framework.
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: Jorm.java 541 2011-10-09 07:10:20Z gerald.chen.hz $
 */
public class Jorm {
	
	public static Session getSession() {
		return SessionFactory.currentSession();
	}
	
	public static Session newSession() {
		return SessionFactory.newSession();
	}
	
	public static synchronized Session getSession(boolean autoClose) {
		return JdbcSession.getSession(JdbcConfigXmlParser.getDefaultConnectionName(), 
				               autoClose);
	}
	
	public static Connection getConnection() {
		return ConnectionManager.getConnection();
	}
	
	public static Session getSession(String providerName) {
		return SessionFactory.getSession(providerName);
	}
	
	public static Connection getConnection(String providerName) {
		return ConnectionManager.getConnection(providerName);
	}
	
	public static void free() {
		SessionFactory.destory();
		ConnectionManager.destory();
	}
	
	public static boolean isInitialize(Object proxy) {
		if ( proxy instanceof JormProxy ) {
			return !( ( JormProxy ) proxy ).getLazyInitializer().isUninitialized();
		} else {
			return true;
		}
	}
	
	public static void initialize(Object proxy) throws JormException {
		if ( proxy == null ) {
			return;
		}
		else if ( proxy instanceof JormProxy ) {
			( ( JormProxy ) proxy ).getLazyInitializer().initialize();
		}
	}
	
	public static boolean isProxy(Object proxy) {
		if ( proxy instanceof JormProxy ) {
			return true;
		}
		return false;
	}
	
	public static Object getTarget(Object proxy) {
		if ( proxy instanceof JormProxy ) {
			return ( ( JormProxy ) proxy ).getLazyInitializer()
					.getImplementation();
		} else {
			return proxy;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Class getClass(Object proxy) {
		if ( proxy instanceof JormProxy ) {
			return ( ( JormProxy ) proxy ).getLazyInitializer()
					.getImplementation()
					.getClass();
		} else {
			return proxy.getClass();
		}
	}
	
	public static void format(String sql) {
		if(!JdbcConfigXmlParser.isFormatSql()) {
			System.out.println("JDBC: " + sql);
		} else {
			if(SqlUtil.isDdl(sql)) {
				System.out.println("JDBC: " + ddlFormatter.format(sql));
			} else {
				System.out.println("JDBC: " + dmlFormatter.format(sql));
			}
		}
	}
	
	public static void console(boolean prefix, String txt) {
		if(prefix) {
			System.out.println("JORM: " + txt);
		} else {
			System.out.println(txt);
		}
	}

	
	public static final String TEMP_TBNAME = "jorm_tb_";
	
	public static final String IGNORE_COLUMN_PREFIX = "ignore_col_";
	public static final String IGNORE_COLUMN_SUFFIX = "_ignore_col_";
	
	public static final String SELECT_ENTITY_COLUMN_PREFIX = "slct0_0ntit_";

	private static Formatter dmlFormatter = new BasicFormatterImpl();
	private static Formatter ddlFormatter = new DDLFormatterImpl();
}
