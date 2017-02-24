/*
 * @(#)JdbcSessionTest.java	May 19, 2010
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm;

import org.junit.Test;

/**
 * desc
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: JdbcSessionTest.java 85 2011-07-09 10:49:54Z gerald.chen.hz@gmail.com $
 */
public class JdbcSessionTest {

	/**
	 * Debug的方式运行
	 *
	 */
	@Test
	public void doTest() {
		for (int i = 0; i < 10; i++) {
			new Thread(new SessionThread(i + "")).start();
		}
		System.out.println("== end ==");
	}
}

class SessionThread implements Runnable {
	
	private String name;
	
	public SessionThread() {
		super();
	}
	
	public SessionThread(String name) {
		super();
		this.name = name;
	}

	public void run() {
		System.out.println("Thread-" + name + " begin to get JdbcSession...");
		Session session = Jorm.getSession();
		System.out.println("Thread-" + name + ": " + session);
	}
	
}