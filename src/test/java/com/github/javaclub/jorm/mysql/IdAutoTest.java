/*
 * @(#)IdIncrementTest.java	2011-8-13
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.mysql;

import java.util.ArrayList;
import java.util.List;

import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.beans.id.IdAuto;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.jdbc.criterion.Order;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * IdIncrementTest
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: IdAutoTest.java 276 2011-08-31 11:24:54Z gerald.chen.hz $
 */
public class IdAutoTest {

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
	public void save() {
		session.clean(IdAuto.class);
		IdAuto u;
		for(int i = 0; i < 100; i++) {
			u = new IdAuto(Strings.fixed(6));
			session.save(u);
		}
		
		for (int i = 0; i < 100; i++) {
			u = session.read(IdAuto.class, i + 1);
			System.out.println(u);
		}
	}
	
	@Test
	public void update() {
		session.clean(IdAuto.class);
		IdAuto user = new IdAuto(Strings.fixed(6));
		System.out.println(session.save(user));
		
		IdAuto u = session.read(IdAuto.class, 1);
		u.setName("driver");
		
		session.update(u);
		IdAuto usr = session.read(IdAuto.class, 1);
		System.out.println(usr);
	}
	
	@Test
	public void delete_sql_1() {
		session.clean(IdAuto.class);
		IdAuto u;
		for(int i = 0; i < 1000; i++) {
			u = new IdAuto(Strings.fixed(6));
			session.save(u);
		}
		
		int count = session.delete("DELETE FROM t_id_auto");
		System.out.println(count);
	}
	
	@Test
	public void delete_sql_2() {
		session.clean(IdAuto.class);
		IdAuto u;
		for(int i = 0; i < 1000; i++) {
			u = new IdAuto(Strings.fixed(6));
			session.save(u);
		}
		int count = session.delete(IdAuto.class, "id > 100");
		System.out.println(count);
	}
	
	@Test
	public void load_first() {
		session.clean(IdAuto.class);
		IdAuto u;
		for(int i = 0; i < 1000; i++) {
			u = new IdAuto(Strings.fixed(6));
			session.save(u);
		}
		IdAuto user = session.loadFirst(IdAuto.class, "(SELECT * FROM t_id_auto WHERE id > ?)", 100);
		System.out.println(user);
	}
	
	@Test
	public void list_1() {
		session.clean(IdAuto.class);
		IdAuto u;
		for(int i = 0; i < 1000; i++) {
			u = new IdAuto(Strings.fixed(6));
			session.save(u);
		}
		List<IdAuto> users = session.list(new SqlParams<IdAuto>("SELECT * FROM t_id_auto WHERE id > ?", new Object[] {100}).setObjectClass(IdAuto.class));
		System.out.println(users.size());
	}
	
	@Test
	public void page_0() {
		SqlParams<IdAuto> params = new SqlParams<IdAuto>("SELECT * FROM t_user where id > 6");
		params.setObjectClass(IdAuto.class);
		params.setFirstResult(3);
		params.setMaxResults(6);
		params.addOrder(Order.desc("id"));
		List<IdAuto> page = session.list(params);
		for (IdAuto user : page) {
			System.out.println(user);
		}
	}
	
	@Test
	public void batch_insert_1() {
		session.clean(IdAuto.class);
		String sql = "INSERT INTO t_id_auto(name, id) VALUES(?, ?)";
		List<Object[]> datalist = new ArrayList<Object[]>();
		for (int i = 0; i < 1000; i++) {
			datalist.add(new Object[] {Strings.fixed(6), (i+1)});
		}
		boolean flag = session.batchInsert(sql, datalist);
		System.out.println(flag);
	}
	
	@Test
	public void tx_1() {
		session.clean(IdAuto.class);
		IdAuto u;
		session.beginTransaction();
		try {
			for(int i = 0; i < 1000; i++) {
				u = new IdAuto(Strings.fixed(6));
				session.save(u);
				if(i == 886) {
					Integer.parseInt("kkk");
				}
			}
		} catch (Exception e) {
			session.rollback();
		} finally {
			session.endTransaction();
		}
	}
}
