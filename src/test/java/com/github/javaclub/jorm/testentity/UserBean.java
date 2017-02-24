/*
 * @(#)UserBean.java	Apr 25, 2009
 *
 * Copyright (c) 2009 by jadmin. All Rights Reserved.
 */

package com.github.javaclub.jorm.testentity;

import java.io.Serializable;

import com.github.javaclub.jorm.annotation.Column;
import com.github.javaclub.jorm.annotation.Entity;
import com.github.javaclub.jorm.annotation.Id;
import com.github.javaclub.jorm.annotation.PK;
import com.github.javaclub.jorm.annotation.constant.GenerationType;

/**
 * User Bean
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: UserBean.java 395 2011-09-18 11:14:21Z gerald.chen.hz $
 */
@Entity(table = "t_user_bean")
@PK({"id","name"})
public class UserBean implements Serializable {

	/** desc */
	private static final long serialVersionUID = -4750351638245912867L;
	
	@Id(GenerationType.UUID)
	@Column("user_id")
	private String id;

	@Column("user_name")
	private String name;

	private String sex;

	@Column("u_age")
	private Integer age; 

	@Column("u_zhiye")
	private String career;
	
	public UserBean() {
		super();
	}

	public UserBean(String name, String sex, Integer age, String career) {
		super();
		this.name = name;
		this.sex = sex;
		this.age = age;
		this.career = career;
	}

	public UserBean(String id, String name, String sex, Integer age, String career) {
		super();
		this.id = id;
		this.name = name;
		this.sex = sex;
		this.age = age;
		this.career = career;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[" + id + ", " + name + ", " + sex + ", " + age + ", " + career + "]");
		return sb.toString();
	}

}
