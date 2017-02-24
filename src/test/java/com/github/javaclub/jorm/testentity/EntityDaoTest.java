/*
 * @(#)UserEntityDaoTest.java	2010-5-8
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.testentity;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.common.Numbers;
import com.github.javaclub.jorm.common.Strings;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * desc
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: EntityDaoTest.java 192 2011-08-03 10:26:25Z gerald.chen.hz $
 */
public class EntityDaoTest {

	static Session session;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		session = Jorm.getSession();
	}
	
	@Test
	public void testAdd_UserEntity() {
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
					Integer.parseInt("aa");
				}
				System.out.println(user.getId());
			}
		} catch(Exception e) {
			session.rollback();
		} finally {
			session.endTransaction();
		}
		
	}
	
	@Test
	public void testAdd_User2() {
		session.clean(User2.class);
		User2 bean = null;
		for (int i = 0; i < 10; i++) {
			bean = new User2(Strings.random(6));
			session.save(bean);
			System.out.println(bean.getId());
		}
	}
	
	@Test
	public void testAdd_MySQLIdIncrementBean() throws Exception {
		session.clean(MySQLIdIncrementBean.class);
		MySQLIdIncrementBean bean = null;
		for (int i = 0; i < 10; i++) {
			bean = new MySQLIdIncrementBean(Strings.random(6));
			session.save(bean);
			System.out.println(bean.getId());
		}
	}
	
	@Test
	public void testMultiThreadAdd_MySQLIdIncrementBean() {
		session.clean(MySQLIdIncrementBean.class);
		AddDataThread t = null;
		for (int i = 0; i < 20; i++) {
			t = new AddDataThread(session, "adding-" + (i + 1));
			t.start();
			System.out.println(t.getName() + " start ...");
		}
		session.close();
		System.out.println("Holding On ...");
	}
	
	@Test
	public void testMultiThreadAdd_MySQLIdIncrementBean_2() {
		session.clean(MySQLIdIncrementBean.class);
		AddDataThread4 t = null;
		for (int i = 0; i < 20; i++) {
			t = new AddDataThread4("adding-" + (i + 1));
			t.start();
			System.out.println(t.getName() + " start ...");
		}
		//session.close();
		System.out.println("Holding On ...");
	}
	
	@Test
	public void testMultiThreadAdd_User2() {
		session.clean(User2.class);
		AddDataThread2 t = null;
		for (int i = 0; i < 20; i++) {
			t = new AddDataThread2(session, "adding-" + (i + 1));
			t.start();
			System.out.println(t.getName() + " start ...");
		}
		System.out.println("Holding On ...");
	}
	
	@Test
	public void testMultiThreadAdd3() {
		session.clean(MySQLIdIncrementBean.class);
		AddDataThread3 t = null;
		for (int i = 0; i < 20; i++) {
			t = new AddDataThread3(Jorm.getSession(), "adding-" + (i + 1));
			t.start();
			System.out.println(t.getName() + " start ...");
		}
		System.out.println("Holding On ...");
	}
	
	@Test
	public void testLoad_User() {
		session.clean(User.class);
		User user = null;
		for(int i = 0; i < 28; i++) {
			String sex = (i % 20)==0 ? "男" : "女";
			user = new User(Strings.fixed(6), sex, Numbers.random(80), Strings.fixed(10));
			session.save(user);
		}
		
		for(int i = 0; i < 28; i++) {
			user = session.loadFirst(User.class, "SELECT * FROM t_user WHERE id = " + (i + 1));
			System.out.println(user);
		}
	}
	
	@Test
	public void testList_User() {
		session.clean(User.class);
		User user = null;
		for(int i = 0; i < 28; i++) {
			String sex = (i % 20)==0 ? "男" : "女";
			user = new User(Strings.fixed(6), sex, Numbers.random(80), Strings.fixed(10));
			session.save(user);
		}
		
		List<User> list = session.list(User.class, "SELECT * FROM t_user WHERE career LIKE '%h%' OR career LIKE '%H%'");
		for(User usr : list) {
			System.out.println(usr);
		}
	}
	
	@Test
	public void testExecuteUpdate_SqlOnly() {
		session.clean(User.class);
		User user = null;
		for(int i = 0; i < 28; i++) {
			String sex = (i % 20)==0 ? "男" : "女";
			user = new User(Strings.fixed(6), sex, Numbers.random(80), Strings.fixed(10));
			session.save(user);
		}
		
		String sql = "UPDATE t_user SET career='PPPPP' WHERE id=1";
		session.beginTransaction();
		try {
			session.executeUpdate(sql);
			Integer.parseInt("kkkk");
		} catch(Exception e) {
			session.rollback();
		} finally {
			session.endTransaction();
		}
		
	}
	
	@Test
	public void testExecuteUpdate_SqlParam() {
		session.clean(User.class);
		User user = null;
		for(int i = 0; i < 28; i++) {
			String sex = (i % 20)==0 ? "男" : "女";
			user = new User(Strings.fixed(6), sex, Numbers.random(80), Strings.fixed(10));
			session.save(user);
		}
		
		String sql = "UPDATE t_user SET career=?, age=? WHERE id=1";
		session.beginTransaction();
		try {
			session.executeUpdate(sql, new Object[] {"工程师", 888});
			Integer.parseInt("kkkk");
		} catch(Exception e) {
			session.rollback();
		} finally {
			session.endTransaction();
		}
		
	}
	
	@Test
	public void testCount_SqlOnly() {
		session.clean(User.class);
		User user = null;
		for(int i = 0; i < 28; i++) {
			String sex = (i % 20)==0 ? "男" : "女";
			user = new User(Strings.fixed(6), sex, Numbers.random(80), Strings.fixed(10));
			session.save(user);
		}
		
		String sql = "SELECT * FROM t_user WHERE career LIKE '%h%' OR career LIKE '%H%'";
		System.out.println(session.count(sql));
		
	}
	
	@Test
	public void testCount_SqlParam() {
		session.clean(User.class);
		User user = null;
		for(int i = 0; i < 28; i++) {
			String sex = (i % 20)==0 ? "男" : "女";
			user = new User(Strings.fixed(6), sex, Numbers.random(80), Strings.fixed(10));
			session.save(user);
		}
		
		String sql = "SELECT * FROM t_user WHERE career LIKE ? OR career LIKE ?";
		System.out.println(session.count(sql, new Object[] {"%h%", "%H%"}));
		
	}
	
	@Test
	public void testDelete_SqlOnly() {
		session.clean(User.class);
		User user = null;
		for(int i = 0; i < 28; i++) {
			String sex = (i % 20)==0 ? "男" : "女";
			user = new User(Strings.fixed(6), sex, Numbers.random(80), Strings.fixed(10));
			session.save(user);
		}
		
		String sql = "DELETE FROM t_user WHERE id > 8";
		System.out.println(session.delete(sql));
	}
	
	@Test
	public void testDelete_SqlOnly_Tx() {
		session.clean(User.class);
		User user = null;
		for(int i = 0; i < 28; i++) {
			String sex = (i % 20)==0 ? "男" : "女";
			user = new User(Strings.fixed(6), sex, Numbers.random(80), Strings.fixed(10));
			session.save(user);
		}
		
		String sql = "DELETE FROM t_user WHERE id > 8";
		session.beginTransaction();
		try {
			System.out.println(session.delete(sql));
			//Integer.parseInt("kkkk");
		} catch(Exception e) {
			session.rollback();
		} finally {
			session.endTransaction();
		}
	}
	
	
	@Test 
	public void queryMap() {
		Map<String, Object> map = session.queryMap("SHOW TABLE STATUS WHERE name='t_incre'");
		System.out.println(map);
	}
	
	@Test
	public void testMultilGetSession() {
		Thread t = null;
		for (int i = 0; i < 20; i++) {
			t = new SessionGetThread("session-get-thread" + (i + 1));
			t.start();
			System.out.println(t.getName() + " start ...");
		}
		System.out.println("Holding On ...");
	}
	
	@Test
	public void testGetSession() {
		Session session = null;
		for (int i = 0; i < 200; i++) {
			session = Jorm.getSession();
			//session.close();
			System.out.println(session + " => " + session.getConnection());
		}
	}
}


