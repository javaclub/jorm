/*
 * @(#)Student.java	2011-9-19
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.mysql.manytomany;

import java.util.HashSet;
import java.util.Set;

import com.github.javaclub.jorm.annotation.Entity;
import com.github.javaclub.jorm.annotation.Id;
import com.github.javaclub.jorm.annotation.ManyToMany;
import com.github.javaclub.jorm.annotation.PK;

/**
 * Student
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Student.java 2011-9-19 下午07:25:09 Exp $
 */
@Entity(table="t_mtm_student")
@PK("id")
public class Student {

	@Id
	private long id;
	
	private String name;
	
	private int age;
	
	@ManyToMany(table="t_mtm_r_couser_student", mappedId="id", type=Course.class)
	private Set<Course> courses = new HashSet<Course>();
	
	@ManyToMany(mappedId="id", type=BookType.class)
	private Set<BookType> bookTypes = new HashSet<BookType>();

	public Student() {
		super();
	}

	public Student(String name, int age) {
		super();
		this.name = name;
		this.age = age;
	}

	public final long getId() {
		return id;
	}

	public final void setId(long id) {
		this.id = id;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final int getAge() {
		return age;
	}

	public final void setAge(int age) {
		this.age = age;
	}
	
	public Student addCourse(Course course) {
		this.courses.add(course);
		return this;
	}

	public final Set<Course> getCourses() {
		return courses;
	}

	public final void setCourses(Set<Course> courses) {
		this.courses = courses;
	}

	public Set<BookType> getBookTypes() {
		return bookTypes;
	}

	public void setBookTypes(Set<BookType> bookTypes) {
		this.bookTypes = bookTypes;
	}
}
