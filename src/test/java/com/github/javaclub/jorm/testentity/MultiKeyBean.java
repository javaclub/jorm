/*
 * @(#)MultiKeyBean.java	2011-7-13
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.testentity;

import java.io.Serializable;
import java.util.List;

import com.github.javaclub.jorm.annotation.Column;
import com.github.javaclub.jorm.annotation.Entity;
import com.github.javaclub.jorm.annotation.Id;
import com.github.javaclub.jorm.annotation.PK;
import com.github.javaclub.jorm.annotation.Relation;
import com.github.javaclub.jorm.annotation.constant.RelationType;
import com.github.javaclub.jorm.jdbc.relation.RelationHelper;

/**
 * MultiKeyBean
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: MultiKeyBean.java 395 2011-09-18 11:14:21Z gerald.chen.hz $
 */
@Entity(table = "t_multi_key")
@PK("id")
@Relation(relation = RelationType.OneToMany, thisField = "name", thatField = "type", thatClass = MultiKeySubBean.class)
public class MultiKeyBean implements Serializable {

	/** desc */
	private static final long serialVersionUID = 2919485527295425179L;
	
	@Id
	@Column("pk_id")
	private long id;
	
	@Column("pk_name")
	private String name;
	
	private String note;

	public MultiKeyBean() {
		super();
	}

	public MultiKeyBean(String name, String note) {
		super();
		this.name = name;
		this.note = note;
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	public List<MultiKeySubBean> getSubBeans(int start, int limit) {
		return RelationHelper.many(this, start, limit);
	}

	public String toString() {
		return "MultiKeyBean [id=" + id + ", name=" + name + ", note=" + note
				+ "]";
	}

}
