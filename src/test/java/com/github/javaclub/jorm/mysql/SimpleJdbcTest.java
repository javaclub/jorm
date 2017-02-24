/*
 * @(#)SimpleJdbcTest.java	2011-8-6
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.mysql;

import java.io.Serializable;
import java.sql.Connection;

import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.common.Numbers;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.jdbc.DBUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * SimpleJdbcTest
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: SimpleJdbcTest.java 328 2011-09-09 14:42:12Z gerald.chen.hz $
 */
public class SimpleJdbcTest {
	
	static String driver = "com.mysql.jdbc.Driver";
	static String jdbcurl = "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&amp;characterEncoding=UTF-8";
	static String username = "root";
	static String password = "root";
	
	static Connection conn;
	static Session session;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		session = Jorm.getSession();
		conn = DBUtil.getConnection(driver, jdbcurl, username, password);
		System.out.println(conn);
	}
	
	@AfterClass
	public static void destroyAfterClass() {
		Jorm.free();
	}
	
	@Test
	public void inOneConnection() throws InterruptedException {
		DBUtil.executeSQL(conn, "DELETE FROM t_user", false);
		for (int i = 0; i < 100; i++) {
			new JdbcSaveThread(Strings.fixed(3), conn, false).start();
			Thread.sleep(1000L);
		}
		
		System.out.println("OK...");
	}
	
	@Test
	public void requireNewConnection() {
		Connection conn = session.getConnection();
		System.out.println("0 => " + conn);
		for (int i = 0; i < 10; i++) {
			try {
				conn = session.getConnection(true);
				System.out.println((i + 1) + " => " + conn);
			} finally {
				//DBUtil.closeQuietly(conn);
			}
		}
	}
	
	@Test
	public void createDB() {
		String cc = "create database test_db";
		int rows = session.executeUpdate(cc);
		System.out.println(rows);
	}
	
	@Test
	public void dropDB() {
		String cc = "drop database if exists test_db";
		int rows = session.executeUpdate(cc);
		System.out.println(rows);
	}
	
	@Test
	public void inNewConnection() throws InterruptedException {
		DBUtil.executeSQL(conn, "DELETE FROM t_user", false);
		for (int i = 0; i < 100; i++) {
			new JdbcSaveThread(Strings.fixed(3), conn, true).start();
			Thread.sleep(1000L);
		}
		
		System.out.println("OK...");
	}

}

class JdbcSaveThread extends Thread {
	
	static String driver = "com.mysql.jdbc.Driver";
	static String jdbcurl = "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&amp;characterEncoding=UTF-8";
	static String username = "root";
	static String password = "root";
	
	private Connection conn;
	private boolean isNew;

	public JdbcSaveThread() {
		super();
	}

	public JdbcSaveThread(String name, Connection conn, boolean isNew) {
		super(name);
		this.conn = conn;
		this.isNew = isNew;
		if(isNew) {
			this.conn = DBUtil.getConnection(driver, jdbcurl, username, password);
		}
	}

	public void run() {
		String sql = "insert into `t_user` (`id`, `name`, `sex`, `age`, `career`) values(?,?,?,?,?)";
		try {
			Serializable id = null;
			for (int i = 0; i < 100; i++) {
				String sex = (i % 2 == 0 ? "男" : "女");
				id = DBUtil.queryUniqueObject(this.conn, "SELECT (MAX(id) + 1) FROM t_user", false);
				DBUtil.executeUpdate(this.conn, sql, new Object[] {
						(id == null ? 1 : id),
						Strings.fixed(6),
						sex,
						Numbers.random(100),
						Strings.random(8)
				}, false);
			}
		} finally {
			if(isNew) {
				DBUtil.closeQuietly(this.conn);
			}
		}
	}
	
}
