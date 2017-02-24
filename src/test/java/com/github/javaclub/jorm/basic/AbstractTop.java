/*
 * @(#)AbstractTop.java	2011-8-31
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.basic;

import com.github.javaclub.jorm.config.JdbcConfigXmlParser;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;

/**
 * AbstractTop
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: AbstractTop.java 2011-8-31 下午02:08:09 Exp $
 */
public abstract class AbstractTop extends Top {
	
	protected AbstractTop() {
		super();
	}

	public <T> String loading(SqlParams<T> sqlParams) {
		String t_sql = this.generate(sqlParams);
		if(this.supportLimit() && sqlParams.getMaxResults() > 0) {
			t_sql = t_sql + " LIMIT " + sqlParams.getMaxResults();
			if(JdbcConfigXmlParser.isShowSql()) {
				System.out.println("JDBC: " + t_sql);
			}
		}
		return t_sql;
	}
}
