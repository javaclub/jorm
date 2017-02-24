/*
 * @(#)OneToManyTest.java	2011-9-15
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.mysql.manytoone;


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
 * @version $Id: ManyToOneTest.java 542 2011-10-09 07:10:38Z gerald.chen.hz $
 */
public class ManyToOneTest {

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
	public void save_student_0() {
		session.clean(Class.class);
		session.clean(Student.class);

		Class bj = new Class("科技联盟");
		Student stu = null;
		long start1 = System.currentTimeMillis();
		for (int i = 0; i < 3 * 1000; i++) {
			stu = new Student(Strings.fixed(6), DateTime.randomDate(
					"1982-01-01", "1989-12-31"));
			stu.setClassId(1);
			stu.setKlass(bj);
			session.save(stu);
		}
		System.out.println("cost1 => " + (System.currentTimeMillis() - start1));

	}

	@Test
	public void loadOneStudent() {
		Student student = session.loadFirst(new SqlParams<Student>().setObjectClass(Student.class).setLoadAssociated(true));
		System.out.println(student);
		System.out.println(student.getKlass());
		System.out.println(student.getKlass().getStudents());
	}
	
	@Test
	public void loadOneClass() {
		Class klass = session.read(Class.class, 1);
		System.out.println(klass);
		System.out.println(klass.getStudents());
		
		PersistentCollection collection = null;
		if(klass.getStudents() instanceof PersistentCollection) {
			collection = (PersistentCollection) klass.getStudents();
			while(collection.hasNext()) {
				System.out.println(collection.next());
			}
		}
	}
	
}
