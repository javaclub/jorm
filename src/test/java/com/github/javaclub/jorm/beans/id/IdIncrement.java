/*
 * @(#)IdAuto.java	2011-8-13
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.beans.id;

import com.github.javaclub.jorm.annotation.Entity;
import com.github.javaclub.jorm.annotation.Id;
import com.github.javaclub.jorm.annotation.PK;
import com.github.javaclub.jorm.annotation.constant.GenerationType;

/**
 * IdAuto
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: IdIncrement.java 395 2011-09-18 11:14:21Z gerald.chen.hz $
 */
@Entity(table = "t_id_increment")
@PK("id")
public class IdIncrement {

	@Id(value = GenerationType.INCREMENT, incrementBy=3)
	private int id;
	
	private String name;

	public IdIncrement() {
		super();
	}

	public IdIncrement(String name) {
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

	@Override
	public String toString() {
		return "IdAuto [id=" + id + ", name=" + name + "]";
	}
}
