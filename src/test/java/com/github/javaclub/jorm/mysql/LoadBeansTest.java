/*
 * @(#)LoadBeansTest.java	2011-8-17
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.mysql;

import java.util.List;

import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.beans.entity.UserClass;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * LoadBeansTest
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: LoadBeansTest.java 2011-8-17 下午05:43:55 Exp $
 */
public class LoadBeansTest {

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
	public void testLoadBeans() {
		String sql = "select t1.item_id id, t1.pc_id pcId, t2.product_no pcNo, t1.thread thread, t3.name threadName from t_m_item t1, t_m_computer t2, t_m_threads t3 where t1.pc_id=t2.id and t1.thread=t3.thread_id and t2.thread=t3.thread_id order by id desc";
		SqlParams<UserClass> params = new SqlParams<UserClass>(sql);
		params.setObjectClass(UserClass.class);
		List<UserClass> list = session.loadBeans(params.setMaxResults(6));
		System.out.println(list.size());
		for (UserClass userClass : list) {
			System.out.println(userClass);
		}
	}
}
