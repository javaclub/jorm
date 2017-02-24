/*
 * @(#)Student.java	2011-9-15
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.mysql.manytoone;

import java.util.Date;

import com.github.javaclub.jorm.annotation.Column;
import com.github.javaclub.jorm.annotation.Entity;
import com.github.javaclub.jorm.annotation.Id;
import com.github.javaclub.jorm.annotation.ManyToOne;
import com.github.javaclub.jorm.annotation.PK;
import com.github.javaclub.jorm.annotation.constant.GenerationType;

/**
 * Student 学生
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Student.java 521 2011-10-06 12:11:32Z gerald.chen.hz@gmail.com $
 */
@Entity(table="t_onetomany_student")
@PK("sid")
public class Student {

	@Id(GenerationType.IDENTITY)
	@Column("id")
	private long sid;
	
	private String name;
	
	private Date birthday;
	
	@Column("class_id")
	private int classId;
	
	// select * from class where classId = 'id'
	@ManyToOne(selField="id", ownerField="classId")
	private Class klass;

	public Student() {
		super();
	}

	public Student(String name, Date birthday) {
		super();
		this.name = name;
		this.birthday = birthday;
	}

	public Student(String name, Date birthday, int classId) {
		super();
		this.name = name;
		this.birthday = birthday;
		this.classId = classId;
	}

	public long getSid() {
		return sid;
	}

	public void setSid(long sid) {
		this.sid = sid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public Class getKlass() {
		return klass;
	}

	public void setKlass(Class klass) {
		this.klass = klass;
	}

	public String toString() {
		return "Student [birthday=" + birthday + ", classId=" + classId
				+ ", name=" + name + ", sid=" + sid + "]";
	}
	
}
