/*
 * @(#)SQLServerDialect.java	May 7, 2009
 *
 * Copyright (c) 2009 by jadmin. All Rights Reserved.
 */

package com.github.javaclub.jorm.jdbc.sql.impl;

import java.lang.reflect.Field;

import com.github.javaclub.jorm.Environment;
import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.common.Version;
import com.github.javaclub.jorm.jdbc.ClassMetadata;


/**
 * SqlDialect for database SQL Server.
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: SQLServerDialect.java 539 2011-10-09 03:48:18Z gerald.chen.hz $
 */
public class SQLServerDialect extends AbstractDialect{
	
    public SQLServerDialect() {
		super();
	}

	public String pageable(String sql, int start, int limit) {
		StringBuilder sbf = new StringBuilder();
		final String ROW_NUM_CONST = Jorm.IGNORE_COLUMN_PREFIX + "row_number" + Jorm.IGNORE_COLUMN_SUFFIX;
		// 默认不使用排序
		String rownum = "ROW_NUMBER() OVER(ORDER BY CURRENT_TIMESTAMP) AS " + ROW_NUM_CONST;
		if (hasOrderBy(sql)) {
			String orderBy = sql.substring(Strings.upperCase(sql).indexOf(ORDER_BY));
			rownum = "ROW_NUMBER() OVER(" + orderBy.trim() + ") AS " + ROW_NUM_CONST;
		}
		final String sqlx = joinRownum(sql, rownum);
		try {
			sbf.append("SELECT * FROM (").append(sqlx).append(") AS temp_table");
			sbf.append(" WHERE ").append(ROW_NUM_CONST).append(" BETWEEN ")
					.append(start + 1).append(" AND ").append(start + limit);
			return sbf.toString();
		} finally {
			sbf.setLength(0);
			sbf = null;
		}
	}

	public String getSelectGuidSql() {
		return "select newid()";
	}
	
	public boolean supportsLimit() {
		Version v2005 = new Version("9.00.1399");
		Version current = new Version(getNativeProperties().getProperty(Environment.DB_VERSION, "0.0.0"));
		// 如果是sqlserver2000(08.00.2039) row_number()是不支持的
		if(current.compareTo(v2005) < 0) {
			return false;
		}
		return true;
	}
	
	public String nextIdentitySql(String dbname, Class<?> clazz) {
		StringBuilder sbf = new StringBuilder();
		sbf.append("SELECT ( ");
		sbf.append("IDENT_CURRENT('" + ClassMetadata.getClassMetadata(clazz).tableName + "')");
		sbf.append(" + ");
		sbf.append("IDENT_INCR('" + ClassMetadata.getClassMetadata(clazz).tableName + "') ) AS id");
		try {
			return sbf.toString();
		} finally {
			sbf.setLength(0);
			sbf = null;
		}
	}
	
	public String identityIncrementStep(String dbname, Class<?> clazz) {
		return "SELECT IDENT_INCR('" 
				+ ClassMetadata.getClassMetadata(clazz).tableName 
				+ "')";
	}

	public String ddlIdentityColumn() {
		return "id bigint identity (1, 1)";
	}
	
	public String ddlFieldColumn(Class<?> entityType) {
		Field identity = ClassMetadata.getClassMetadata(entityType).identifierField;
		Class<?> identityType = identity.getType();
		if("int".equals(identityType.getName()) || "java.lang.Integer".equals(identityType.getName())) {
			return Strings.lowerCase(entityType.getSimpleName()) + "_id int";
		} else if("long".equals(identityType.getName()) || "java.lang.Long".equals(identityType.getName())) {
			return Strings.lowerCase(entityType.getSimpleName()) + "_id bigint";
		}
		return Strings.lowerCase(entityType.getSimpleName()) + "_id varchar(64)";
	}

	/**
	 * 将 rowNum 插入到 SQL 中间组成一个新的SQL
	 * 
	 * @param sql sql
	 * @param rownum row number
	 * @return new sql
	 */
	private static String joinRownum(String sql, String rownum) {
		boolean isSQNormal = true;
		boolean isBracketNoraml = true;
		String upper = Strings.upperCase(sql).trim();
		int fromPos = upper.indexOf(" FROM ");
		for (int i = 0; i < sql.length(); i++) {
			String s = sql.substring(i, i + 1);
			if (isBracketNoraml) {
				if (s.equals("(")) {
					isBracketNoraml = false;
				}
			} else if (s.equals(")")) {
				isBracketNoraml = true;
			}
			if (isSQNormal) {
				if (s.equals("'")) {
					isSQNormal = false;
				}
			} else if (s.equals("'")) {
				isSQNormal = true;
			}
			if (fromPos == i) {
				if (isSQNormal && isBracketNoraml) {
					break;
				} else {
					fromPos = upper.indexOf(" FROM ", i + 1);
					if (fromPos == -1) {
						System.out.println(" error ----- ");
					}
				}
			}
		}
		StringBuilder sbf = new StringBuilder();
		try {
			sbf.append("SELECT TOP 100 PERCENT ").append(rownum);
			sbf.append(",").append(sql.substring(6));
			return sbf.toString();
		} finally {
			sbf.setLength(0);
			sbf = null;
		}
	}

	
}
