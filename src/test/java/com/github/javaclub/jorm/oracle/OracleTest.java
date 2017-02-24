/*
 * @(#)OracleTest.java	2010-4-22
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.oracle;

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
 * @version $Id: OracleTest.java 479 2011-09-26 14:01:29Z gerald.chen.hz $
 */
public class OracleTest {
	
	static Session session;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		session = Jorm.getSession("Oracle");
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
		session = Jorm.getSession("Oracle");
		
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
		User user = new User(396, "陈志", "男", 21, "Java工程师");
		session.update(user);
		System.out.println(user);
	}
	
	@Test
	public void testUpdateUuid() {
		UserBean user = new UserBean("708f687991634c67ae4f1792f3b3ca11", "YhKO3n", "男", 998, "Java高级开发工程师");
		session.update(user);
		System.out.println(user.getId());
	}
	
	@Test
	public void testDeleteAuto() {
		// User user = new User(396, "陈志强", "男", 25, "IT工程师");
		User user = new User(396, "陈志", "男", 21, "Java工程师");
		session.delete(user);
	}
	
	@Test
	public void testDeleteUuid() {
		UserBean user = new UserBean("708f687991634c67ae4f1792f3b3ca11", "YhKO3n", "男", 998, "Java高级开发工程师");
		session.delete(user);
	}
	
	@Test
	public void testContainsAuto() {
		// User user = new User(396, "陈志强", "男", 25, "IT工程师");
		User user = new User(397, "陈志强", "男", 21, "Java工程师");
		System.out.println(session.has(user));
	}
	
	@Test
	public void testContainsUuid() {
		UserBean user = new UserBean("07bd3214aee84936a2a986efa13d5cdf", "曦勤", "男", 998, "Java高级开发工程师");
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
		
		flag = session.existsTable("scott", "dept");
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
		UserBean user = session.read(UserBean.class, "6eb1a2886d544b37a6e788438535babc", "9HFohM");
		System.out.println(user);
		
		UserBean user2 = session.read(UserBean.class);
		System.out.println(user2);
		
		UserBean user3 = session.loadFirst(UserBean.class, "SELECT * FROM t_user_entity WHERE u_age > 68");
		System.out.println(user3);
		
		UserBean user4 = session.loadFirst(UserBean.class, "SELECT * FROM t_user_entity WHERE user_name LIKE ? AND u_age > ?", "TB%", 18);
		System.out.println(user4);
	}
	
	@Test
	public void testRead3() {
		String user = session.loadFirst(String.class, "SELECT user_id FROM t_user_entity WHERE user_name = 'UE4kic'");
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
		
		List<User> user2 = session.list(User.class, "SELECT * FROM users WHERE age = 18");
		System.out.println(user2);
	}
	
	@Test
	public void testList3() {
		List<String> user = session.list(String.class, "SELECT name FROM users WHERE id > ?", 200);
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
		for (User user : page) {
			System.out.println(user);
		}
	}

}
