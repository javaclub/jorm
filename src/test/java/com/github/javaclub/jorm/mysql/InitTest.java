/*
 * @(#)SimpleTest.java	May 17, 2010
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.mysql;

import java.sql.Connection;
import java.sql.SQLException;

import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.jdbc.DBUtil;
import com.github.javaclub.jorm.jdbc.connection.DBConnection;
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
		// session = Jorm.getSession();
	}
	
	@AfterClass
	public static void after() {
		Jorm.free();
	}
	
	@Test
	public void doOnceConnect() {
		// 1.344, 1.250, 1.328, 1.390, 1.343, 1.219, 1.312
		long start = System.currentTimeMillis();
		Jorm.getConnection();
		long cost = System.currentTimeMillis() - start;
		System.out.println(cost);
		System.out.println(Jorm.getConnection());
	}
	
	@Test
	public void info() {
		for (int i = 0; i < 10; i++) {
			System.out.println(Jorm.getSession());
		}
	}

	@Test
	public void testInfo_c3p0() throws SQLException {
		Connection connection = null;
		try {
			connection = Jorm.getConnection("MSSQL2005");
			System.out.println("Connection => " + connection);
			System.out.println("DatabaseInfo => " + DBUtil.getDatabaseInfo(connection));
		} finally {
			DBUtil.closeQuietly(connection);
		}
		
	}
	
	@Test
	public void testInfo_dbcp() throws SQLException {
		Connection connection = null;
		try {
			connection = Jorm.getConnection("Oracle");
			System.out.println("Connection => " + connection);
			System.out.println("DatabaseInfo => " + DBUtil.getDatabaseInfo(connection));
		} finally {
			DBUtil.closeQuietly(connection);
		}
		
	}
	
	@Test
	public void test_dbcp_get_conn() throws SQLException {
		Connection connection = null;
		for (int i = 0; i < 100; i++) {
			connection = Jorm.getConnection("dbcp");
			System.out.println("Connection => " + connection);
			DBUtil.closeQuietly(connection);
		}
	}
	
	@Test
	public void test_proxool_get_conn() throws Exception {
		
		String provider = "proxool_original_used";// proxool
		Connection connection = null;
		for (int i = 0; i < 100; i++) {
			connection = Jorm.getConnection(provider);
			System.out.println("Connection => " + connection);
			/*if(i == 58) {
				DBConnection.getImplementation(provider).realReleaseAllConnections();
			}*/
			DBUtil.closeQuietly(connection);
		}
	}
	
	@Test
	public void test_get_connection() throws Exception {
		
		String provider = "bonecp";// proxool
		Connection connection = null;
		for (int i = 0; i < 100; i++) {
			connection = Jorm.getConnection(provider);
			System.out.println("Connection " + (i + 1) + " => " + connection);
			if(i == 58) {
				DBConnection.getImplementation(provider).realReleaseAllConnections();
			}
			DBUtil.closeQuietly(connection);
		}
	}
	
	@Test
	public void createDB() {
		String sql = "create database testdb";
		session.beginTransaction();
		try {
			session.executeUpdate(sql);
			Integer.parseInt("ewrwe");
		} catch (Exception e) {
			e.printStackTrace();
			session.rollback();
		} finally {
			session.endTransaction();
		}
	}
	
	@Test
	public void dropDB() {
		String sql = "drop database testdb";
		session.executeUpdate(sql);
	}
	
}
