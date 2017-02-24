/*
 * @(#)SqlUtil.java	2011-8-17
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.sql.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaclub.jorm.common.Strings;

/**
 * SqlUtil
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: SqlUtil.java 2011-8-17 下午04:46:24 Exp $
 */
public abstract class SqlUtil {
	
	public static boolean isDdl(final String sql) {
		String upper = Strings.upperCase(sql);
		if((upper.indexOf("CREATE TABLE") > -1) || 
			(upper.indexOf("ALTER TABLE") > -1) || 
			(upper.indexOf("DROP TABLE") > -1) || 
			(upper.indexOf("CREATE DATABASE") > -1) || 
			(upper.indexOf("DROP DATABASE") > -1) || 
			(upper.indexOf("CREATE SCHEMA") > -1) || 
			(upper.indexOf("DROP SCHEMA") > -1) || 
			(upper.indexOf("DROP SEQUENCE") > -1) || 
			(upper.indexOf("CREATE SEQUENCE") > -1)) {
			return true;
		}
		return false;
	}
	
	public static String convert(final String sql) {
		if(Strings.isEmpty(sql)) {
			return sql;
		}
		if(sql.startsWith("(") && sql.endsWith(")")) {
			return sql.substring(1, sql.length() - 1);
		} else {
			return sql;
		}
	}

	public static boolean hasOrderBy(final String sql) {
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
	
	public static String getSqlNoOrderBy(final String sql) {
		if(hasOrderBy(sql)) {
			String sbf = Strings.upperCase(sql);
			int lastOrderByPos = sbf.lastIndexOf(" ORDER BY");
			return sql.substring(0, lastOrderByPos);
		}
		return sql;
	}
}
