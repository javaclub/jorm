/*
 * @(#)DummyLoader.java	2011-9-1
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.process;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.javaclub.jorm.Session;

/**
 * DummyLoader
 * 
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: DummyLoader.java 2011-9-1 下午04:57:10 Exp $
 */
public class DummyFieldProcessor implements FieldProcessor, Serializable {

	private static final long serialVersionUID = 1L;

	public Object insert(Session session, Object entity, Field field)
			throws SQLException {
		return null;
	}

	public void load(Session session, Object entity, Field field, ResultSet rs,
			int idx) throws SQLException {
		// do nothing

	}

}
