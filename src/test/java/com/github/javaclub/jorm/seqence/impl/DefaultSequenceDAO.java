/*
 * @(#)DefaultSequenceDao.java	2017-3-16
 *
 * Copyright (c) 2017. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.seqence.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.javaclub.jorm.seqence.SequenceDAO;
import com.github.javaclub.jorm.seqence.SequenceException;
import com.github.javaclub.jorm.seqence.SequenceRange;

/**
 * DefaultSequenceDao
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: DefaultSequenceDao.java 2017-3-16 22:09:27 Exp $
 */
public class DefaultSequenceDAO implements SequenceDAO {

	private static final Log log = LogFactory.getLog(DefaultSequenceDAO.class);
	
	private static final int MIN_STEP = 1;
	private static final int MAX_STEP = 100000;
	private static final int DEFAULT_STEP = 1000;
	private static final int DEFAULT_RETRY_TIMES = 150;
	private static final String DEFAULT_TABLE_NAME = "sequence";
	private static final String DEFAULT_NAME_COLUMN_NAME = "name";
	private static final String DEFAULT_VALUE_COLUMN_NAME = "value";
	private static final String DEFAULT_GMT_MODIFIED_COLUMN_NAME = "gmt_modified";
	private static final long DELTA = 100000000L;
	
	private DataSource dataSource;
	private int retryTimes;
	private int step;
	private String tableName;
	private String nameColumnName;
	private String valueColumnName;
	private String gmtModifiedColumnName;
	private volatile String selectSql;
	private volatile String updateSql;

	public DefaultSequenceDAO() {
		this.retryTimes = DEFAULT_RETRY_TIMES;
		this.step = DEFAULT_STEP;
		this.tableName = DEFAULT_TABLE_NAME;
		this.nameColumnName = DEFAULT_NAME_COLUMN_NAME;
		this.valueColumnName = DEFAULT_VALUE_COLUMN_NAME;
		this.gmtModifiedColumnName = DEFAULT_GMT_MODIFIED_COLUMN_NAME;
	}

	public SequenceRange nextRange(String name) throws SequenceException {
		if (name == null) {
			throw new IllegalArgumentException("序列名称不能为空");
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		for (int i = 0; i < this.retryTimes + 1; ++i) {
			long oldValue = -1;
			long newValue = -1;
			try {
				conn = this.dataSource.getConnection();
				stmt = conn.prepareStatement(getSelectSql());
				stmt.setString(1, name);
				rs = stmt.executeQuery();
				rs.next();
				oldValue = rs.getLong(1);

				if (oldValue < 0L) {
					StringBuilder message = new StringBuilder();
					message.append("Sequence value cannot be less than zero, value = ").append(oldValue);
					message.append(", please check table ").append(getTableName());

					throw new SequenceException(message.toString());
				}

				if (oldValue > Long.MAX_VALUE) { 
					StringBuilder message = new StringBuilder();
					message.append("Sequence value overflow, value = ").append(oldValue);
					message.append(", please check table ").append(getTableName());

					throw new SequenceException(message.toString());
				}

				newValue = oldValue + getStep();
			} catch (SQLException e) {
			} finally {
				closeResultSet(rs);
				rs = null;
				closeStatement(stmt);
				stmt = null;
				closeConnection(conn);
				conn = null;
			}
			try {
				conn = this.dataSource.getConnection();
				stmt = conn.prepareStatement(getUpdateSql());
				stmt.setLong(1, newValue);
				stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
				stmt.setString(3, name);
				stmt.setLong(4, oldValue);
				int affectedRows = stmt.executeUpdate();
				if (affectedRows == 0) {
					closeStatement(stmt);
					stmt = null;
					closeConnection(conn);
					conn = null;
				} else {
					SequenceRange localSequenceRange = new SequenceRange(oldValue + 1L, newValue);

					return localSequenceRange;
				}
			} catch (SQLException e) {
			} finally {
				closeStatement(stmt);
				stmt = null;
				closeConnection(conn);
				conn = null;
			}
		}

		throw new SequenceException("Retried too many times, retryTimes = " + this.retryTimes);
	}

	private String getSelectSql() {
		if (this.selectSql == null) {
			synchronized (this) {
				if (this.selectSql == null) {
					StringBuilder buffer = new StringBuilder();
					buffer.append("select ").append(getValueColumnName());
					buffer.append(" from ").append(getTableName());
					buffer.append(" where ").append(getNameColumnName()).append(" = ?");

					this.selectSql = buffer.toString();
				}
			}
		}

		return this.selectSql;
	}

	private String getUpdateSql() {
		if (this.updateSql == null) {
			synchronized (this) {
				if (this.updateSql == null) {
					StringBuilder buffer = new StringBuilder();
					buffer.append("UPDATE ").append(getTableName());
					buffer.append(" SET ").append(getValueColumnName()).append(" = ?, ");
					buffer.append(getGmtModifiedColumnName()).append(" = ? WHERE ");
					buffer.append(getNameColumnName()).append(" = ? AND ");
					buffer.append(getValueColumnName()).append(" = ?");

					this.updateSql = buffer.toString();
				}
			}
		}

		return this.updateSql;
	}

	private static void closeResultSet(ResultSet rs) {
		if (rs == null)
			return;
		try {
			rs.close();
		} catch (SQLException e) {
			log.debug("Could not close JDBC ResultSet", e);
		} catch (Throwable e) {
			log.debug("Unexpected exception on closing JDBC ResultSet", e);
		}
	}

	private static void closeStatement(Statement stmt) {
		if (stmt == null)
			return;
		try {
			stmt.close();
		} catch (SQLException e) {
			log.debug("Could not close JDBC Statement", e);
		} catch (Throwable e) {
			log.debug("Unexpected exception on closing JDBC Statement", e);
		}
	}

	private static void closeConnection(Connection conn) {
		if (conn == null)
			return;
		try {
			conn.close();
		} catch (SQLException e) {
			log.debug("Could not close JDBC Connection", e);
		} catch (Throwable e) {
			log.debug("Unexpected exception on closing JDBC Connection", e);
		}
	}

	public DataSource getDataSource() {
		return this.dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public int getRetryTimes() {
		return this.retryTimes;
	}

	public void setRetryTimes(int retryTimes) {
		if (retryTimes < 0) {
			throw new IllegalArgumentException("Property retryTimes cannot be less than zero, retryTimes = " + retryTimes);
		}

		this.retryTimes = retryTimes;
	}

	public int getStep() {
		return this.step;
	}

	public void setStep(int step) {
		if ((step < MIN_STEP) || (step > MAX_STEP)) {
			StringBuilder message = new StringBuilder();
			message.append("Property step out of range [").append(1);
			message.append(",").append(100000).append("], step = ").append(step);

			throw new IllegalArgumentException(message.toString());
		}

		this.step = step;
	}

	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getNameColumnName() {
		return this.nameColumnName;
	}

	public void setNameColumnName(String nameColumnName) {
		this.nameColumnName = nameColumnName;
	}

	public String getValueColumnName() {
		return this.valueColumnName;
	}

	public void setValueColumnName(String valueColumnName) {
		this.valueColumnName = valueColumnName;
	}

	public String getGmtModifiedColumnName() {
		return this.gmtModifiedColumnName;
	}

	public void setGmtModifiedColumnName(String gmtModifiedColumnName) {
		this.gmtModifiedColumnName = gmtModifiedColumnName;
	}
	
	public static void main(String[] args) {
		System.out.println(Long.MAX_VALUE);
	}

}
