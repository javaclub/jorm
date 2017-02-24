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
import com.github.javaclub.jorm.beans.id.IdGuid;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.common.UuidUtil;
import com.github.javaclub.jorm.jdbc.criterion.Order;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * IdIncrementTest
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: IdGuidTest.java 276 2011-08-31 11:24:54Z gerald.chen.hz $
 */
public class IdGuidTest {

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
		session.clean(IdGuid.class);
		IdGuid u;
		for(int i = 0; i < 100; i++) {
			u = new IdGuid(Strings.fixed(6));
			session.save(u);
		}
		
	}
	
	@Test
	public void update() {
		session.clean(IdGuid.class);
		IdGuid IdGuid = new IdGuid(Strings.fixed(6));
		System.out.println(session.save(IdGuid));
		
		IdGuid u = session.loadFirst(IdGuid.class, "SELECT * FROM t_id_guid");
		u.setName("driver");
		
		session.update(u);
		IdGuid usr = session.loadFirst(IdGuid.class, "SELECT * FROM t_id_guid");
		System.out.println(usr);
	}
	
	@Test
	public void delete_obj() {
		session.clean(IdGuid.class);
		IdGuid IdGuid = new IdGuid("myname");
		System.out.println(session.save(IdGuid));
		
		IdGuid u = session.loadFirst(IdGuid.class, "SELECT * FROM t_id_guid");
		session.delete(u);
		System.out.println(session.loadFirst(IdGuid.class, "SELECT * FROM t_id_guid"));
	}
	
	@Test
	public void delete_sql_1() {
		session.clean(IdGuid.class);
		IdGuid u;
		for(int i = 0; i < 1000; i++) {
			u = new IdGuid(Strings.fixed(6));
			session.save(u);
		}
		
		int count = session.delete("DELETE FROM t_id_guid");
		System.out.println(count);
	}
	
	@Test
	public void delete_sql_2() {
		session.clean(IdGuid.class);
		IdGuid u;
		for(int i = 0; i < 1000; i++) {
			u = new IdGuid(Strings.fixed(6));
			session.save(u);
		}
		int count = session.delete(IdGuid.class, "name like '%a%'");
		System.out.println(count);
	}
	
	@Test
	public void load_first() {
		session.clean(IdGuid.class);
		IdGuid u;
		for(int i = 0; i < 1000; i++) {
			u = new IdGuid(Strings.fixed(6));
			session.save(u);
		}
		IdGuid IdGuid = session.loadFirst(IdGuid.class, "SELECT * FROM t_id_guid order by name desc");
		System.out.println(IdGuid);
	}
	
	@Test
	public void list_1() {
		session.clean(IdGuid.class);
		IdGuid u;
		for(int i = 0; i < 1000; i++) {
			u = new IdGuid(Strings.fixed(6));
			session.save(u);
		}
		List<IdGuid> IdGuids = session.list(new SqlParams<IdGuid>("SELECT * FROM t_id_guid WHERE name like ?", new Object[] {"%a%"}).setObjectClass(IdGuid.class));
		System.out.println(IdGuids.size());
	}
	
	@Test
	public void page_0() {
		SqlParams<IdGuid> params = new SqlParams<IdGuid>("SELECT * FROM t_user where id > 6");
		params.setObjectClass(IdGuid.class);
		params.setFirstResult(3);
		params.setMaxResults(6);
		params.addOrder(Order.desc("id"));
		List<IdGuid> page = session.list(params);
		for (IdGuid user : page) {
			System.out.println(user);
		}
	}
	
	@Test
	public void batch_insert_1() {
		session.clean(IdGuid.class);
		String sql = "INSERT INTO t_id_guid(name, id) VALUES(?, ?)";
		List<Object[]> datalist = new ArrayList<Object[]>();
		for (int i = 0; i < 1000; i++) {
			datalist.add(new Object[] {Strings.fixed(6), UuidUtil.newUUID()});
		}
		session.batchInsert(sql, datalist);
	}
	
	@Test
	public void tx_1() {
		session.clean(IdGuid.class);
		IdGuid u;
		session.beginTransaction();
		try {
			for(int i = 0; i < 1000; i++) {
				u = new IdGuid(Strings.fixed(6));
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
}
