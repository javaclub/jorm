/*
 * @(#)ClassTest.java	2011-9-18
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.basic;

import com.github.javaclub.jorm.testentity.User;

/**
 * ClassTest
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: ClassTest.java 2011-9-18 下午07:05:32 Exp $
 */
public class ClassTest {

	public static void main(String[] args) {
		User user = new User();
		
		System.out.println(user.getClass().getSimpleName());
		System.out.println(user.getClass().getName());

	}

}
