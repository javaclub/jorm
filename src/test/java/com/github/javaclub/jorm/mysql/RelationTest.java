/*
 * @(#)RelationTest.java	2011-8-2
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.mysql;

import java.util.ArrayList;
import java.util.List;

import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.common.Numbers;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.testentity.Computer;
import com.github.javaclub.jorm.testentity.Item;
import com.github.javaclub.jorm.testentity.Threads;
import org.junit.Before;
import org.junit.Test;

/**
 * RelationTest
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: RelationTest.java 2011-8-2 下午04:06:58 Exp $
 */
public class RelationTest {
	
	private static final int ITEM_COUNT = 200;
	private static final int COMPUTER_COUNT = 20;
	private static final int THREADS_COUNT = 10;
	static Session jsession;

	@Before
	public void before() {
		jsession = Jorm.getSession();
		long c1 = jsession.count(Item.class);
		long c2 = jsession.count(Computer.class);
		long c3 = jsession.count(Threads.class);
		if(c1 == 0 || c2 == 0 || c3 == 0) {
			init();
		}
	}
	
	@Test
	public void testGetOne_1() {
		Item item = null;
		for (int i = 0; i < ITEM_COUNT; i++) {
			item = jsession.read(Item.class, i + 1);
			System.out.println(item.parent(Threads.class));
			System.out.println(item.parent(Computer.class));
		}
		
	}
	
	@Test
	public void testGetMany_1() {
		Computer computer = null;
		for (int i = 0; i < COMPUTER_COUNT; i++) {
			computer = jsession.read(Computer.class, i + 1);
			System.out.println(computer.parent(Threads.class));
			System.out.println(computer.child(Item.class));
		}
		
	}
	
	@Test
	public void testGetMany_2() {
		List<Threads> list = jsession.list(Threads.class, "SELECT * from t_m_Threads");
		for (Threads threads : list) {
			System.out.println(threads.child(Item.class));
			System.out.println(threads.child(Computer.class));
		}
		
	}

	protected void init() {
		clearData();
		List<Threads> threadslist = new ArrayList<Threads>();
		Threads threads = null;
		for (int i = 0; i < THREADS_COUNT; i++) {
			threads = new Threads(Strings.fixed(8));
			jsession.save(threads);
			threadslist.add(threads);
		}
		
		List<Computer> computerlist = new ArrayList<Computer>();
		Computer computer = null;
		for (int i = 0; i < COMPUTER_COUNT; i++) {
			computer = new Computer(threadslist.get(
					Numbers.random(THREADS_COUNT)).getThreadId(), Strings
					.fixed(3)
					+ "-" + Strings.fixed(8));
			jsession.save(computer);
			computerlist.add(computer);
		}
		
		Item item = null;
		for (int i = 0; i < ITEM_COUNT; i++) {
			item = new Item(Strings.random(3, 10));
			item.setPcId(Numbers.random(COMPUTER_COUNT));
			item.setThread(threadslist.get(
					Numbers.random(THREADS_COUNT)).getThreadId());
			jsession.save(item);
		}
	}

	protected void clearData() {
		jsession.clean(Item.class);
		jsession.clean(Computer.class);
		jsession.clean(Threads.class);
	}
}
