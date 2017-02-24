/*
 * @(#)PostgreSQLDialect.java	May 7, 2009
 *
 * Copyright (c) 2009 by jadmin. All Rights Reserved.
 */

package com.github.javaclub.jorm.jdbc.sql.impl;

import java.lang.reflect.Field;

import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.jdbc.ClassMetadata;


/**
 * SqlDialect for database PostgreSQL.
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: PostgreSQLDialect.java 521 2011-10-06 12:11:32Z gerald.chen.hz@gmail.com $
 */
public class PostgreSQLDialect extends AbstractDialect {
	
	public PostgreSQLDialect() {
		super();
	}

	public String pageable(String sql, int start, int limit) {
		if (hasLimit(sql)) {
			int lastLimitPos = Strings.upperCase(sql).lastIndexOf(LIMIT);
			/*  
			 * 如果给出了一个LIMIT计数，那么将返回不超过该数字的行OFFSET 
			 * 指明在开始返回行之前忽略多少行
			 * 
			 */
			return sql.substring(0, lastLimitPos) + " " + LIMIT + " "
					+ limit + " OFFSET " + start;
		}
		return sql + " " + LIMIT + " " + limit + " OFFSET " + start;
	}

	public String getSelectGuidSql() {
		return "select uuid_generate_v1()";
	}
	
	public boolean supportsSequences() {
		return true;
	}
	
	public boolean supportsLimit() {
		return true;
	}

	public boolean supportDdlRollback() {
		return true;
	}

	public String sequenceNextValSql(String sequenceName) {
		return "select nextval ('" + sequenceName + "')";
	}

	public String nextIdentitySql(String dbname, Class<?> clazz) {
		ClassMetadata metadata = ClassMetadata.getClassMetadata(clazz);
		String col = metadata.column(metadata.identifierField.getName());
		try {
			return this.sequenceNextValSql(metadata.tableName + "_" + col + "_seq");
		} finally {
			col = null;
			metadata = null;
		}
	}

	public String ddlIdentityColumn() {
		return "id bigserial";
	}
	
	public String ddlFieldColumn(Class<?> entityType) {
		Field identity = ClassMetadata.getClassMetadata(entityType).identifierField;
		Class<?> identityType = identity.getType();
		if("int".equals(identityType.getName()) || "java.lang.Integer".equals(identityType.getName())) {
			return Strings.lowerCase(entityType.getSimpleName()) + "_id integer";
		} else if("long".equals(identityType.getName()) || "java.lang.Long".equals(identityType.getName())) {
			return Strings.lowerCase(entityType.getSimpleName()) + "_id bigint";
		}
		return Strings.lowerCase(entityType.getSimpleName()) + "_id VARCHAR(64)";
	}
	
}