class SessionGetThread extends Thread {

	public SessionGetThread() {
		super();
	}

	public SessionGetThread(String name) {
		super(name);
	}

	public void run() {
		Session session = null;
		for (int i = 0; i < 30; i++) {
			session = Jorm.getSession();
			System.out.println(session + " -> " + getName() + "\t" +
					"conn -> " + session.getConnection());
			// System.out.println(Jorm.getConnection() + " -> " + getName());
		}
	}
	
}


class AddDataThread extends Thread {
	
	private Session session;
	
	public AddDataThread() {
		super();
	}

	public AddDataThread(Session session, String name) {
		super(name);
		this.session = session;
	}

	public void run() {
		MySQLIdIncrementBean bean = null;
		for (int i = 0; i < 100; i++) {
			bean = new MySQLIdIncrementBean(Strings.random(6));
			session.save(bean);
			System.out.println(bean.getId());
		}
	}
}

class AddDataThread2 extends Thread {
	
	private Session session;
	
	public AddDataThread2() {
		super();
	}

	public AddDataThread2(Session session, String name) {
		super(name);
		this.session = session;
	}

	public void run() {
		User2 bean = null;
		for (int i = 0; i < 100; i++) {
			bean = new User2(Strings.random(6));
			session.save(bean);
			System.out.println(bean.getId());
		}
	}
}

class AddDataThread3 extends Thread {
	
	private Session session;
	
	public AddDataThread3() {
		super();
	}

	public AddDataThread3(Session session, String name) {
		super(name);
		this.session = session;
	}

	public void run() {
		MySQLIdIncrementBean bean = null;
		for (int i = 0; i < 100; i++) {
			bean = new MySQLIdIncrementBean(Strings.random(6));
			session.save(bean);
			System.out.println(bean.getId());
		}
		session.close();
	}
}

class AddDataThread4 extends Thread {
	
	private Session session;
	
	public AddDataThread4() {
		super();
	}

	public AddDataThread4(String name) {
		super(name);
		this.session = Jorm.getSession();
	}

	public void run() {
		MySQLIdIncrementBean bean = null;
		for (int i = 0; i < 100; i++) {
			bean = new MySQLIdIncrementBean(Strings.random(6));
			session.save(bean);
			System.out.println(bean.getId());
		}
	}
}
