/*
 * @(#)AssignedIdGenerator.java	2011-8-5
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.id;

import java.io.Serializable;
import java.lang.reflect.Field;

import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.common.Reflections;
import com.github.javaclub.jorm.jdbc.JdbcException;
import com.github.javaclub.jorm.jdbc.sql.Dialect;

/**
 * AssignedIdGenerator
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: AssignedIdGenerator.java 2011-8-5 下午07:38:46 Exp $
 */
public class AssignedGenerator implements IdentifierGenerator {
	
	private Dialect dialect;
	
	public AssignedGenerator() {
		super();
	}

	public Serializable generate(Session session, Object object, Field field) {
		Serializable value = (Serializable) Reflections.getFieldValue(object, field);
		if (null == value) {
			throw new JdbcException("the property [" + field.getName()
							+ "] must be initialized, which is primary key in table.");
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
