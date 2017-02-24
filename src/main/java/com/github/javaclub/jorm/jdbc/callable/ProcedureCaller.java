/*
 * @(#)ProcedureCaller.java	2011-9-13
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.callable;

import java.sql.CallableStatement;
import java.sql.SQLException;

import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.jdbc.DBUtil;

/**
 * A abstract procedure caller.
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: ProcedureCaller.java 2011-9-13 下午05:38:31 Exp $
 */
public abstract class ProcedureCaller {
	
	private Session session;
	
	public final Session getSession() {
		return this.session;
	}

	public final void setSession(Session session) {
		this.session = session;
	}

	public final Object execute() throws SQLException {
		CallableStatement cs = prepare();
		try {
			return callback(cs);
		} catch (SQLException sqle) {
			throw sqle;
		} finally {
			DBUtil.closeQuietly(cs);
			cs = null;
		}
	}
	
	/**
	 * Creates a CallableStatement by the given a Session.
	 *
	 * @return CallableStatement
	 * @throws SQLException
	 */
	public abstract CallableStatement prepare() throws SQLException;
	
	/**
	 * Allows to execute any number of operations on a single CallableStatement,
	 * for example a single execute call or repeated execute calls with varying parameters.
	 *
	 * @param cs CallableStatement
	 * @return result the executed procedure result.
	 * @throws SQLException
	 */
	public abstract Object callback(CallableStatement cs) throws SQLException;
}
