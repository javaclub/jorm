/*
 * @(#)FieldProcessorTest.java	2011-9-1
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.fieldprocessor;

import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.common.Numbers;
import com.github.javaclub.jorm.common.Strings;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * FieldProcessorTest
 * 
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: FieldProcessorTest.java 287 2011-09-01 15:23:35Z gerald.chen.hz@gmail.com $
 */
public class FieldProcessorTest {

	static Session session;

	@BeforeClass
	public static void setUpBeforeClass() {
		session = Jorm.getSession();
	}

	@AfterClass
	public static void destroy() {
		Jorm.free();
	}

	@Test
	public void test_save() {

		session.clean(JawaUser.class);
		JawaUser u;
		for (int i = 0; i < 100; i++) {
			String sex = (i % 2 == 0 ? "男" : "女");
			String[] cr = {};
			if(i % 3 == 0) {
				cr = new String[] {Strings.fixed(2), Strings.random(5), Strings.fixed(6)};
			} else if(i % 3 == 1) {
				cr = new String[] {Strings.fixed(2), Strings.random(5)};
			} else {
				cr = new String[] {Strings.fixed(2)};
			}
			u = new JawaUser(Strings.fixed(6), sex, Numbers.random(100), cr);
			session.save(u);
		}

		for (int i = 0; i < 10; i++) {
			u = session.read(JawaUser.class, i + 1);
			System.out.println(u);
		}
	}
}
