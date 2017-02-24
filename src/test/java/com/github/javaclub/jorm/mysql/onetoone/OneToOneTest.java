/*
 * @(#)OneToOneTest.java	2011-9-5
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.mysql.onetoone;

import com.github.javaclub.jorm.DataTools;
import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.common.Strings;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * OneToOneTest
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: OneToOneTest.java 2011-9-5 下午01:10:00 Exp $
 */
public class OneToOneTest {

	static Session session;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		session = Jorm.getSession();
	}
	
	@AfterClass
	public static void destroyAfterClass() {
		Jorm.free();
	}
	
	@Test
	public void get_0() {
		Person p = session.read(Person.class, "421121198508312017");
		System.out.println(p);
		Idcard card = p.getIdcard();
		System.out.println(card);
		System.out.println(card.getPerson());
	}
	
	@Test
	public void get_1() {
		Idcard card = session.read(Idcard.class, 1);
		System.out.println(card);
		Person p = card.getPerson();
		System.out.println(p);
		System.out.println(p.getIdcard());
	}
	
	@Test
	public void save_0() {
		session.delete(Idcard.class, "id > 8");
		session.delete(Person.class, "identity_number NOT IN ('421121198508312016', '421121198508312017', '321121198605282016')");
		String cnum = DataTools.numbers(18);
		Idcard card = new Idcard(cnum);
		
		Person p = new Person(Strings.fixed(3), false, Strings.fixed(6), DataTools.numbers(11));
		p.setIdcard(card);
		session.save(p);
	}
	
	@Test
	public void save_1() {
		session.delete(Idcard.class, "id > 8");
		session.delete(Person.class, "identity_number NOT IN ('421121198508312016', '421121198508312017', '321121198605282016')");
		
		Person p = new Person(Strings.random(3), true, Strings.random(6), DataTools.numbers(11));
		
		String cnum = DataTools.numbers(18);
		Idcard idcard = new Idcard(cnum);
		idcard.setPerson(p);
		session.save(idcard);
	}
	
	@Test
	public void save_2() {
		
		session.delete(Idcard.class, "id > 8");
		session.delete(Person.class, "identity_number NOT IN ('421121198508312016', '421121198508312017', '321121198605282016')");
		
		String cnum = DataTools.numbers(18);
		Idcard idcard = new Idcard(cnum);
		idcard.setPerson(null);
		session.save(idcard);
	}
	
	@Test
	public void save_3() {
		session.delete(Idcard.class, "id > 8");
		session.delete(Person.class, "identity_number NOT IN ('421121198508312016', '421121198508312017', '321121198605282016')");
		
		// 会抛出异常，因为person有依赖关联外键
		Person p = new Person(Strings.fixed(3), false, Strings.fixed(6), DataTools.numbers(11));
		p.setIdcard(null);
		session.save(p);
	}
}
