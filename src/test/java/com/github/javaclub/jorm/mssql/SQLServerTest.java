/*
 * @(#)SQLServerTest.java	2010-4-22
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.mssql;

import java.util.List;

import junit.framework.Assert;

import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.common.Numbers;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.jdbc.criterion.Order;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;
import com.github.javaclub.jorm.testentity.User;
import com.github.javaclub.jorm.testentity.UserBean;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * desc
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: SQLServerTest.java 479 2011-09-26 14:01:29Z gerald.chen.hz $
 */
public class SQLServerTest {
	
	static Session session;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		session = Jorm.getSession("MSSQL2005");
	}

	@Test
	public void testSaveAuto() {
		User user = new User("陈志强", "男", 25, "IT工程师");
		for (int i = 0; i < 61; i++) {
			user.setName(Strings.random(6));
			user.setAge(Numbers.random(30));
			session.save(user);
		}
		session.save(user);
		System.out.println(user.getId());;
	}
	
	@Test
	public void testSaveUuid() {
		
		UserBean user = new UserBean(Strings.random(6), "男", 20, "Java开发工程师");
		for (int i = 0; i < 1000; i++) {
			user.setName(Strings.random(6));
			user.setAge(Numbers.random(30));
			session.save(user);
		}
		System.out.println(user.getId());
	}


	@Test
	public void testUpdateAuto() {
		// User user = new User(396, "陈志强", "男", 25, "IT工程师");
		User user = new User(28, "陈志", "男", 21, "Java工程师");
		session.update(user);
		System.out.println(user);
	}
	
	@Test
	public void testUpdateUuid() {
		UserBean user = new UserBean("3eca34cd172e4c91bdb0697c6e093ce0", "zqI6n4", "男", 998, "Java高级开发工程师");
		session.update(user);
		System.out.println(user.getId());
	}
	
	@Test
	public void testDeleteAuto() {
		// User user = new User(396, "陈志强", "男", 25, "IT工程师");
		User user = new User(28, "陈志", "男", 21, "Java工程师");
		session.delete(user);
	}
	
	@Test
	public void testDeleteUuid() {
		UserBean user = new UserBean("3eca34cd172e4c91bdb0697c6e093ce0", "zqI6n4", "男", 998, "Java高级开发工程师");
		session.delete(user);
	}
	
	@Test
	public void testContainsAuto() {
		// User user = new User(396, "陈志强", "男", 25, "IT工程师");
		User user = new User(1, "陈志强", "男", 21, "Java工程师");
		System.out.println(session.has(user));
	}
	
	@Test
	public void testContainsUuid() {
		UserBean user = new UserBean("bd3292503a8543459a0a8ca907a26cd5", "4BOg1S", "男", 998, "Java高级开发工程师");
		System.out.println(session.has(user));
	}
	
	@Test
	public void testSaveOrUpdateAuto() {
		// User user = new User(396, "陈志强", "男", 25, "IT工程师");
		User user = new User(400, "陈志强", "男", 21, "Java工程师");
		session.saveOrUpdate(user);
	}
	
	@Test
	public void testSaveOrUpdateUuid() {
		UserBean user = new UserBean("07bd3214aee84936a2a986efa13d5cd1", "曦勤", "男", 998, "Java高级开发工程师");
		session.saveOrUpdate(user);
	}

	@Test
	public void testExsitsTable() {
		boolean flag = session.existsTable("users");
		Assert.assertTrue(flag == true);
		
		flag = session.existsTable("ttt");
		Assert.assertTrue(flag == false);
		
		flag = session.existsTable("test", "t_user_entity");
		Assert.assertTrue(flag == true);
		
		flag = session.existsTable("ReportServer", "Batch");
		Assert.assertTrue(flag == true);
	}
	
	@Test
	public void testRead1() {
		User user = session.read(User.class, 1);
		System.out.println(user);
		
		User user2 = session.read(User.class);
		System.out.println(user2);
	}
	
	@Test
	public void testRead2() {
		UserBean user = session.read(UserBean.class, "1378cec100634ea3933c5c5256fc0ab9", "lSZvgD");
		System.out.println(user);
		
		UserBean user2 = session.read(UserBean.class);
		System.out.println(user2);
		
		UserBean user3 = session.loadFirst(UserBean.class, "SELECT * FROM t_user_entity WHERE u_age > 21");
		System.out.println(user3);
		
		UserBean user4 = session.loadFirst(UserBean.class, "SELECT * FROM t_user_entity WHERE user_name LIKE ? AND u_age > ?", "Ufp%", 18);
		System.out.println(user4);
	}
	
	@Test
	public void testRead3() {
		String user = session.loadFirst(String.class, "SELECT user_id FROM t_user_entity WHERE user_name = 'lSZvgD'");
		System.out.println(user);
		
		int user2 = session.loadFirst(int.class, "SELECT u_age FROM t_user_entity WHERE user_name LIKE ? AND u_age > ?", "%a%", 1);
		System.out.println(user2);
	}
	
	@Test
	public void testList1() {
		List<User> user = session.all(User.class);
		System.out.println(user);
		
		List<User> user2 = session.list(User.class, "SELECT * FROM users WHERE age > ?", 18);
		System.out.println(user2);
	}
	
	@Test
	public void testList2() {
		List<User> user1 = session.list(User.class, "SELECT * FROM users WHERE id > ? AND age > ?", 50, 20);
		System.out.println(user1);
		
		List<User> user2 = session.list(User.class, "SELECT * FROM users WHERE age > 18");
		System.out.println(user2);
	}
	
	@Test
	public void testList3() {
		List<String> user = session.list(String.class, "SELECT name FROM users WHERE id > ?", 20);
		System.out.println(user);
		
		List<Integer> user2 = session.list(int.class, "SELECT age FROM users WHERE age > 18");
		System.out.println(user2);
	}
	
	@Test
	public void testClean() {
		session.clean(User.class);
		
		List<User> user = session.all(User.class);
		System.out.println(user);
		
	}
	
	@Test
	public void page_0_just_select() {
		SqlParams<User> params = new SqlParams<User>("SELECT * FROM t_user where id > 6");
		params.setObjectClass(User.class);
		params.setFirstResult(3);
		params.setMaxResults(6);
		params.addOrder(Order.desc("id"));
		List<User> page = session.list(params);
		System.out.println(display(page));
		for (User user : page) {
			System.out.println(user);
		}
	}
	
	private static String display(List<User> list) {
		StringBuilder sb = new StringBuilder();
		for (User user : list) {
			sb.append(user.getId() + ",");
		}
		return sb.toString();
	}

}
