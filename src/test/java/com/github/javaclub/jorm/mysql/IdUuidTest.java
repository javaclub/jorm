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
import com.github.javaclub.jorm.beans.id.IdUuid;
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
 * @version $Id: IdUuidTest.java 276 2011-08-31 11:24:54Z gerald.chen.hz $
 */
public class IdUuidTest {

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
		session.clean(IdUuid.class);
		IdUuid u;
		for(int i = 0; i < 10; i++) {
			u = new IdUuid(Strings.fixed(6));
			session.save(u);
		}
		
	}
	
	@Test
	public void update() {
		session.clean(IdUuid.class);
		IdUuid IdUuid = new IdUuid(Strings.fixed(6));
		System.out.println(session.save(IdUuid));
		
		IdUuid u = session.loadFirst(IdUuid.class, "SELECT * FROM t_id_uuid");
		u.setName("driver");
		
		session.update(u);
		IdUuid usr = session.loadFirst(IdUuid.class, "SELECT * FROM t_id_uuid");
		System.out.println(usr);
	}
	
	@Test
	public void delete_obj() {
		session.clean(IdUuid.class);
		IdUuid IdUuid = new IdUuid("myname");
		System.out.println(session.save(IdUuid));
		
		IdUuid u = session.loadFirst(IdUuid.class, "SELECT * FROM t_id_uuid");
		session.delete(u);
		System.out.println(session.loadFirst(IdUuid.class, "SELECT * FROM t_id_uuid"));
	}
	
	@Test
	public void delete_sql_1() {
		session.clean(IdUuid.class);
		IdUuid u;
		for(int i = 0; i < 1000; i++) {
			u = new IdUuid(Strings.fixed(6));
			session.save(u);
		}
		
		int count = session.delete("DELETE FROM t_id_uuid");
		System.out.println(count);
	}
	
	@Test
	public void delete_sql_2() {
		session.clean(IdUuid.class);
		IdUuid u;
		for(int i = 0; i < 1000; i++) {
			u = new IdUuid(Strings.fixed(6));
			session.save(u);
		}
		int count = session.delete(IdUuid.class, "name like '%a%'");
		System.out.println(count);
	}
	
	@Test
	public void load_first() {
		session.clean(IdUuid.class);
		IdUuid u;
		for(int i = 0; i < 1000; i++) {
			u = new IdUuid(Strings.fixed(6));
			session.save(u);
		}
		IdUuid IdUuid = session.loadFirst(IdUuid.class, "(SELECT * FROM t_id_uuid order by name desc)");
		System.out.println(IdUuid);
	}
	
	@Test
	public void list_1() {
		session.clean(IdUuid.class);
		IdUuid u;
		for(int i = 0; i < 1000; i++) {
			u = new IdUuid(Strings.fixed(6));
			session.save(u);
		}
		List<IdUuid> IdUuids = session.list(new SqlParams<IdUuid>("SELECT * FROM t_id_uuid WHERE name like ?", new Object[] {"%a%"}).setObjectClass(IdUuid.class));
		System.out.println(IdUuids.size());
	}
	
	@Test
	public void page_0() {
		SqlParams<IdUuid> params = new SqlParams<IdUuid>("SELECT * FROM t_user where id > 6");
		params.setObjectClass(IdUuid.class);
		params.setFirstResult(3);
		params.setMaxResults(6);
		params.addOrder(Order.desc("id"));
		List<IdUuid> page = session.list(params);
		for (IdUuid user : page) {
			System.out.println(user);
		}
	}
	
	@Test
	public void batch_insert_1() {
		session.clean(IdUuid.class);
		String sql = "INSERT INTO t_id_uuid(name, id) VALUES(?, ?)";
		List<Object[]> datalist = new ArrayList<Object[]>();
		for (int i = 0; i < 1000; i++) {
			datalist.add(new Object[] {Strings.fixed(6), UuidUtil.newUUID()});
		}
		session.batchInsert(sql, datalist);
	}
	
	@Test
	public void tx_1() {
		session.clean(IdUuid.class);
		IdUuid u;
		session.beginTransaction();
		try {
			for(int i = 0; i < 1000; i++) {
				u = new IdUuid(Strings.fixed(6));
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
