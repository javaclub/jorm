/*
 * @(#)User2.java	2011-7-11
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.testentity;

import java.io.Serializable;

import com.github.javaclub.jorm.annotation.Entity;
import com.github.javaclub.jorm.annotation.Id;
import com.github.javaclub.jorm.annotation.PK;

/**
 * User2
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: User2.java 395 2011-09-18 11:14:21Z gerald.chen.hz $
 */
@Entity(table = "t_user2")
@PK("id")
public class User2 implements Serializable {

	/** desc */
	private static final long serialVersionUID = -4563751404953582306L;

	@Id
	private int id;
	
	private String name;
	
	public User2() {
		super();
	}
	
	public User2(String name) {
		super();
		this.name = name;
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
	
}
