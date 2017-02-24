/*
 * @(#)SequenceGenerator.java	2011-8-5
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.id;

import java.io.Serializable;
import java.lang.reflect.Field;

import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.annotation.Id;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.jdbc.JdbcException;
import com.github.javaclub.jorm.jdbc.sql.Dialect;

/**
 * SequenceGenerator
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: SequenceGenerator.java 2011-8-5 下午07:39:32 Exp $
 */
public class SequenceGenerator implements IdentifierGenerator {
	
	private Dialect dialect;

	public SequenceGenerator() {
		super();
	}

	public Serializable generate(Session session, Object object, Field field) {
		Id id = field.getAnnotation(Id.class);
		String sequenceName = id.sequenceName();
		if (Strings.isEmpty(sequenceName)) {
			throw new IllegalStateException(
					"the attribute [sequence] of annotation @Id is empty in the field ["
							+ field + "]");
		}
		String sql = dialect.sequenceNextValSql(sequenceName);
		Serializable value = session.unique(sql);
		if(null == value) {
			throw new JdbcException("Failed to generated the sequence value.");
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
