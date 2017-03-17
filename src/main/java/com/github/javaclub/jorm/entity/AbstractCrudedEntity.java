/*
 * @(#)AbstractCrudedEntity.java	2011-10-10
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.entity;

import java.util.Date;

import com.github.javaclub.jorm.annotation.Column;

/**
 * This class is abstract for common POJOs, it provides CRUD items such as 
 * createUser,updateUser,createTime,updateTime.
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: AbstractCrudedEntity.java 2011-10-10 17:02:31 Exp $
 */
public abstract class AbstractCrudedEntity {

	@Column("create_user")
	private String createUser;
	
	@Column("update_user")
	private String updateUser;
	
	@Column("create_time")
	private Date createTime;
	
	@Column("update_time")
	private Date updateTime;

	public final String getCreateUser() {
		return createUser;
	}

	public final void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public final String getUpdateUser() {
		return updateUser;
	}

	public final void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public final Date getCreateTime() {
		return createTime;
	}

	public final void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public final Date getUpdateTime() {
		return updateTime;
	}

	public final void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	
}
