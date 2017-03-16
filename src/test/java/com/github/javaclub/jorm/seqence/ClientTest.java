/*
 * @(#)JsessionSpringTest.java	May 19, 2010
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.seqence;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.jdbc.DBUtil;

import junit.framework.Assert;

/**
 * 测试将自己定义的Session和datasource注入在spring中
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: JsessionSpringTest.java 1319 2012-01-10 12:47:26Z gerald.chen.hz $
 */
public class ClientTest {
	
	static Sequence userSequence;
	static ApplicationContext context;
	
	static ConcurrentMap<Long, String> db;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		context = new ClassPathXmlApplicationContext("conf/context/sequence.context.xml");
		userSequence = (Sequence) context.getBean("userSequence");
		db = new ConcurrentHashMap<Long, String>();
	}
	
	@AfterClass
	public static void destroy() throws Exception {
		Thread.sleep(30000L);
	}
	
	@Test
	public void testDataSource() throws Exception {
		DataSource ds = (DataSource) context.getBean("dataSource");
		System.out.println(DBUtil.getDatabaseInfo(ds.getConnection()));
	}

	@Test
	public void testSimpleGetId() throws Exception {
		for (int i = 0; i < 10000; i++) {
			System.out.println(userSequence.nextValue());
		}
	}
	
	@Test
	public void testConcurrencyGetId() throws Exception {
		
		for (int i = 0; i < 500; i++) {
			// 建立100个线程
			new IdGetThread(Strings.fixed(3), userSequence, db).start();
		}
				
		System.out.println("Hold on ...");
	}
	
}

class IdGetThread extends Thread {
	
	static Log log = LogFactory.getLog(IdGetThread.class);
	
	private ConcurrentMap<Long, String> db;
	
	private Sequence seq;

	public IdGetThread() {
		super();
	}

	public IdGetThread(String name, Sequence seq, ConcurrentMap<Long, String> db) {
		super(name);
		this.seq = seq;
		this.db = db;
	}

	public void run() {
		
		for (int i = 0; i < 2000000; i++) {
			try {
				long id = this.seq.nextValue();
				if(db.containsKey(Long.valueOf(id))) {
					log.error("Failed => Dumplicated Id => " + id);
					Assert.fail("Failed => Dumplicated Id => " + id);
				} else {
					db.put(Long.valueOf(id), String.valueOf(id));
					System.out.println(id);
				}
				
			} catch (SequenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	
}
