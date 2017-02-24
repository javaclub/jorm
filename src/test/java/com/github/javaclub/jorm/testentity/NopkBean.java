/*
 * @(#)NopkBean.java	2011-7-21
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.testentity;

import java.io.Serializable;

import com.github.javaclub.jorm.annotation.Entity;

/**
 * NopkBean
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: NopkBean.java 2011-7-21 下午09:52:00 Exp $
 */
@Entity(table = "t_no_pk_bean")
public class NopkBean implements Serializable {

	/** desc */
	private static final long serialVersionUID = 627202906490253795L;
	
	private long id;
	private String name;
	private String job;
	
	public NopkBean() {
		super();
	}

	public NopkBean(long id, String name, String job) {
		super();
		this.id = id;
		this.name = name;
		this.job = job;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	@Override
	public String toString() {
		return "NopkBean [id=" + id + ", job=" + job + ", name=" + name + "]";
	}
	
}
