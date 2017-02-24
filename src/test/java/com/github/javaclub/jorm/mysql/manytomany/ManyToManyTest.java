/*
 * @(#)ManyToManyTest.java	2011-10-1
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.mysql.manytomany;

import java.util.HashSet;
import java.util.Set;

import com.github.javaclub.jorm.DataTools;
import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.common.Numbers;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.jdbc.collection.PersistentSet;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * ManyToManyTest
 * 
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: ManyToManyTest.java 578 2011-10-10 17:14:34Z gerald.chen.hz@gmail.com $
 */
public class ManyToManyTest {

	static Session session;

	static int student_count = 30000;
	static int book_type_count = 600;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		session = Jorm.getSession();
	}

	@AfterClass
	public static void destroyAfterClass() {
		Jorm.free();
	}

	@Test
	public void save_0() {
		session.clean(Student.class);
		session.clean(Course.class);
		session.clean("t_mtm_r_couser_student");

		Course kc = new Course("淘宝技术");
		Student stu = null;
		long start1 = System.currentTimeMillis();
		for (int i = 0; i < student_count; i++) {
			stu = new Student(Strings.fixed(6), DataTools.age(118));
			kc.addStudent(stu);
		}
		System.out.println("cost1 => " + (System.currentTimeMillis() - start1));

		long start2 = System.currentTimeMillis();
		session.save(kc);
		System.out.println("cost2 => " + (System.currentTimeMillis() - start2));
		// Integer.parseInt("kkk");
	}

	@Test
	public void load_0() {
		Course kc = session.loadFirst(new SqlParams<Course>()
				.setObjectClass(Course.class));
		System.out.println(kc);
		System.out.println(kc.getStudents());
		Set<Student> set = kc.getStudents();
		int i = 0;
		if (set instanceof PersistentSet) {
			while (((PersistentSet) set).hasNext()) {
				System.out.println((++i) + " => "
						+ ((PersistentSet) set).next());
			}
		}

	}

	@Test
	public void save_1() throws Exception {
		session.clean(Student.class);
		session.clean(Course.class);
		session.clean("t_mtm_r_couser_student");

		Student s = new Student(Strings.fixed(6), DataTools.age(118));
		Course kc = null;
		long start1 = System.currentTimeMillis();
		for (int i = 0; i < 3 * 1000; i++) {
			kc = new Course(Strings.random(6));
			s.addCourse(kc);
		}
		System.out.println("cost1 => " + (System.currentTimeMillis() - start1));

		long start2 = System.currentTimeMillis();
		session.save(s);
		System.out.println("cost2 => " + (System.currentTimeMillis() - start2));
		// Integer.parseInt("kkk");
	}

	@Test
	public void doubleMany2Many() {
		session.clean(Student.class);
		session.clean(Course.class);
		session.clean("t_mtm_r_couser_student");
		
		// 初始化BookType
		Set<BookType> bookTypes = new HashSet<BookType>();
		BookType bookType = null;
		for (int i = 0; i < book_type_count; i++) {
			bookType = new BookType(Strings.random(18), Numbers.random(6));
			bookTypes.add(bookType);
		}

		Course kc = new Course("淘宝技术");
		Student stu = null;
		long start1 = System.currentTimeMillis();
		for (int i = 0; i < student_count; i++) {
			stu = new Student(Strings.fixed(6), DataTools.age(118));
			stu.setBookTypes(bookTypes);
			kc.addStudent(stu);
		}
		System.out.println("cost1 => " + (System.currentTimeMillis() - start1));

		long start2 = System.currentTimeMillis();
		session.save(kc);
		System.out.println("cost2 => " + (System.currentTimeMillis() - start2));
	}

}
