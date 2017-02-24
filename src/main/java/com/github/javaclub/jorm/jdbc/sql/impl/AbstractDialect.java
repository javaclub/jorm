/*
 * @(#)AbstractDialect.java	May 7, 2009
 *
 * Copyright (c) 2009 by jadmin. All Rights Reserved.
 */

package com.github.javaclub.jorm.jdbc.sql.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.jdbc.sql.Dialect;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;

/**
 * The abstract SqlDialect for general database.
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: AbstractDialect.java 282 2011-08-31 15:27:05Z gerald.chen.hz@gmail.com $
 */
/* package-private */
abstract class AbstractDialect extends Dialect {
	
	protected static final String LIMIT = "LIMIT";
	
	public AbstractDialect() {
		
	}
	
	public <T> SqlParams<T> pageable(SqlParams<T> sqlParams) {
		SqlParams<T> r = sqlParams.copy();
		final String sql = this.generateSql(sqlParams, true);
		if(this.supportsLimit() && sqlParams.getMaxResults() > 0) {
			r.setSql(pageable(sql, sqlParams.getFirstResult(), sqlParams.getMaxResults()));
		} else {
			r.setSql(sql);
		}
		return r;
	}
	
	public boolean hasOrderBy(final String sql) {
		final String regex = "(select\\s+)(.+\\s+)(from\\s+)(.+\\s+)([^\\(]+)(\\s+order\\s+by\\s+)([^\\)]+)";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(sql);
		if(matcher.find()) {
			String matched = matcher.group();
			if(!matched.endsWith(")") && matched.equalsIgnoreCase(sql)) {
				return true;
			}
		}

		String lower = Strings.lowerCase(sql);
		if (Strings.count(lower, "select") == 1
				&& Strings.count(lower, "from") == 1
				&& Strings.count(lower, "order by") == 1) {
			return true;
		}
		return false;
	}
	
	public boolean hasLimit(final String sql) {
		String regex = "(select\\s+)(.+\\s+)(from\\s+)(.+\\s+)([^\\(]+)(\\s+limit\\s+)([^\\)]+)";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(sql);
		if(matcher.find()) {
			String matched = matcher.group();
			if(!matched.endsWith(")") && matched.equalsIgnoreCase(sql)) {
				return true;
			}
		} 
		
		String lower = Strings.lowerCase(sql);
		if (Strings.count(lower, "select") == 1
				&& Strings.count(lower, "limit") == 1) {
			return true;
		}
		return false;
	}
	
	/**
	 * Remove the last "order by" if exists.
	 *
	 * @param sql the input sql
	 * @return a sql string
	 */
	public String removeOrderBy(final String sql) {
		if(hasOrderBy(sql)) {
			String upper = Strings.upperCase(sql);
			return sql.substring(0, upper.lastIndexOf(ORDER_BY));
		}
		return sql;
	}
	
	public <T> SqlParams<T> loadEntityParams(SqlParams<T> sqlParams, boolean annotationClass) {
		Class<T> clazz = sqlParams.getObjectClass();
		if(null == clazz) {
			LOG.warn("The objectClass in SqlParams is null.");
			if(annotationClass) {
				throw new IllegalStateException("The objectClass should be initialized in SqlParams.");
			}
		}
		String t_sql = this.generateSql(sqlParams, annotationClass);
		if(this.supportsLimit() && sqlParams.getMaxResults() > 0) {
			t_sql = pageable(t_sql, sqlParams.getFirstResult(), sqlParams.getMaxResults());
		}
		SqlParams<T> newParams = sqlParams.copy();
		newParams.setSql(t_sql);
		try {
			return newParams;
		} finally {
			t_sql = null;
			newParams = null;
		}
	}

}
