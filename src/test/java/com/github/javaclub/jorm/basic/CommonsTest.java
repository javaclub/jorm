/*
 * @(#)CommonsTest.java	2012-1-7
 *
 * Copyright (c) 2012. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.basic;

import java.net.URL;

import org.junit.Test;

/**
 * CommonsTest
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: CommonsTest.java 2012-1-7 下午10:22:08 Exp $
 */
public class CommonsTest {

	@Test
	public void getClasspath() {
		Object obj = new Object();
		URL url = obj.getClass().getResource("");
		System.out.println(url);
		
		url = obj.getClass().getResource("/");
		System.out.println(url);
	}
}
