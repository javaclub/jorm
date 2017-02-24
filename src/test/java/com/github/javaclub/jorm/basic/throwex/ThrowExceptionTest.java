/*
 * @(#)ThrowExceptionTest.java	2011-9-15
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.basic.throwex;

import com.github.javaclub.jorm.jdbc.JdbcException;

/**
 * ThrowExceptionTest
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: ThrowExceptionTest.java 2011-9-15 下午07:37:44 Exp $
 */
public class ThrowExceptionTest {

	public static void main(String[] args) {
		String sid = "Hello";
		User usr = new User(sid);
		try {
			new ThrowExceptionTest().m(usr);
		} finally {
			sid = null;
			usr = null;
		}

	}
	
	public void m(User u) throws JdbcException {
		u.insert();
	}

}

class User {
	
	private String sid;
	
	public User() {
		super();
	}
	
	public User(String sid) {
		super();
		this.sid = sid;
	}

	public final String getSid() {
		return sid;
	}

	public final void setSid(String sid) {
		this.sid = sid;
	}

	public void insert() throws JdbcException {
		throw new JdbcException("JdbcException happened.");
	}
}
