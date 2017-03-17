/*
 * @(#)IdIncrementDO.java	2017年3月16日
 *
 * Copyright (c) 2017. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.mysql.increment;

import com.github.javaclub.jorm.annotation.Entity;
import com.github.javaclub.jorm.annotation.Id;
import com.github.javaclub.jorm.annotation.PK;
import com.github.javaclub.jorm.annotation.constant.GenerationType;

/**
 * IdIncrementDO
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: IdIncrementDO.java 2017年3月16日 下午7:46:58 Exp $
 */
@Entity(table = "t_incre")
@PK("id")
public class IdIncrementDO {

	@Id(value=GenerationType.INCREMENT, sequenceName="mysql_incr_seq")
	private int id;
	
	private String name;

	public IdIncrementDO() {
		super();
	}

	public IdIncrementDO(String name) {
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
		return "IdIncrementDO [id=" + id + ", name=" + name + "]";
	}
	
}
