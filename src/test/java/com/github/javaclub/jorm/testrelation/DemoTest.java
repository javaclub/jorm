/*
 * @(#)DemoTest.java	2011-7-12
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.testrelation;

import java.util.List;

import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.common.DateTime;
import com.github.javaclub.jorm.common.Numbers;
import com.github.javaclub.jorm.common.Strings;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * DemoTest
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: DemoTest.java 189 2011-08-02 11:01:37Z gerald.chen.hz $
 */
public class DemoTest {

	static Session session;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		session = Jorm.getSession();
	}
	
	@Test
	public void testGetOne() {
		session.clean(Category.class);
		Category category = null;
		for(int i = 0; i < 28; i++) {
			category = new Category(Strings.fixed(8));
			category.setOrder(i);
			category.setCreateTime(DateTime.randomDate("1999-01-01", "2011-01-01"));
			session.save(category);
		}
		
		session.clean(Book.class);
		Book book = null;
		for(int i = 0; i < 1000; i++) {
			book = new Book(Strings.fixed(10));
			book.setCategoryId(Numbers.random(28));
			book.setAuthor(Strings.fixed(3));
			book.setIsbn(Strings.fixed(3) + "-" + Strings.fixed(2) + "-" + Strings.fixed(6));
			book.setPubTime(DateTime.randomDate("1999-01-01", "2008-01-01"));
			book.setCreateTime(DateTime.randomDate("1999-01-01", "2008-01-01"));
			session.save(book);
			System.out.println(book.getCategory());
		}
	}
	
	@Test
	public void testGetBook() throws Exception {
//		Book book = Book.class.newInstance();
		Book book = session.read(Book.class, "1e31f573d1aa48109a9b53c22e3acd1e");
		System.out.println(book);
	}
	
	@Test
	public void testGetMany0() {
		Category category = null;
		category = session.read(Category.class, 1);;
		// List<Book> list = category.getBooks(1, 100);
		List<Book> list = category.getAllBooks();
		for(int j = 0; j < list.size(); j++) {
			System.out.println((j + 1) + " -> " + (Book) list.get(j));
		}
	}
	
	@Test
	public void testGetMany() {
		Category category = null;
		for(int i = 0; i < 28; i++) {
			category = session.read(Category.class, i + 1);;
			List<Book> list = category.getBooks(1, 20);
			for(int j = 0; j < list.size(); j++) {
				System.out.println((j + 1) + " -> " + (Book) list.get(j));
			}
		}
	}
}
