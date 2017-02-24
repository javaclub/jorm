/*
 * @(#)AutoIdGenerator.java	2011-8-5
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.id;

import java.io.Serializable;
import java.lang.reflect.Field;

import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.jdbc.sql.Dialect;

/**
 * AutoIdGenerator
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: AutoIdGenerator.java 2011-8-5 下午07:18:27 Exp $
 */
public class AutoIdGenerator implements IdentifierGenerator {
	
	private Dialect dialect;

	public AutoIdGenerator() {
		super();
	}

	public Serializable generate(Session session, Object object, Field field) {
		return IncreasableIdentifiers.getNextIdValue(session, object.getClass(), field);
	}

	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

	public Dialect getDialect() {
		return dialect;
	}
}
