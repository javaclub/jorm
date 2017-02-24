/*
 * @(#)SpringContextTestCase.java	2010-4-6
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm;


import org.junit.Assert;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


/**
 * Spring的支持依赖注入的JUnit4集成测试基类简写.
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: SpringContextTestCase.java 85 2011-07-09 10:49:54Z gerald.chen.hz@gmail.com $
 */
public class SpringContextTestCase extends AbstractJUnit4SpringContextTests {

	/**
	 * sleep等待,单位毫秒.
	 */
	protected void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	//-- Assert 函数 --//
	protected void assertEquals(Object expected, Object actual) {
		Assert.assertEquals(expected, actual);
	}

	protected void assertEquals(String message, Object expected, Object actual) {
		Assert.assertEquals(message, expected, actual);
	}

	protected void assertTrue(boolean condition) {
		Assert.assertTrue(condition);
	}

	protected void assertTrue(String message, boolean condition) {
		Assert.assertTrue(message, condition);
	}

	protected void assertFalse(boolean condition) {
		Assert.assertFalse(condition);
	}

	protected void assertFalse(String message, boolean condition) {
		Assert.assertFalse(message, condition);
	}

	protected void assertNull(Object object) {
		Assert.assertNull(object);
	}

	protected void assertNull(String message, Object object) {
		Assert.assertNull(message, object);
	}

	protected void assertNotNull(Object object) {
		Assert.assertNotNull(object);
	}

	protected void assertNotNull(String message, Object object) {
		Assert.assertNotNull(message, object);
	}
}
