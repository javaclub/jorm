/*
 * @(#)JsessionSpringTest.java	May 19, 2010
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.springtx;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.SpringContextTestCase;
import com.github.javaclub.jorm.SpringContextUtil;
import com.github.javaclub.jorm.jdbc.DBUtil;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

/**
 * 测试将自己定义的Session和datasource注入在spring中
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: JsessionSpringTest.java 1319 2012-01-10 12:47:26Z gerald.chen.hz $
 */
@ContextConfiguration(locations = { "/conf/context/applicationContext.xml" })
public class JsessionSpringTest extends SpringContextTestCase {

	@Test
	public void testSession() {
		Session session = SpringContextUtil.getBean("session");
		System.out.println(session.existsTable("test", "users"));
		System.out.println(session.existsTable("test", "users-1"));
	}
	
	@Test
	public void testDataSource() throws SQLException {
		DataSource ds = SpringContextUtil.getBean("dataSource");
		System.out.println(DBUtil.getDatabaseInfo(ds.getConnection()));
	}
}
