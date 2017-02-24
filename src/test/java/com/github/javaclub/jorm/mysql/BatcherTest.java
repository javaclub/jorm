/*
 * @(#)BatcherTest.java	2011-8-21
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.mysql;

import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.common.Numbers;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.jdbc.batch.JdbcBatcher;
import com.github.javaclub.jorm.testentity.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * BatcherTest
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: BatcherTest.java 2011-8-21 下午08:47:39 Exp $
 */
public class BatcherTest {

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
	public void getBatcher() {
		JdbcBatcher batcher = session.createBatcher();
		System.out.println(batcher);
	}
	
	@Test
	public void op_1() {
		
		JdbcBatcher batcher = session.createBatcher();
		batcher.addBatch("delete from t_id_auto");
		batcher.addBatch("delete from t_incre");
		batcher.addBatch("delete from t_user");
		batcher.execute();
		
		session.beginTransaction();
		long start;
		try {
			start = System.currentTimeMillis();
			String sql = "INSERT INTO t_user(sex,age,career,name,id) VALUES(?,?,?,?,?)";
			for (int i = 0; i < 100000; i++) {
				batcher.addBatch(sql, new Object[] {"男", Numbers.random(98), 
						Strings.random(10), 
						Strings.fixed(6), (i+1) });
			}
			
			String sqlx = "INSERT INTO t_id_auto(name, id) VALUES(?, ?)";
			for (int i = 0; i < 100000; i++) {
				batcher.addBatch(sqlx, new Object[] {Strings.fixed(6), (i+1)});
				if(i > 200) {
					//Integer.parseInt("kkk");
				}
			}
			batcher.execute();
			
			System.out.println(System.currentTimeMillis() - start);
		} catch (Exception e) {
			session.rollback();
		} finally {
			session.endTransaction();
		}
		
	}
	
	@Test
	public void op_save() {
		session.beginTransaction();
		session.clean(User.class);
		JdbcBatcher batcher = session.createBatcher();
		batcher.setBatchSize(500);
		
		User u;
		int times = 20 * 100;
		long start = System.currentTimeMillis();
		for(int i = 0; i < times; i++) {
			String sex = (i % 2 == 0 ? "男" : "女");
			u = new User(Strings.fixed(6), sex, Numbers.random(100), Strings.random(16));
			batcher.save(u);
		}
		batcher.execute();
		session.endTransaction();
		long cost = (System.currentTimeMillis() - start);
		System.out.println("Total：" + cost);
		System.out.println("Each：" + (float) cost / times);
		
	}
}
