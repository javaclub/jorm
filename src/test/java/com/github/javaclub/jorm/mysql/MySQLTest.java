/*
 * @(#)MySQLTest.java	2010-4-22
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.mysql;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.common.Numbers;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.jdbc.criterion.Order;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;
import com.github.javaclub.jorm.testentity.MultiKeyBean;
import com.github.javaclub.jorm.testentity.MultiKeySubBean;
import com.github.javaclub.jorm.testentity.NopkBean;
import com.github.javaclub.jorm.testentity.User;
import com.github.javaclub.jorm.testentity.UserBean;
import com.github.javaclub.jorm.testentity.UserEntity;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * desc
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: MySQLTest.java 479 2011-09-26 14:01:29Z gerald.chen.hz $
 */
public class MySQLTest {
	
	static Session session;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		session = Jorm.getSession();
	}
	
	@AfterClass
	public static void destroyAfterClass() {
		Jorm.free();
	}

	@Test
	public void testSaveAuto() {
		User user = new User("陈志强", "男", 25, "IT工程师");
		for (int i = 0; i < 1000; i++) {
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
	public void testContains_NopkBean() {
		session.clean(NopkBean.class);
		NopkBean bean = new NopkBean(1l, "kkk", "java");
		Serializable id = session.save(bean);
		System.out.println(bean + " => id=" + id);
		
		System.out.println(session.has(new NopkBean(1l, "kkk", "java")));
		session.update(new NopkBean(1l, "kkk", "java"));
		
		session.delete(new NopkBean(1l, "kkk", "java"));
	}
	
	@Test
	public void testUpdate_UserEntity() {
		session.clean(UserEntity.class);
		UserEntity user = null;
		session.beginTransaction();
		try {
			for (int i = 0; i < 20; i++) {
				user = new UserEntity(Strings.random(6), Numbers.random(30));
				// System.out.println(user.getAge());
				if(user.getAge() < 18) {
					user.setAdult(false);
				} else {
					user.setAdult(true);
				}
				user.setPassword(Strings.random(16));
				user.setCreateTime(new Date());
				user.setUpdateTime(new Date());
				user.setCreateUser(Strings.random(6));
				user.setUpdateUser(Strings.random(6));
				session.save(user);
				if(i == 9) {
					// Integer.parseInt("aa");
				}
				System.out.println(user.getId());
			}
		} catch(Exception e) {
			session.rollback();
		} finally {
			session.endTransaction();
		}
		
		UserEntity entity = session.read(UserEntity.class, 1);
		System.out.println(entity);
		
		entity.setAge(88);
		entity.setUsername("kkkkkkkkkkkk");
		
		session.update(entity);
		
		entity = session.read(UserEntity.class, 1);
		System.out.println(entity);
		
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
		UserBean user = new UserBean("086ca0c2491849d0b1d1aeac0a77d417", "曦勤111", "男", 998, "Java高级开发工程师");
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
		UserBean user = new UserBean("086ca0c2491849d0b1d1aeac0a77d417", "曦勤111", "男", 998, "Java高级开发工程师");
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
		UserBean user = new UserBean("082bdd251dcf453cbe606c7b3c263de3", "曦勤", "男", 998, "Java高级开发工程师");
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
		
		flag = session.existsTable("command", "ark_log");
		Assert.assertTrue(flag == true);
	}
	
	// ============== Read ==============
	
	@Test
	public void testRead1() {
		session.clean(User.class);
		User u = new User("Kate", "女", 20, "会计");
		Serializable id = session.save(u);
		User user = session.read(User.class, id);
		System.out.println(user);
		
		User user2 = session.read(User.class);
		System.out.println(user2);
	}
	
	@Test
	public void testRead2() {
		UserBean user = session.read(UserBean.class, "080206918cb146d5b55d1411f7fb61c5", "kAbpzQ");
		System.out.println(user);
		
		UserBean user2 = session.read(UserBean.class);
		System.out.println(user2);
		
		UserBean user3 = session.loadFirst(UserBean.class, "SELECT * FROM t_user_bean WHERE u_age > 68");
		System.out.println(user3);
		
		UserBean user4 = session.loadFirst(UserBean.class, "SELECT * FROM t_user_bean WHERE user_name LIKE ? AND u_age > ?", "曦%", 18);
		System.out.println(user4);
	}
	
	@Test
	public void testRead3() {
		String user = session.loadFirst(String.class, "SELECT user_id FROM t_user_bean WHERE user_name = 'Xq2Y'");
		System.out.println(user);
		
		int user2 = session.loadFirst(int.class, "SELECT u_age FROM t_user_bean WHERE user_name LIKE ? AND u_age > ?", "曦%", 68);
		System.out.println(user2);
	}
	
	@Test
	public void testList1() {
		List<User> user = session.all(User.class);
		System.out.println(user);
		
		List<User> user2 = session.list(User.class, "SELECT * FROM t_user WHERE age > ?", 18);
		System.out.println(user2);
	}
	
	@Test
	public void testList2() {
		List<User> user1 = session.list(User.class, "SELECT * FROM t_user WHERE id > ? AND age > ?", 50, 20);
		System.out.println(user1);
		
		List<User> user2 = session.list(User.class, "SELECT * FROM t_user WHERE age = 18");
		System.out.println(user2);
	}
	
	@Test
	public void testList3() {
		List<String> user = session.list(String.class, "SELECT name FROM t_user WHERE id > ?", 200);
		System.out.println(user);
		
		List<Integer> user2 = session.list(int.class, "SELECT age FROM t_user WHERE age > 18");
		System.out.println(user2);
	}
	
	@Test
	public void testClean() {
		session.clean(User.class);
		
		List<User> user = session.all(User.class);
		System.out.println(user);
		
	}
	
	@Test
	public void page_0() {
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
	
	//=========== test relation ===============
	
	@Test
	public void testRelation1() {
		// insert data
		session.clean(MultiKeyBean.class);
		session.clean(MultiKeySubBean.class);
		MultiKeyBean parent = null;
		MultiKeySubBean sub = null;
		for(int i = 0;i < 28; i++) {
			parent = new MultiKeyBean(Strings.fixed(3), Strings.random(10));
			session.save(parent);
			for(int j = 0;j < 10; j++) {
				sub = new MultiKeySubBean(parent.getName(), Strings.random(18));
				session.save(sub);
				System.out.println(sub.getParent());
			}
		}
		
		for(int i = 0;i < 28; i++) {
			parent = session.read(MultiKeyBean.class, i+1);
			System.out.println((i + 1) + " => " + parent.getName() + " => " + parent.getSubBeans(1, 1000).size() + " => " + parent.getSubBeans(1, 1000));
		}
		
	}

}
