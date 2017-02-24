/*
 * @(#)DialectFactory.java	2011-8-6
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.sql;

import java.util.Map;

import com.github.javaclub.jorm.JormException;
import com.github.javaclub.jorm.common.CaseInsensitiveMap;
import com.github.javaclub.jorm.common.Reflections;
import com.github.javaclub.jorm.jdbc.sql.impl.H2Dialect;
import com.github.javaclub.jorm.jdbc.sql.impl.MySQLDialect;
import com.github.javaclub.jorm.jdbc.sql.impl.OracleDialect;
import com.github.javaclub.jorm.jdbc.sql.impl.PostgreSQLDialect;
import com.github.javaclub.jorm.jdbc.sql.impl.SQLServerDialect;

/**
 * DialectFactory
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: DialectFactory.java 446 2011-09-22 14:34:07Z gerald.chen.hz $
 */
@SuppressWarnings("unchecked")
public class DialectFactory {

	private static final Map DIALECTS = new CaseInsensitiveMap();
	static {
		DIALECTS.put("H2Dialect", H2Dialect.class);
		DIALECTS.put("MySQLDialect", MySQLDialect.class);
		DIALECTS.put("OracleDialect", OracleDialect.class);
		DIALECTS.put("SQLServerDialect", SQLServerDialect.class);
		DIALECTS.put("PostgreSQLDialect", PostgreSQLDialect.class);
	}
	
	public static Dialect create(String dialectName) {
		try {
			Class clazz = getDialectClass(dialectName);
			Dialect dialect = (Dialect) clazz.newInstance();
			return dialect;
		} catch (Exception e) {
			throw new JormException(
					"Could not instantiate dialect => " + dialectName, e);
		}
	}
	
	public static Class getDialectClass(String dialectName)
			throws JormException {
		Class clazz = (Class) DIALECTS.get(dialectName);
		try {
			if (clazz == null) {
				clazz = Reflections.classForName(dialectName);
			}
		} catch (ClassNotFoundException e) {
			throw new JormException("The dialect => " + dialectName
					+ " is not supported.");
		}
		return clazz;
	}
}
