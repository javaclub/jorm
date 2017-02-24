/*
 * @(#)H2Dialect.java	May 17, 2010
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.jdbc.sql.impl;

import com.github.javaclub.jorm.common.Strings;

/**
 * SqlDialect for database H2.
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: H2Dialect.java 483 2011-09-26 14:04:43Z gerald.chen.hz $
 */
public class H2Dialect extends AbstractDialect {
	
	public H2Dialect() {
		super();
	}

	public String pageable(String sql, int start, int limit) {
		if (hasLimit(sql)) {
			int lastLimitPos = Strings.upperCase(sql).lastIndexOf(LIMIT);
			return sql.substring(0, lastLimitPos) + " " + LIMIT + " "
					+ limit + " OFFSET " + start;
		}
		return sql + " " + LIMIT + " " + limit + " OFFSET " + start;
	}

	public boolean supportsSequences() {
		return true;
	}

	public boolean supportsLimit() {
		return true;
	}

	public String sequenceNextValSql(String sequenceName) {
		return "call next value for " + sequenceName;
	}
	
}
