/*
 * @(#)MySQLIdIncrementBean.java	2011-7-11
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.testentity;

import java.io.Serializable;

import com.github.javaclub.jorm.annotation.Entity;
import com.github.javaclub.jorm.annotation.Id;
import com.github.javaclub.jorm.annotation.PK;
import com.github.javaclub.jorm.annotation.constant.GenerationType;

/**
 * MySQLIdIncrementBean
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: MySQLIdIncrementBean.java 395 2011-09-18 11:14:21Z gerald.chen.hz $
 */
@Entity(table = "t_incre")
@PK("id")
public class MySQLIdIncrementBean implements Serializable {

	/** desc */
	private static final long serialVersionUID = 9046276864617139284L;
	
	@Id(GenerationType.INCREMENT)
	private int id;
	
	private String name;

	public MySQLIdIncrementBean() {
		super();
	}

	public MySQLIdIncrementBean(String name) {
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
