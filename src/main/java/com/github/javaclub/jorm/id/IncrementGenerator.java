/*
 * @(#)IncrementGenerator.java	2011-8-5
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
 * IncrementGenerator, 非数据库自动生成的自增ID实现
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: IncrementGenerator.java 2011-8-5 下午07:40:09 Exp $
 */
public class IncrementGenerator implements IdentifierGenerator {
	
	private Dialect dialect;

	public IncrementGenerator() {
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
