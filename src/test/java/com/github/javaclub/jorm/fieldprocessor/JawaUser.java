/*
 * @(#)JawaUser.java	2011-9-1
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.fieldprocessor;

import java.io.Serializable;
import java.util.Arrays;

import com.github.javaclub.jorm.annotation.Basic;
import com.github.javaclub.jorm.annotation.Entity;
import com.github.javaclub.jorm.annotation.Id;
import com.github.javaclub.jorm.annotation.NoColumn;
import com.github.javaclub.jorm.annotation.PK;

/**
 * JawaUser
 * 
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: JawaUser.java 395 2011-09-18 11:14:21Z gerald.chen.hz $
 */
@Entity(table = "t_user")
@PK(value = "id")
public class JawaUser implements Serializable {

	/** desc */
	private static final long serialVersionUID = -4750351638245912867L;

	@Id
	private int id;

	private String name;

	private String sex;

	private Integer age;

	@Basic(processor=DefinedFieldProcessor.class)
	private String[] career;

	@NoColumn
	private int kvalue;

	public JawaUser() {
		super();
	}

	public JawaUser(String name, String sex, Integer age, String[] career) {
		super();
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

	public String[] getCareer() {
		return career;
	}

	public void setCareer(String[] career) {
		this.career = career;
	}

	public int getKvalue() {
		return kvalue;
	}

	public void setKvalue(int kvalue) {
		this.kvalue = kvalue;
	}

	public String toString() {
		return "JawaUser [age=" + age + ", career=" + Arrays.toString(career)
				+ ", id=" + id + ", kvalue=" + kvalue + ", name=" + name
				+ ", sex=" + sex + "]";
	}

}
