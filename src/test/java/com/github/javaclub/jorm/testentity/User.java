/*
 * @(#)Person.java	Apr 25, 2009
 *
 * Copyright (c) 2009 by jadmin. All Rights Reserved.
 */

package com.github.javaclub.jorm.testentity;

import java.io.Serializable;

import com.github.javaclub.jorm.annotation.Entity;
import com.github.javaclub.jorm.annotation.Id;
import com.github.javaclub.jorm.annotation.NoColumn;
import com.github.javaclub.jorm.annotation.PK;

/**
 * User Bean
 * 
 * @author <a href="mailto:jadmin@163.com">jadmin</a>
 * @version $Revision: 1.1 $
 */
@PK(value = "id")
@Entity(table="t_user", lazy = true)
public class User implements Serializable {

	/** desc */
	private static final long serialVersionUID = -4750351638245912867L;
	
	@Id
	private int id;

	private String name;

	private String sex;

	private Integer age;

	private String career;
	
	@NoColumn
	private int kvalue;
	
	public User() {
		super();
	}

	public User(String name, String sex, Integer age, String career) {
		super();
		this.name = name;
		this.sex = sex;
		this.age = age;
		this.career = career;
	}

	public User(Integer id, String name, String sex, Integer age, String career) {
		super();
		this.id = id;
		this.name = name;
		this.sex = sex;
		this.age = age;
		this.career = career;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getCareer() {
		return career;
	}

	public void setCareer(String career) {
		this.career = career;
	}

	public int getKvalue() {
		return kvalue;
	}

	public void setKvalue(int kvalue) {
		this.kvalue = kvalue;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[" + id + ", " + name + ", " + sex + ", " + age + ", " + career + "]");
		return sb.toString();
	}
	
	public static void main(String[] args) {
		User user = new User();
		System.out.println(User.class.equals(user.getClass()));
	}

}
