/*
 * @(#)UuidGenerator.java	2011-8-5
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.id;

import java.io.Serializable;
import java.lang.reflect.Field;

import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.common.UuidUtil;
import com.github.javaclub.jorm.jdbc.sql.Dialect;

/**
 * UuidGenerator
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: UuidGenerator.java 2011-8-5 下午07:19:29 Exp $
 */
public class UuidGenerator implements IdentifierGenerator {
	
	private Dialect dialect;

	public UuidGenerator() {
		super();
	}

	public Serializable generate(Session session, Object object, Field field) {
		return UuidUtil.newUUID();
	}

	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

	public Dialect getDialect() {
		return dialect;
	}
}
