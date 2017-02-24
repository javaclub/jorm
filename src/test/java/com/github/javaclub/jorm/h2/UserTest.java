/*
 * @(#)UserTest.java	2011-7-22
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.h2;

import java.util.ArrayList;
import java.util.List;

import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.common.Numbers;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.jdbc.criterion.Order;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;
import com.github.javaclub.jorm.testentity.User;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * UserTest
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: UserTest.java 2011-7-22 下午04:34:14 Exp $
 */
public class UserTest {

	static Session session;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		session = Jorm.getSession("h2");
	}
	
	@Test
	public void save() {
		session.clean(User.class);
		User u;
		for(int i = 0; i < 10; i++) {
			String sex = (i % 2 == 0 ? "男" : "女");
			u = new User(Strings.fixed(6), sex, Numbers.random(100), Strings.random(16));
			session.save(u);
		}
		
		for (int i = 0; i < 10; i++) {
			u = session.read(User.class, i + 1);
			System.out.println(u);
		}
	}
	
	@Test
	public void update() {
		session.clean(User.class);
		User user = new User(Strings.fixed(6), "男", 99, "job");
		System.out.println(session.save(user));
		
		User u = session.read(User.class, 1);
		u.setCareer("driver");
		u.setAge(199);
		
		session.update(u);
		User usr = session.read(User.class, 1);
		System.out.println(usr);
	}
	
	@Test
	public void contains() {
		session.clean(User.class);
		User user = new User("myname", "男", 99, "job");
		System.out.println(session.save(user));
		
		User u = session.read(User.class, 1);
		u.setCareer("driver");
		u.setAge(199);
		System.out.println(session.has(u));
		
		User usr = new User(2, "myname", "男", 99, "job");
		System.out.println(session.has(usr));
	}
	
	@Test
	public void delete_obj() {
		session.clean(User.class);
		User user = new User("myname", "男", 99, "job");
		System.out.println(session.save(user));
		
		User u = session.read(User.class, 1);
		session.delete(u);
		System.out.println(session.read(User.class, 1));
	}
	
	@Test
	public void delete_sql_1() {
		session.clean(User.class);
		User u;
		for(int i = 0; i < 1000; i++) {
			String sex = (i % 2 == 0 ? "男" : "女");
			u = new User(Strings.fixed(6), sex, Numbers.random(100), Strings.random(16));
			session.save(u);
		}
		
		int count = session.delete("DELETE FROM t_user");
		System.out.println(count);
	}
	
	@Test
	public void delete_sql_2() {
		session.clean(User.class);
		User u;
		for(int i = 0; i < 1000; i++) {
			String sex = (i % 2 == 0 ? "男" : "女");
			u = new User(Strings.fixed(6), sex, Numbers.random(100), Strings.random(16));
			session.save(u);
		}
		int count = session.delete(User.class, "id > 100");
		System.out.println(count);
	}
	
	@Test
	public void load_first() {
		session.clean(User.class);
		User u;
		for(int i = 0; i < 1000; i++) {
			String sex = (i % 2 == 0 ? "男" : "女");
			u = new User(Strings.fixed(6), sex, Numbers.random(100), Strings.random(16));
			session.save(u);
		}
		User user = session.loadFirst(User.class, "(SELECT * FROM t_user WHERE id > ?)", 100);
		System.out.println(user);
	}
	
	@Test
	public void list_1() {
		session.clean(User.class);
		User u;
		for(int i = 0; i < 1000; i++) {
			String sex = (i % 2 == 0 ? "男" : "女");
			u = new User(Strings.fixed(6), sex, Numbers.random(100), Strings.random(16));
			session.save(u);
		}
		List<User> users = session.list(new SqlParams<User>("SELECT * FROM t_user WHERE id > ?", new Object[] {100}).setObjectClass(User.class));
		System.out.println(users.size());
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
	
	
	@Test
	public void batch_insert_1() {
		session.clean(User.class);
		String sql = "INSERT INTO t_user(sex,age,career,name,id) VALUES(?,?,?,?,?)";
		List<Object[]> datalist = new ArrayList<Object[]>();
		for (int i = 0; i < 1000; i++) {
			datalist.add(new Object[] {"男", Numbers.random(98), 
					Strings.random(10), 
					Strings.fixed(6), (i+1) });
			
		}
		session.batchInsert(sql, datalist);
	}
	
	@Test
	public void tx_1() {
		session.clean(User.class);
		User u;
		session.beginTransaction();
		try {
			for(int i = 0; i < 1000; i++) {
				String sex = (i % 2 == 0 ? "男" : "女");
				u = new User(Strings.fixed(6), sex, Numbers.random(100), Strings.random(16));
				session.save(u);
				if(i == 886) {
					//Integer.parseInt("kkk");
				}
			}
		} catch (Exception e) {
			session.rollback();
		} finally {
			session.endTransaction();
		}
	}

	/**
	 * 测试结果：
	 * 如果不同的线程使用同一个session是不会出现ID重复的问题，
	 * 但是不同的线程使用不同的session就有问题了
	 * 
	 * => 后来通过IncreasableIdentifiers类解决了此问题
	 * @throws InterruptedException 
	 *
	 */
	@Test
	public void conncurency() throws InterruptedException {
		session.clean(User.class);
		for (int i = 0; i < 100; i++) {
			// new SessionSaveThread("save", session, false).start();
			
			new SessionSaveThread(Strings.fixed(3), session, true).start();
			Thread.sleep(1000L);
		}
		
		Thread.sleep(3000L);
		Jorm.free();
		System.out.println("Hold on ...");
	}
	
	@Test
	public void getColValue() {
		session.clean(User.class);
		User u;
		for(int i = 0; i < 10; i++) {
			String sex = (i % 2 == 0 ? "男" : "女");
			u = new User(Strings.fixed(6), sex, Numbers.random(100), Strings.random(16));
			session.save(u);
		}
		
		String sql = "select id c_id, name, sex, age c_age, career c_job from t_user order by c_id desc";
		String s = (String) session.column(sql, "c_job");
		System.out.println(s);
	}
}

class SessionSaveThread extends Thread {
	
	private Session session;
	private boolean isNew;

	public SessionSaveThread() {
		super();
	}

	public SessionSaveThread(String name, Session session, boolean newSession) {
		super(name);
		this.session = session;
		this.isNew = newSession;
		if(newSession) {
			// this.session = Jorm.getSession();
			this.session = Jorm.newSession();
		}
	}

	public void run() {
		User u = null;
		try {
			for (int i = 0; i < 100; i++) {
//				if(i % 7 == 0) {
//					this.session.clean(User.class);
//				}
				String sex = (i % 2 == 0 ? "男" : "女");
				u = new User(Strings.fixed(6), sex, Numbers.random(100), Strings.random(16));
				this.session.save(u);
			}
		} finally {
			if(isNew) {
				this.session.close();
			}
		}
	}
	
}


class SessionCloseThread extends Thread {

	public SessionCloseThread() {
		super();
	}

	public SessionCloseThread(String name) {
		super(name);
	}

	public void run() {
		Session session = null;
		for (int i = 0; i < 30; i++) {
			session = Jorm.getSession();
			try {
				session.close();
			} finally {
				System.out.println("session [" + session + "] is closed.");
			}
		}
	}
	
}