/*
 * @(#)SimpleTest.java	May 17, 2010
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.postgresql;

import java.sql.Connection;
import java.sql.SQLException;

import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.jdbc.DBUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * desc
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: SimpleTest.java 226 2011-08-08 05:29:35Z gerald.chen.hz $
 */
public class InitTest {
	
	static Session session;
	
	@BeforeClass
	public static void before() {
		session = Jorm.getSession("PostgreSQL");
	}
	
	@AfterClass
	public static void after() {
		Jorm.free();
	}

	@Test
	public void testInfo() throws SQLException {
		Connection connection = null;
		try {
			connection = Jorm.getConnection("PostgreSQL");
			System.out.println("Connection => " + connection);
			System.out.println("DatabaseInfo => " + DBUtil.getDatabaseInfo(connection));
		} finally {
			DBUtil.closeQuietly(connection);
		}
		
	}
	
	/**
	 * 此种语句在PostgreSQL中执行不通过
	 *
	 */
	@Test
	public void createDB() {
		String sql = "create database testdb";
		session.executeUpdate(sql);
	}
	
	/**
	 * 此种语句在PostgreSQL中执行不通过
	 *
	 */
	@Test
	public void dropDB() {
		String sql = "drop database testdb";
		session.executeUpdate(sql);
	}
	
	@Test
	public void createTable() throws SQLException {
		String sql = "create table t_user(" +
				"id int primary key, " +
				"name varchar, " +
				"sex varchar, " +
				"age int, " +
				"career varchar)";
		session.executeUpdate(sql);
	}
	
}
