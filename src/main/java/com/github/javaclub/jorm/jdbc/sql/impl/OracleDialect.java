/*
 * @(#)OracleDialect.java	May 7, 2009
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
import com.github.javaclub.jorm.jdbc.sql.JoinFragment;
import com.github.javaclub.jorm.jdbc.sql.OracleJoinFragment;




/**
 * SqlDialect for database Oracle.
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: OracleDialect.java 522 2011-10-08 10:29:16Z gerald.chen.hz $
 */
public class OracleDialect extends AbstractDialect {
	
	public OracleDialect() {
		super();
	}
	
	
	public JoinFragment createOuterJoinFragment() {
		String oracle10g = "10.0";
		Version version = new Version(getNativeProperties().getProperty(Environment.DB_VERSION, "0.0.0"));
		if(version.compareTo(new Version(oracle10g)) < 0) {
			return new OracleJoinFragment();
		}
		return super.createOuterJoinFragment();
	}

	public String pageable(String sql, int start, int limit) {
		return "SELECT * FROM (" 
			        + "SELECT " + Jorm.TEMP_TBNAME + ".*, rownum " + Jorm.IGNORE_COLUMN_PREFIX + "rownum" + Jorm.IGNORE_COLUMN_SUFFIX + 
			        		" FROM (" + sql + ") " + Jorm.TEMP_TBNAME + " WHERE rownum <= " + (start + limit)
		     + ") WHERE " + Jorm.IGNORE_COLUMN_PREFIX + "rownum" + Jorm.IGNORE_COLUMN_SUFFIX + " > " + start;
		// Oracle 'rownum' starts with 1, not like MySQL with 0;
	}
	
	public String getSelectGuidSql() {
		return "select rawtohex(sys_guid()) from dual";
	}

	public String ddlIdentityColumn() {
		return "id NUMBER NOT NULL";
	}
	
	public String ddlFieldColumn(Class<?> entityType) {
		Field identity = ClassMetadata.getClassMetadata(entityType).identifierField;
		Class<?> identityType = identity.getType();
		if("int".equals(identityType.getName()) || "java.lang.Integer".equals(identityType.getName()) 
			|| "long".equals(identityType.getName()) || "java.lang.Long".equals(identityType.getName())) {
			return Strings.lowerCase(entityType.getSimpleName()) + "_id NUMBER";
		}
		return Strings.lowerCase(entityType.getSimpleName()) + "_id VARCHAR2(64)";	
	}

	public boolean supportsSequences() {
		return true;
	}

	public boolean supportsLimit() {
		return true;
	}

	public String sequenceNextValSql(String sequenceName) {
		return "select " + sequenceName + ".nextval from dual";
	}
	
}
