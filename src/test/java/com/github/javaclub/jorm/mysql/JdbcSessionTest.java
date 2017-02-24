/*
 * @(#)JdbcSessionTest.java	2011-8-3
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.mysql;

import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.Session;
import org.junit.Test;

/**
 * JdbcSessionTest
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: JdbcSessionTest.java 2011-8-3 上午10:27:15 Exp $
 */
public class JdbcSessionTest {

	@Test
	public void testGetSession_default() {
		Session session = null;
		for (int i = 0; i < 200; i++) {
			session = Jorm.getSession();
			//session.close();
			System.out.println(session + " => " + session.getConnection());
		}
	}
	
	@Test
	public void testGetSession_byname() {
		Session session = null;
		for (int i = 0; i < 200; i++) {
			session = Jorm.getSession("simple");
			//session.close();
			System.out.println(session + " => " + session.getConnection());
		}
	}
	
	@Test
	public void testMultilGetSession_default() {
		
		Thread t = null;
		for (int i = 0; i < 20; i++) {
			t = new SessionGetThread("session-get-thread" + (i + 1));
			t.start();
			System.out.println(t.getName() + " start ...");
		}
		System.out.println("Holding On ...");
		
		
	}
	
	@Test
	public void testGetConnection_default() {
		for (int i = 0; i < 200; i++) {
			System.out.println("connection => " + Jorm.getConnection());
		}
	}
	
	@Test
	public void testGetConnection_byname() {
		for (int i = 0; i < 200; i++) {
			System.out.println("connection => " + Jorm.getConnection("simple"));
		}
	}
}

class SessionGetThread extends Thread {

	public SessionGetThread(String name) {
		super(name);
	}

	public void run() {
		Session session = null;
		for (int i = 0; i < 30; i++) {
			session = Jorm.getSession();
			System.out.println(session + " -> " + getName() + "\t" +
					"conn -> " + session.getConnection());
			// System.out.println(Jorm.getConnection() + " -> " + getName());
		}
	}
	
}
