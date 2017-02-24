/*
 * @(#)OneToManyWork.java	2011-9-15
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
import com.github.javaclub.jorm.annotation.OneToMany;
import com.github.javaclub.jorm.common.CommonUtil;
import com.github.javaclub.jorm.common.Reflections;
import com.github.javaclub.jorm.jdbc.ClassMetadata;
import com.github.javaclub.jorm.jdbc.JdbcException;
import com.github.javaclub.jorm.jdbc.batch.AbstractBatcher;
import com.github.javaclub.jorm.jdbc.batch.JdbcBatcher;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;
import com.github.javaclub.jorm.jdbc.work.IsolatedWork;

/**
 * OneToManyWork
 * 
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: OneToManyWork.java 2011-9-15 下午03:08:17 Exp $
 */
public class OneToManyWork implements IsolatedWork {
	
	protected static final Log LOG = LogFactory.getLog(AbstractBatcher.class);

	/** The object to save */
	private Object target;
	private ClassMetadata metadata;
	private JdbcBatcher batcher;
	
	private Serializable identifier;

	public OneToManyWork() {
		super();
	}

	public OneToManyWork(Object target, JdbcBatcher batcher,
			ClassMetadata metadata) {
		this.target = target;
		this.batcher = batcher;
		this.metadata = metadata;
	}

	public void doWork(Session session) throws JdbcException {
		OneToMany otm = null;
		Object item = null;
		Object many = null;
		Object[] values = null;
		ClassMetadata cm = null;
		try {
			for (Field field : this.metadata.OneToManyFields) {
				otm = field.getAnnotation(OneToMany.class);
				if (null == otm || (null != otm && otm.cascade() == false)) {
					continue;
				}
				many = Reflections.getFieldValue(target, field);
				values = getOwnerFieldsValue(otm, target);
				for (int i = 0; i < CommonUtil.length(many); i++) {
					item = CommonUtil.get(many, i);
					if(null == item) {
						continue;
					}
					for (int j = 0; j < values.length; j++) {
						Reflections.setFieldValue(item, otm.selField()[j], values[j]);
					}
					cm = ClassMetadata.getClassMetadata(item.getClass());
					if (!cm.isForeignStrategy()) {
						session.getPersister().getIdentifierValue(item);
					}
					this.doInternalWork(batcher, session, item);
				}
			}
		} finally {
			otm = null;
			item = null;
			many = null;
			values = null;
			cm = null;
		}

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
			System.gc();
			try {
				Thread.sleep(3L);
			} catch (InterruptedException e) {
			}
		}
	}

	protected Object[] getOwnerFieldsValue(OneToMany otm, Object obj) {
		Object[] result = new Object[otm.ownerField().length];
		for (int i = 0; i < otm.ownerField().length; i++) {
			result[i] = Reflections.getFieldValue(obj, otm.ownerField()[i]);
		}
		try {
			return result;
		} finally {
			result = null;
			System.gc();
			try {
				Thread.sleep(3L);
			} catch (InterruptedException e) {
			}
		}
	}

	public void doWork(Connection connection) throws JdbcException {
		// do nothing
	}

	public Preference getPreference() {
		return Preference.SESSION;
	}
	
	public final Serializable getIdentifier() {
		return identifier;
	}

}
