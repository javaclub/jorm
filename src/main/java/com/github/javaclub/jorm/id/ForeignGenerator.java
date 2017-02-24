/*
 * @(#)ForeignGenerator.java	2011-8-5
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.id;

import java.io.Serializable;
import java.lang.reflect.Field;

import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.annotation.OneToOne;
import com.github.javaclub.jorm.common.Reflections;
import com.github.javaclub.jorm.jdbc.ClassMetadata;
import com.github.javaclub.jorm.jdbc.JdbcException;
import com.github.javaclub.jorm.jdbc.sql.Dialect;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;

/**
 * ForeignGenerator, for one-to-one reationship.
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: ForeignGenerator.java 2011-8-5 下午07:40:30 Exp $
 */
public class ForeignGenerator implements IdentifierGenerator {
	
	private Dialect dialect;
	
	private final Object lock = new Object();

	public ForeignGenerator() {
		super();
	}

	public Serializable generate(Session session, Object object, Field field) {
		ClassMetadata metadata = ClassMetadata.getClassMetadata(object.getClass());
		if(metadata.OneToOneFields.isEmpty()) {
			throw new JdbcException("No one-to-one reationship was found in the class => " 
					+ object.getClass().getName());
		}
		Field oneField = metadata.OneToOneFields.get(0);
		Object associated = Reflections.getFieldValue(object, oneField);
		if(null == associated) {
			throw new JdbcException("The associated object of [" + object.getClass().getName() + "] is null.");
		}
		if(ClassMetadata.getClassMetadata(associated.getClass()).isForeignStrategy()) {
			throw new JdbcException("The associated object [" + associated.getClass().getName() 
					+ "] identifier strategy is Foreign, bad state.");
		}
		
		Object associatedId = Reflections.getFieldValue(
						associated,
						ClassMetadata.getClassMetadata(associated.getClass()).identifierField);
		if (null == associatedId) {
			synchronized (this.lock) {
				session.getPersister().getIdentifierValue(associated);
				SqlParams<Object> sqlParams = session.getPersister().insert(associated);
				session.executeUpdate(sqlParams);
			}
		}
		
		OneToOne oto = oneField.getAnnotation(OneToOne.class);
		String[] sefieldNames = oto.selField();
		String[] ownerFields = oto.ownerField();
		Object value = null;
		// 为目标对象设置关联属性值
		for (int i = 0; i < sefieldNames.length; i++) {
			value = Reflections.getFieldValue(associated, sefieldNames[i]);
			Reflections.setFieldValue(object, ownerFields[i], value);
		}
		
		return (Serializable) Reflections.getFieldValue(object, field);
	}

	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

	public Dialect getDialect() {
		return dialect;
	}
}
