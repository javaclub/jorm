/*
 * @(#)SQLBatcherImpl.java	2011-8-21
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.batch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.common.CommonUtil;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.config.JdbcConfigXmlParser;
import com.github.javaclub.jorm.jdbc.JdbcException;
import com.github.javaclub.jorm.jdbc.JdbcUtil;

/**
 * SQLBatcherImpl
 * 
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: SQLBatcherImpl.java 2011-8-21 下午07:05:09 Exp $
 */
public class SQLBatcherImpl extends AbstractBatcher implements SQLBatcher {

	protected int counter;

	protected Session session;

	protected Statement stmt;
	protected Map<String, PreparedStatement> pstmts = new HashMap<String, PreparedStatement>();

	public SQLBatcherImpl() {
		super();
	}

	public SQLBatcherImpl(Session session) {
		this.session = session;
	}
	
	public Session getSession() {
		return this.session;
	}

	public void addBatch(String sql, Object... params) throws JdbcException {
		if(checkAndAutoExecuteBatch(counter)) {
			// 下面的操作加上可以提升部分性能
			session.flush();
			System.gc(); 
			try {
				Thread.sleep(3L);
			} catch (InterruptedException e) {
			}
		}
		Connection connection = this.getConnection();
		try {
			if(JdbcConfigXmlParser.isShowSql()) {
				Jorm.format(sql);
			}
			if (CommonUtil.isEmpty(params)) {
				if (null == stmt) {
					stmt = connection.createStatement();
				}
				stmt.addBatch(sql);
			} else {
				String lower = Strings.lowerCase(sql);
				PreparedStatement ps = pstmts.get(lower);
				if (null == ps) {
					ps = connection.prepareStatement(sql);
					pstmts.put(lower, ps);
				}
				JdbcUtil.setParameters(ps, params);
				ps.addBatch();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new JdbcException(
					"Errors occured while executing method addBatch().", e);
		}
		counter++;
	}

	public void clearBatch() throws JdbcException {
		Set<Entry<String, PreparedStatement>> set = pstmts.entrySet();
		PreparedStatement ps;
		try {
			for (Entry<String, PreparedStatement> entry : set) {
				ps = entry.getValue();
				if (null != ps) {
					ps.clearBatch();
				}
			}
			if(null != stmt) {
				stmt.clearBatch();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new JdbcException(
					"Errors occured while executing method clearBatch().", e);
		}
		this.counter = 0;
	}

	public void executeBatch() throws JdbcException {
		Set<Entry<String, PreparedStatement>> set = pstmts.entrySet();
		PreparedStatement ps;
		try {
			for (Entry<String, PreparedStatement> entry : set) {
				ps = entry.getValue();
				if (null != ps) {
					ps.executeBatch();
				}
			}
			if(null != stmt) {
				stmt.executeBatch();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new JdbcException(
					"Errors occured while executing method executeBatch().", e);
		}
	}
	
	protected Connection getConnection() {
		return session.getConnection();
	}
	
	protected boolean getAutoCommit() throws JdbcException {
		Connection connection = this.getConnection();
		try {
			if (null == connection || connection.isClosed()) {
				throw new JdbcException("The JdbcSession had been closed already.");
			}
			return connection.getAutoCommit();
		} catch (Exception e) {
			throw new JdbcException(e);
		}
	}
	
	protected void setAutoCommit(boolean autoCommit) throws JdbcException {
		Connection connection = this.getConnection();
		try {
			if (null == connection || connection.isClosed()) {
				throw new JdbcException("The JdbcSession had been closed already.");
			}
			connection.setAutoCommit(autoCommit);
		} catch (Exception e) {
			throw new JdbcException(e);
		}
	}
	
	protected void commit() throws JdbcException {
		Connection connection = this.getConnection();
		try {
			if (null == connection || connection.isClosed()) {
				throw new JdbcException("The JdbcSession had been closed already.");
			}
			connection.commit();
		} catch (Exception e) {
			throw new JdbcException(e);
		}
	}
	
	protected void rollback() throws JdbcException {
		Connection connection = this.getConnection();
		try {
			if (null == connection || connection.isClosed()) {
				throw new JdbcException("The JdbcSession had been closed already.");
			}
			if (LOG.isInfoEnabled()) {
				LOG.info("Rolling back transaction of Session => " + session);
			}
			connection.rollback();
		} catch (Exception e) {
			throw new JdbcException(e);
		}
		
	}
	
}
