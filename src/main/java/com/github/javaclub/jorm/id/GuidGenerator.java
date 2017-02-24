/*
 * @(#)GuidGenerator.java	2011-8-5
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.id;

import java.io.Serializable;
import java.lang.reflect.Field;

import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.jdbc.JdbcException;
import com.github.javaclub.jorm.jdbc.sql.Dialect;

/**
 * GuidGenerator
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: GuidGenerator.java 2011-8-5 下午07:20:06 Exp $
 */
public class GuidGenerator implements IdentifierGenerator {
	
	private Dialect dialect;

	public GuidGenerator() {
		super();
	}

	public Serializable generate(Session session, Object object, Field field) {
		String sql = dialect.getSelectGuidSql();
		Serializable value = session.unique(sql);
		if(null == value) {
			throw new JdbcException("Failed to generate a GUID string.");
		}
		return value;
	}

	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}
	
	public Dialect getDialect() {
		return dialect;
	}

}
