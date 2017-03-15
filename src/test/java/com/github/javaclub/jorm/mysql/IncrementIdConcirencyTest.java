/*
 * @(#)IncrementIdConcirencyTest.java	2011-8-6
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.mysql;

import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.testentity.MySQLIdIncrementBean;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * IncrementIdConcirencyTest
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: IncrementIdConcirencyTest.java 435 2011-09-22 02:47:14Z gerald.chen.hz $
 */
public class IncrementIdConcirencyTest {
	
	static Session session;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		session = Jorm.getSession();
	}
	
	@Test
	public void conncurency_1() throws InterruptedException {
		//session.clean(MySQLIdIncrementBean.class);
		// 应该生成20000条记录
		for (int i = 0; i < 100; i++) {
			// 建立10个线程
			new SaveThread(Strings.fixed(3), session, true).start();
			//Thread.sleep(1000L);
		}
		
		Thread.sleep(30000L);
		Jorm.free();
		System.out.println("Hold on ...");
	}

	@Test
	public void conncurency_2() throws InterruptedException {
		session.clean(MySQLIdIncrementBean.class);
		// 应该生成20000条记录
		for (int i = 0; i < 100; i++) {
			// 建立10个线程
			new SaveThread(Strings.fixed(3), session, true).start();
			//Thread.sleep(1000L);
		}
		
		Thread.sleep(30000L);
		Jorm.free();
		System.out.println("Hold on ...");
	}
}

class SaveThread extends Thread {
	
	private Session session;

	public SaveThread() {
		super();
	}

	public SaveThread(String name, Session session, boolean newSession) {
		super(name);
		this.session = session;
		if(newSession) {
			this.session = Jorm.newSession();
		}
	}

	public void run() {
		
		MySQLIdIncrementBean u = null;
		try {
			for (int i = 0; i < 20000; i++) {
				u = new MySQLIdIncrementBean(Strings.fixed(6));
				this.session.save(u);
			}
		} finally {
			this.session.close();
		}
	}

	
	
}
