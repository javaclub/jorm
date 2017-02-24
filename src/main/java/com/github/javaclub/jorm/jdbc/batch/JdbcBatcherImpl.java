/*
 * @(#)JdbcBatcherImpl.java	2011-8-21
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.batch;

import java.sql.PreparedStatement;
import java.util.Set;
import java.util.Map.Entry;

import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.jdbc.DBUtil;
import com.github.javaclub.jorm.jdbc.JdbcException;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;
import com.github.javaclub.jorm.jdbc.sql.SqlPrepared;

/**
 * JdbcBatcherImpl
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: JdbcBatcherImpl.java 2011-8-21 下午08:34:26 Exp $
 */
@SuppressWarnings("unchecked")
public class JdbcBatcherImpl extends SQLBatcherImpl implements JdbcBatcher {
	
	private final Object lock = new Object();
	
	protected boolean submit = false;
	
	public JdbcBatcherImpl() {
		super();
	}

	public JdbcBatcherImpl(Session session) {
		super(session);
	}
	
	public void save(Object obj) throws JdbcException {
		synchronized(this.lock) {
			session.getPersister().getIdentifierValue(obj);
			SqlParams sqlParams = session.getPersister().insert(obj);
			try {
				addBatch(sqlParams.getSql(false), sqlParams.getParams());
			} finally {
				sqlParams = null;
				System.gc(); 
				try {
					Thread.sleep(3L);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public void update(Object obj) throws JdbcException {
		SqlParams sqlParams = SqlPrepared.preparedUpdate(obj);
		try {
			addBatch(sqlParams.getSql(false), sqlParams.getParams());
		} finally {
			sqlParams = null;
		}
	}

	public void delete(Object obj) throws JdbcException {
		SqlParams sqlParams = SqlPrepared.preparedDelete(obj);
		try {
			addBatch(sqlParams.getSql(false), sqlParams.getParams());
		} finally {
			sqlParams = null;
		}
	}

	public boolean hadSubmit() {
		return this.submit;
	}

	public void execute() throws JdbcException {
		boolean commit = false, rollback = false;
		if(getAutoCommit()) {
			setAutoCommit(false);
			commit = true;
		}
		try {
			executeBatch();
			this.submit = true;
		} catch(Throwable t) {
			t.printStackTrace();
			rollback = true;
			if(commit) {
				rollback();
			}
			if(t instanceof JdbcException) {
				throw (JdbcException) t;
			} else {
				throw new JdbcException("");
			}
			
		} finally {
			if (commit) {
				if (!rollback) {
					commit();
				}
				setAutoCommit(true);
				if(LOG.isInfoEnabled()) {
					LOG.info("JDBC Batcher is executed.");
				}
			}
			Set<Entry<String, PreparedStatement>> set = pstmts.entrySet();
			for (Entry<String, PreparedStatement> entry : set) {
				DBUtil.closeQuietly(entry.getValue());
				pstmts.put(entry.getKey(), null);
			}
			pstmts.clear();
			DBUtil.closeQuietly(stmt);
			stmt = null;
		}
	}
	
}
