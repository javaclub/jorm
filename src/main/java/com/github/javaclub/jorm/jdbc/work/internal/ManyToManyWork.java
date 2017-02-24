/*
 * @(#)ManyToManyWork.java	2011-9-30
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
import com.github.javaclub.jorm.annotation.ManyToMany;
import com.github.javaclub.jorm.common.CommonUtil;
import com.github.javaclub.jorm.common.Reflections;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.jdbc.ClassMetadata;
import com.github.javaclub.jorm.jdbc.JdbcException;
import com.github.javaclub.jorm.jdbc.batch.AbstractBatcher;
import com.github.javaclub.jorm.jdbc.batch.JdbcBatcher;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;
import com.github.javaclub.jorm.jdbc.work.IsolatedWork;
import com.github.javaclub.jorm.jdbc.work.Isolater;

/**
 * ManyToManyWork for saving many-to-many relationship between entities.
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: ManyToManyWork.java 2011-9-30 下午05:27:31 Exp $
 */
public class ManyToManyWork implements IsolatedWork {

	protected static final Log LOG = LogFactory.getLog(AbstractBatcher.class);

	/** The object to save */
	private Object target;
	private ClassMetadata metadata;
	private JdbcBatcher batcher;
	
	public ManyToManyWork() {
		super();
	}
	
	public ManyToManyWork(Object target, JdbcBatcher batcher,
			ClassMetadata metadata) {
		this.target = target;
		this.batcher = batcher;
		this.metadata = metadata;
		
	}

	public void doWork(Session session) throws JdbcException {
		ensureTable(session);
		Object id = Reflections.getFieldValue(target, metadata.identifierField);
		
		// many
		Object obj = null; Serializable id2 = null; SqlParams<Object> sqlParams = null;
		String r_sql = null; Object[] r_params = null;
		for (Field field : this.metadata.ManyToManyFields) {
			if(!field.getAnnotation(ManyToMany.class).cascade()) {
				continue;
			}
			Object many = Reflections.getFieldValue(target, field);
			for (int i = 0; i < CommonUtil.length(many); i++) {
				obj = CommonUtil.get(many, i);
				id2 = session.getPersister().getIdentifierValue(obj);
				sqlParams = session.getPersister().insert(obj);
				r_sql = this.getMappingSql(session, field);
				r_params = this.getMappingParams(session, field, id, id2);
				if(null != batcher) {
					batcher.addBatch(sqlParams.getSql(false), sqlParams.getParams());
					batcher.addBatch(r_sql, r_params);
				} else {
					session.executeUpdate(sqlParams);
					session.executeUpdate(r_sql, r_params);
				}
			}
		}
		
		sqlParams = null;
	}
	
	private Object[] getMappingParams(Session session, Field field, Object obj1, Object obj2) {
		return new Object[] { obj1, obj2 };
	}

	private String getMappingSql(Session session, Field field) {
		return "INSERT INTO " + ClassMetadata.getMappingTablename(target, field) + "(" + 
				Strings.lowerCase(target.getClass().getSimpleName()) + "_id, " + 
				Strings.lowerCase(field.getAnnotation(ManyToMany.class).type().getSimpleName()) + 
				"_id)" + " VALUES(?, ?)";
	}

	/**
	 * Make sure the many-to-many mapping table is ready.
	 *
	 * @param session <tt>JdbcSession</tt>
	 */
	protected void ensureTable(Session session) throws JdbcException {
		for (Field field : this.metadata.ManyToManyFields) {
			if(!session.existsTable(ClassMetadata.getMappingTablename(target, field))) {
				createMappingTable(session, field);
			}
		}
		
	}

	protected void createMappingTable(Session session, final Field field) throws JdbcException {
		final StringBuilder script = new StringBuilder("CREATE TABLE ").append(ClassMetadata.getMappingTablename(target, field) + " (");
		//script.append(session.getDialect().ddlIdentityColumn() + ", ");
		script.append(session.getDialect().ddlFieldColumn(target.getClass()) + ", ");
		script.append(session.getDialect().ddlFieldColumn(field.getAnnotation(ManyToMany.class).type()) + ", ");
		script.append("PRIMARY KEY (" + Strings.lowerCase(target.getClass().getSimpleName()) + "_id" + "," 
				+ Strings.lowerCase(field.getAnnotation(ManyToMany.class).type().getSimpleName()) + "_id"
				+ ") )");
		try {
			IsolatedWork work = new IsolatedWork() {
				public Preference getPreference() {
					return Preference.SESSION;
				}
				
				public void doWork(Session session) throws JdbcException {
					session.executeUpdate(script.toString());
				}
				
				public void doWork(Connection connection) throws JdbcException {
				}
			};
			Isolater.doNonTransactedWork(work, session);
		} finally {
			script.setLength(0);
		}
	}
	
	public Preference getPreference() {
		return Preference.SESSION;
	}
	
	public void doWork(Connection connection) throws JdbcException {
		// do nothing
	}
	

}
