/*
 * @(#)MySQLDialect.java	May 7, 2009
 *
 * Copyright (c) 2009 by jadmin. All Rights Reserved.
 */

package com.github.javaclub.jorm.jdbc.sql.impl;

import java.lang.reflect.Field;

import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.jdbc.ClassMetadata;

/**
 * SqlDialect for database MySQL.
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: MySQLDialect.java 539 2011-10-09 03:48:18Z gerald.chen.hz $
 */
public class MySQLDialect extends AbstractDialect {
	
	public MySQLDialect() {
		super();
	}
	
	public String pageable(String sql, int start, int limit) {
		if (hasLimit(sql)) {
			int lastLimitPos = Strings.upperCase(sql).lastIndexOf(LIMIT);
			return sql.substring(0, lastLimitPos) + " " + LIMIT + " " + start
					+ ", " + limit;
		}
		return sql + " " + LIMIT + " " + start + ", " + limit;
		
	}

	public boolean supportsLimit() {
		return true;
	}

	public String getSelectGuidSql() {
		return "SELECT uuid()";
	}
	
	public String ddlIdentityColumn() {
		return "id bigint(20) NOT NULL AUTO_INCREMENT";
	}
	
	public String ddlFieldColumn(Class<?> entityType) {
		Field identity = ClassMetadata.getClassMetadata(entityType).identifierField;
		Class<?> identityType = identity.getType();
		if("int".equals(identityType.getName()) || "java.lang.Integer".equals(identityType.getName())) {
			return Strings.lowerCase(entityType.getSimpleName()) + "_id int(11)";
		} else if("long".equals(identityType.getName()) || "java.lang.Long".equals(identityType.getName())) {
			return Strings.lowerCase(entityType.getSimpleName()) + "_id bigint(20)";
		}
		return Strings.lowerCase(entityType.getSimpleName()) + "_id varchar(64)";
	}

	public String nextIdentitySql(String dbname, Class<?> clazz) {
		return "SELECT auto_increment FROM information_schema.TABLES WHERE TABLE_SCHEMA = '"
				+ dbname
				+ "' AND table_name = '"
				+ ClassMetadata.getClassMetadata(clazz).tableName + "'";
	}

	public String identityIncrementStep(String dbname, Class<?> clazz) {
		return "SELECT VARIABLE_VALUE FROM information_schema.GLOBAL_VARIABLES WHERE VARIABLE_NAME = 'AUTO_INCREMENT_INCREMENT'";
	}

	public String isExistsTableSQL(String databaseName, String tableName) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT count(TABLE_NAME) FROM information_schema.TABLES WHERE ");
		if (databaseName != null) {
			sql.append(" TABLE_SCHEMA = '").append(databaseName).append("' AND ");
		}
		sql.append("TABLE_NAME = '").append(tableName).append("'");
		
		return sql.toString();
	}
	
}
