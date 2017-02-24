/*
 * @(#)Course.java	2011-9-19
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.mysql.manytomany;

import java.util.HashSet;
import java.util.Set;

import com.github.javaclub.jorm.annotation.Column;
import com.github.javaclub.jorm.annotation.Entity;
import com.github.javaclub.jorm.annotation.Id;
import com.github.javaclub.jorm.annotation.ManyToMany;
import com.github.javaclub.jorm.annotation.PK;
import com.github.javaclub.jorm.annotation.constant.GenerationType;

/**
 * Course
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Course.java 2011-9-19 下午07:24:58 Exp $
 */
@Entity(table="t_mtm_course")
@PK("cid")
public class Course {

	@Id(GenerationType.IDENTITY)
	@Column("id")
	private int cid;
	
	private String name;
	
	@ManyToMany(table="t_mtm_r_couser_student", mappedId="cid", type=Student.class)
	private Set<Student> students = new HashSet<Student>();
	
	public Course() {
		super();
	}

	public Course(String name) {
		super();
		this.name = name;
	}

	public final int getCid() {
		return cid;
	}

	public final void setCid(int cid) {
		this.cid = cid;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public Course addStudent(Student stu) {
		this.students.add(stu);
		return this;
	}
	
	public final Set<Student> getStudents() {
		return students;
	}

	public final void setStudents(Set<Student> students) {
		this.students = students;
	}

	public String toString() {
		return "Course [cid=" + cid + ", name=" + name + "]";
	}
	
}
