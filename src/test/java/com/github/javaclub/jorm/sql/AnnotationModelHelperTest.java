/*
 * @(#)AnnotationModelHelperTest.java	2011-7-11
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.sql;

import com.github.javaclub.jorm.jdbc.sql.AnnotationModelHelper;
import com.github.javaclub.jorm.jdbc.sql.SqlPrepared;
import com.github.javaclub.jorm.testentity.MySQLIdIncrementBean;
import com.github.javaclub.jorm.testentity.UserEntity;
import org.junit.Test;

/**
 * AnnotationModelHelperTest
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: AnnotationModelHelperTest.java 196 2011-08-04 11:42:51Z gerald.chen.hz $
 */
public class AnnotationModelHelperTest {

	@Test
	public void testInsertSql_1() {
		String sql = "";
		for (int i = 0; i < 1000000; i++) {
			sql = AnnotationModelHelper.insert(UserEntity.class);
		}
		System.out.println(sql);
	}
	
	@Test
	public void testInsertSql_2() {
		String sql = "";
		for (int i = 0; i < 1000000; i++) {
			sql = SqlPrepared.preparedInsert(UserEntity.class);
		}
		System.out.println(sql);
	}
	
	@Test
	public void testSelectSql_1() {
		String sql = "";
		for (int i = 0; i < 100; i++) {
			sql = SqlPrepared.preparedContains(new UserEntity()).getSql();
		}
		System.out.println(sql);
	}
	
	@Test
	public void testUpdateSql_1() {
		String sql = "";
		for (int i = 0; i < 1000000; i++) {
			sql = SqlPrepared.preparedUpdate(new UserEntity()).getSql();
		}
		System.out.println(sql);
	}
	
	@Test
	public void testAddSql() {
		String sql = AnnotationModelHelper.insert(MySQLIdIncrementBean.class);
		System.out.println(sql);
	}
}
