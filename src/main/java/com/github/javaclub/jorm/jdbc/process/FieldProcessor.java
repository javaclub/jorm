/*
 * @(#)FieldProcessor.java	2011-9-1
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.process;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.javaclub.jorm.Session;

/**
 * FieldProcessor
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: FieldProcessor.java 2011-9-1 下午08:07:39 Exp $
 */
public interface FieldProcessor {

	/**
	 * Get the specified form value of the field column to insert.
	 *
	 * @param session JdbcSession
	 * @param entity  the entity
	 * @param field   entity's field
	 * @return value of the field column to insert
	 * @throws SQLException
	 */
	public Object insert(Session session, Object entity, Field field)
			throws SQLException;
	
	
	/**
	 * Convert the field column value that loading from database to specified form.
	 *
	 * @param session JdbcSession
	 * @param entity  the entity
	 * @param field   entity's field
	 * @param rs      JDBC ResultSet
	 * @param idx     the index
	 * @throws SQLException
	 */
	public void load(Session session, Object entity, Field field, ResultSet rs,
			int idx) throws SQLException;
}
