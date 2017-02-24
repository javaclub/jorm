/*
 * @(#)IdentifierGenerator.java	2011-8-5
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
 * IdentifierGenerator
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: IdentifierGenerator.java 2011-8-5 下午07:09:23 Exp $
 */
public interface IdentifierGenerator {

	/**
	 * Generate a new identifier.
	 *
	 * @param session a <tt>JdbcSession</tt>
	 * @param object the entity for which the id is being generated
	 * @param field  id field
	 * @return unique id value
	 */
	public Serializable generate(Session session, Object object, Field field);
	
	/**
	 * Initlialized the underlying database's dialect
	 *
	 * @param dialect The underlying database's dialect
	 */
	public void setDialect(Dialect dialect);
	
	public Dialect getDialect();
}
