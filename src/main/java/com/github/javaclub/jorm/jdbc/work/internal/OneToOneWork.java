/*
 * @(#)OneToOneWork.java	2011-9-9
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.work.internal;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.annotation.OneToOne;
import com.github.javaclub.jorm.common.Reflections;
import com.github.javaclub.jorm.jdbc.ClassMetadata;
import com.github.javaclub.jorm.jdbc.JdbcException;
import com.github.javaclub.jorm.jdbc.batch.AbstractBatcher;
import com.github.javaclub.jorm.jdbc.batch.JdbcBatcher;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;
import com.github.javaclub.jorm.jdbc.work.IsolatedWork;

/**
 * OneToOneWork
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: OneToOneWork.java 2011-9-9 上午09:57:42 Exp $
 */
public class OneToOneWork implements IsolatedWork {
	
	protected static final Log LOG = LogFactory.getLog(AbstractBatcher.class);
	
	/** The object to save */
	private Object target;
	private ClassMetadata metadata;
	private JdbcBatcher batcher;
	
	private Serializable identifier;
	
	public OneToOneWork() {
	}

	public OneToOneWork(Object target, JdbcBatcher batcher, ClassMetadata metadata) {
		this.target = target;
		this.batcher = batcher;
		this.metadata = metadata;
	}

	public Preference getPreference() {
		return Preference.SESSION;
	}

	/* 确保只保存目标对象的关联对象 */
	public void doWork(Session session) throws JdbcException {
		if(metadata.ManyToOneFields.size() > 0) {
			return;
		}
		OneToOne oto = null;
		Object value = null;
		ClassMetadata cm = null;
		if(!this.metadata.isForeignStrategy()) {
			identifier = session.getPersister().getIdentifierValue(target);
			this.doInternalWork(batcher, session, target);
		}
		
		for(Field field : metadata.OneToOneFields) {
			if(this.metadata.isForeignStrategy()) {
				Object associated = Reflections.getFieldValue(target, field);
				if(null == associated) {
					throw new JdbcException("The associated object [" + field.getType().getName() 
							+ "] of [" + target.getClass().getName() + "] is null.");
				}
				if(ClassMetadata.getClassMetadata(associated.getClass()).isForeignStrategy()) {
					throw new JdbcException("The associated object [" + associated.getClass().getName() 
							+ "] identifier strategy is Foreign, bad state.");
				}
				
				session.getPersister().getIdentifierValue(associated);
				this.doInternalWork(batcher, session, associated);
				
				oto = field.getAnnotation(OneToOne.class);
				String[] sefieldNames = oto.selField();
				String[] ownerFields = oto.ownerField();
				// 为目标对象设置关联属性值
				for (int i = 0; i < sefieldNames.length; i++) {
					value = Reflections.getFieldValue(associated, sefieldNames[i]);
					Reflections.setFieldValue(target, ownerFields[i], value);
				}
			} else {
				Object associated = Reflections.getFieldValue(target, field);
				if (null != associated) {
					oto = field.getAnnotation(OneToOne.class);
					String[] sefieldNames = oto.selField();
					String[] ownerFields = oto.ownerField();
					for (int i = 0; i < ownerFields.length; i++) {
						value = Reflections.getFieldValue(target, ownerFields[i]);
						Reflections.setFieldValue(associated, sefieldNames[i], value);
					}

					cm = ClassMetadata.getClassMetadata(associated.getClass());
					if (!cm.isForeignStrategy()) {
						session.getPersister().getIdentifierValue(associated);
					}
					this.doInternalWork(batcher, session, associated);
				}
			}
		}
		if (this.metadata.isForeignStrategy()) {
			identifier = (Serializable) Reflections.getFieldValue(target, metadata.identifierField);
			this.doInternalWork(batcher, session, target);
		}
	}
	
	public void doWork(Connection connection) throws JdbcException {
		// do nothing
	}

	public final Serializable getIdentifier() {
		return identifier;
	}
	
	protected void doInternalWork(JdbcBatcher batcher, Session session,
			Object entity) throws JdbcException {
		SqlParams<Object> sqlParams = session.getPersister().insert(entity);
		try {
			if(null != batcher) {
				batcher.addBatch(sqlParams.getSql(false), sqlParams.getParams());
			} else {
				session.executeUpdate(sqlParams);
			}
		} finally {
			sqlParams = null;
		}
	}

}
