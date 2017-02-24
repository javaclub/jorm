/*
 * @(#)Class.java	2011-9-15
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.mysql.manytoone;

import java.util.ArrayList;
import java.util.List;

import com.github.javaclub.jorm.annotation.Column;
import com.github.javaclub.jorm.annotation.Entity;
import com.github.javaclub.jorm.annotation.Id;
import com.github.javaclub.jorm.annotation.OneToMany;
import com.github.javaclub.jorm.annotation.PK;

/**
 * Class 班级
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Class.java 521 2011-10-06 12:11:32Z gerald.chen.hz@gmail.com $
 */
@Entity(table="t_onetomany_class")
@PK("id")
public class Class {

	@Id
	private int id;
	
	@Column("class_name")
	private String className;
	
	@OneToMany(type = Student.class, selField="classId", ownerField="id")
	private List<Student> students = new ArrayList<Student>();

	public Class() {
		super();
	}

	public Class(String className) {
		super();
		this.className = className;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}
	
	public Class addStudent(Student stu) {
		this.students.add(stu);
		return this;
	}

	public String toString() {
		return "Class [className=" + className + ", id=" + id + "]";
	}
	
}
