/*
 * @(#)OneToManyTest.java	2011-9-15
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.mysql.onetomany;

import java.util.Collection;
import java.util.List;

import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.common.DateTime;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.jdbc.collection.PersistentCollection;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * OneToManyTest
 * 
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: OneToManyTest.java 2011-9-15 上午11:01:24 Exp $
 */
public class OneToManyTest {

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
	public void save_class_0() {
		session.clean(Class.class);
		session.clean(Student.class);

		Class bj = new Class("科技联盟");
		Student stu = null;
		long start1 = System.currentTimeMillis();
		for (int i = 0; i < 3 * 100; i++) {
			stu = new Student(Strings.fixed(6), DateTime.randomDate(
					"1982-01-01", "1989-12-31"));
			bj.addStudent(stu);
		}
		System.out.println("cost1 => " + (System.currentTimeMillis() - start1));

		long start2 = System.currentTimeMillis();
		session.save(bj);
		System.out.println("cost2 => " + (System.currentTimeMillis() - start2));
		// Integer.parseInt("kkk");
	}

	@Test
	public void presentAllMany() {
		Class calzz = session.read(Class.class, 1);
		List<Student> list = calzz.getStudents();
		if(list instanceof PersistentCollection) {
			Collection<Student> cc = ((PersistentCollection) list).presentAll();
			System.out.println(cc.size());
		}
		//System.out.println(list);
	}
	
	@Test
	public void loadPersistentList() {
		Class calzz = session.read(Class.class, 1);
		List<Student> list = calzz.getStudents();
		System.out.println(list);
		if(list instanceof PersistentCollection) {
			PersistentCollection pc = (PersistentCollection) list;
			while(pc.hasNext()) {
				System.out.println(pc.next());
			}
		}
	}
	
	@Test
	public void loadOneStudent() {
		Student student = session.loadFirst(new SqlParams<Student>().setObjectClass(Student.class));
		System.out.println(student);
		System.out.println(student.getKlass());
		System.out.println(student.getKlass().getStudents());
	}
}
