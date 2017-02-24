/*
 * @(#)ManyToOneWork.java	2011-10-6
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.work.internal;

import java.lang.reflect.Field;
import java.sql.Connection;

import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.annotation.ManyToOne;
import com.github.javaclub.jorm.common.Reflections;
import com.github.javaclub.jorm.id.IdentifierGeneratorFactory;
import com.github.javaclub.jorm.jdbc.ClassMetadata;
import com.github.javaclub.jorm.jdbc.JdbcException;
import com.github.javaclub.jorm.jdbc.batch.JdbcBatcher;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;
import com.github.javaclub.jorm.jdbc.work.IsolatedWork;

/**
 * ManyToOneWork
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: ManyToOneWork.java 521 2011-10-06 12:11:32Z gerald.chen.hz@gmail.com $
 */
public class ManyToOneWork implements IsolatedWork {

	/** The object to save */
	private Object target;
	private ClassMetadata metadata;
	private JdbcBatcher batcher;
	
	public ManyToOneWork() {
		super();
	}
	
	public ManyToOneWork(Object target, JdbcBatcher batcher,
			ClassMetadata metadata) {
		this.target = target;
		this.batcher = batcher;
		this.metadata = metadata;
	}

	public void doWork(Session session) throws JdbcException {
		Object one = null;
		String[] selfId = null;
		String[] ownerId = null;
		SqlParams<Object> sqlParams = null;
		for (Field field : this.metadata.ManyToOneFields) {
			one = Reflections.getFieldValue(target, field);
			selfId = field.getAnnotation(ManyToOne.class).selField();
			ownerId = field.getAnnotation(ManyToOne.class).ownerField();
			if(null == one) {
				for (String string : ownerId) {
					if(!IdentifierGeneratorFactory.isFieldInitialized(Reflections.getField(metadata.clazz, string), target)) {
						throw new IllegalStateException("Bad state, the relational ids is not initialized or relational object is null.");
					}
				}
			} else {
				for (int i = 0; i < ownerId.length; i++) {
					if(IdentifierGeneratorFactory.isFieldInitialized(Reflections.getField(field.getType(), selfId[i]), Reflections.getFieldValue(target, field))) {
						Reflections.setFieldValue(target, ownerId[i], Reflections.getFieldValue(Reflections.getFieldValue(target, field), selfId[i]));
					}
					// tests if all relational id is initialized
					if(!IdentifierGeneratorFactory.isFieldInitialized(Reflections.getField(metadata.clazz, ownerId[i]), target)) {
						throw new IllegalStateException("Bad state, the relational id => " + ownerId[i] + " is not initialized.");
					}
				}
			}
		}
		sqlParams = session.getPersister().insert(this.target);
		if(null != batcher) {
			batcher.addBatch(sqlParams.getSql(false), sqlParams.getParams());
		} else {
			session.executeUpdate(sqlParams);
		}
		sqlParams = null;
		ownerId = null;
		selfId = null;
		one = null;
	}

	public Preference getPreference() {
		return Preference.SESSION;
	}
	
	public void doWork(Connection connection) throws JdbcException {
		// do nothing
	}

}
