/*
 * @(#)DatabaseType.java	2010-4-23
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.jdbc;

/**
 * All database type, such as mysql,oracle, etc.
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: DatabaseType.java 85 2011-07-09 10:49:54Z gerald.chen.hz@gmail.com $
 */
public class DatabaseType {

	public static final int UNKNOWN_TYPE    = 0;
	
	public static final int MYSQL           = 1;
	public static final int MSSQL           = 2;
	public static final int ORACLE          = 3;
	public static final int DB2             = 4;
	public static final int H2              = 5;
	public static final int DERBY           = 6;
	public static final int PostgreSQL      = 7;
	public static final int HSQLDB          = 8;
	public static final int FIREBIRD        = 9;

	public static String[] DATABASE_NAME = new String[] {
		"Unknown database",
		"MySQL",
		"Microsoft SQL Server",
		"Oracle",
		"DB2", 
		"H2",
		"Derby",
		"PostgreSQL",
		"Hsqldb",
		"Firebird"
	};
	
	public static String getDatabaseName(final int type) {
		return DATABASE_NAME[type];
	}
}
