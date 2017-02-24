/*
 * @(#)IdentityGenerator.java	2011-9-24
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.id;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.jdbc.ClassMetadata;
import com.github.javaclub.jorm.jdbc.sql.Dialect;
import com.github.javaclub.jorm.jdbc.sql.impl.MySQLDialect;
import com.github.javaclub.jorm.jdbc.sql.impl.PostgreSQLDialect;
import com.github.javaclub.jorm.jdbc.sql.impl.SQLServerDialect;

/**
 * IdentityGenerator for the stratyge 'identity', this stratyge is for auto-generated keys such as 
 * auto_increment(MySQL)、identity(MSSQL) etc.
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: IdentityGenerator.java 538 2011-10-09 03:48:00Z gerald.chen.hz $
 */
public class IdentityGenerator implements IdentifierGenerator {
	
	private Dialect dialect;
	
	@SuppressWarnings("unchecked")
	protected static final ConcurrentMap<Class, AtomicLong[]> identifiersHolder = new ConcurrentHashMap<Class, AtomicLong[]>();

	public synchronized Serializable generate(Session session, Object object,
			Field field) {
		Object nextId = null;
		if ((dialect instanceof MySQLDialect) 
			|| (dialect instanceof SQLServerDialect) 
			|| (dialect instanceof PostgreSQLDialect)) {
			nextId = NextIdentifierGenerator.getInstance().value(session, object);
		} else {
			throw new UnsupportedOperationException("IdentityGenerator strategy is not supported by => " + dialect);
		}
		
		return IdentifierGeneratorFactory.getGeneratedIdentity(nextId, field.getType());
	}

	public Dialect getDialect() {
		return this.dialect;
	}

	public void setDialect(Dialect dialect) {
		this.dialect = dialect;

	}
	
	static class NextIdentifierGenerator {
		
		private static class SingletonHolder {
			private final static  NextIdentifierGenerator INSTANCE = new NextIdentifierGenerator();
		}
		
		private NextIdentifierGenerator() {}
		
		public static NextIdentifierGenerator getInstance() {
			return SingletonHolder.INSTANCE;
		}
		
		/**
		 * Gets next identity id value
		 *
		 * @param session JdbcSession
		 * @param object the target object
		 * @return identity value
		 */
		public Serializable value(Session session, Object object) {
			ClassMetadata metadata = ClassMetadata.getClassMetadata(object.getClass());
			// al[0]:next id; al[1]:incrment step
			AtomicLong[] al = identifiersHolder.get(metadata.clazz);
			if(null == al || (null != al && al[1] == null)) {
				al = new AtomicLong[2];
				
				Long nextId = nextIdValue(session, metadata);
				Long currentId = currentMaxIdValue(session, metadata);
				al[0] = new AtomicLong(nextId.longValue());
				if(null != currentId) {
					al[1] = new AtomicLong(nextId.longValue() - currentId.longValue());
				} else {
					al[1] = new AtomicLong(incrementStep(session, metadata).longValue());
				}
				
				identifiersHolder.put(ClassMetadata.getClassMetadata(object.getClass()).clazz, al);
				metadata = null;
			} else {
				al[0].addAndGet(al[1].get());
			}
			
			return al[0].get();
		}
		
		protected Long currentMaxIdValue(Session session, ClassMetadata metadata) {
			String sql = session.getDialect().maxIdSql(metadata.clazz, metadata.identifierField);
			Serializable id = session.unique(sql);
			return id == null ? null : Long.valueOf(id.toString());
		}
		
		protected Long nextIdValue(Session session, ClassMetadata metadata) {
			String sql = session.getDialect().nextIdentitySql(session.getDefaultDatabase(), metadata.clazz);
			Serializable id = session.unique(sql);
			return id == null ? 1L : Long.valueOf(id.toString());
		}
		
		protected Long incrementStep(Session session, ClassMetadata metadata) {
			String sql = session.getDialect().identityIncrementStep(session.getDefaultDatabase(), metadata.clazz);
			Serializable id = session.unique(sql);
			return id == null ? 1L : Long.valueOf(id.toString());
		}
	}

}
