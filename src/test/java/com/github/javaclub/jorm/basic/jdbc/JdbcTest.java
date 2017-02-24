/*
 * @(#)JdbcTest.java	2012-1-7
 *
 * Copyright (c) 2012. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.basic.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.Test;

/**
 * JdbcTest
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: JdbcTest.java 2012-1-7 下午1:29:50 Exp $
 */
public class JdbcTest {

	@Test
	public void testGetConnection() throws Throwable {
		String driver = "com.mysql.jdbc.Driver";
		String jdbcurl = "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&amp;characterEncoding=UTF-8";
		String username = "root";
		String password = "root";
		
		long start = System.currentTimeMillis();
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(jdbcurl, username, password);
		long cost = System.currentTimeMillis() - start;
		System.out.println(cost);
		System.out.println(conn);
	}
}
