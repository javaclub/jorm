/*
 * @(#)AbstractLazyInitializer.java	2011-8-30
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.proxy;

import java.io.Serializable;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.JormException;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;

/**
 * AbstractLazyInitializer
 * 
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: AbstractLazyInitializer.java 2011-8-30 下午06:55:21 Exp $
 */
@SuppressWarnings("unchecked")
public abstract class AbstractLazyInitializer implements LazyInitializer {
	
	private static final Log LOG = LogFactory.getLog(AbstractLazyInitializer.class);

	private Serializable id;
	private SqlParams sqlParams;
	
	private Object target;
	private boolean initialized;

	private transient Session session;

	protected AbstractLazyInitializer() {

	}
	
	protected AbstractLazyInitializer(Serializable id, Session session) {
		this.id = id;
		this.session = session;
	}
	
	protected AbstractLazyInitializer(Serializable id, SqlParams sqlParams, Session session) {
		this(id, session);
		this.sqlParams = sqlParams;
	}

	public final Serializable getIdentifier() {
		return this.id;
	}
	
	public final SqlParams getSqlParams() {
		return sqlParams;
	}

	public final void setSqlParams(SqlParams sqlParams) {
		this.sqlParams = sqlParams;
	}

	public Object getImplementation() {
		initialize();
		return target;
	}
	
	/**
	 * Getter for property 'target'.
	 * <p/>
	 * Same as {@link #getImplementation()} except that this method will not force initialization.
	 *
	 * @return Value for property 'target'.
	 */
	protected final Object getTarget() {
		return target;
	}

	public final Object getImplementation(Session session) throws JormException {
		Serializable id = this.getIdentifier();
		return loadEntity(session, id, this.getSqlParams());
	}

	public final Session getSession() {
		return this.session;
	}

	public void initialize() throws JormException {
		if (!initialized) {
			Session extra;
			if ( session == null ) {
				throw new JormException("could not initialize proxy - no Session");
			} else {
				if(session.isClosed()) {
					extra = Jorm.newSession();
					LOG.warn("The holding session is closed, using an new extra session instead.");
					try {
						target = loadEntity(extra, id, this.getSqlParams());
						initialized = true;
					} finally {
						extra.close();
						extra = null;
					}
				} else {
					target = loadEntity(session, id, this.getSqlParams());
					initialized = true;
				}
			}
		} else {
			if(target == null) {
				// throw new IllegalStateException("Entity had been initialized, but it is null, this may be a bug.");
			}
		}
	}

	public final boolean isUninitialized() {
		return !initialized;
	}

	public final void setIdentifier(Serializable id) {
		this.id = id;
	}

	public final void setImplementation(Object target) {
		this.target = target;
		initialized = true;
	}

	public final void setSession(Session s) throws JormException {
		if (s != session) {
			if (null == s || s.isClosed()) {
				throw new JormException(
						"illegally attempted to associate a proxy with two open Sessions");
			} else {
				session = s;
			}
		}
	}

	public void unsetSession() {
		session = null;
	}
	
	protected Object loadEntity(Session session, Serializable id, SqlParams sqlParams)
			throws JormException {
		try {
			return session.getPersister().loadBean(getPersistentClass(), id);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new JormException(e);
		}
	}

}
