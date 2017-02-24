/*
 * @(#)UserEntity.java	2010-5-8
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.testentity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;

import com.github.javaclub.jorm.annotation.Column;
import com.github.javaclub.jorm.annotation.Entity;
import com.github.javaclub.jorm.annotation.NoColumn;
import com.github.javaclub.jorm.annotation.PK;

/**
 * desc
 * 
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: UserEntity.java 395 2011-09-18 11:14:21Z gerald.chen.hz $
 */
@Entity(table = "t_user_entity")
@PK(value = "id")
public class UserEntity extends BasePOJO implements Serializable {

	/** desc */
	private static final long serialVersionUID = -206780523066594L;

	@Column("user_name")
	private String username;
	private String password;
	private int age;
	private boolean isAdult;
	@NoColumn
	private User user;

	public UserEntity() {
		super();
	}

	public UserEntity(String username, int age) {
		super();
		this.username = username;
		this.age = age;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public boolean isAdult() {
		return isAdult;
	}

	public void setAdult(boolean isAdult) {
		this.isAdult = isAdult;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "UserEntity [age=" + age + ", isAdult=" + isAdult
				+ ", password=" + password + ", user=" + user + ", username="
				+ username + "]";
	}

	public static void main(String[] args) {
		Field[] fields = UserEntity.class.getDeclaredFields();
		System.out.println(Arrays.asList(fields));
	}
}
