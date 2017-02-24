/*
 * @(#)MultiKeyBeanSub.java	2011-7-13
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.testentity;

import java.io.Serializable;

import com.github.javaclub.jorm.annotation.Entity;
import com.github.javaclub.jorm.annotation.Id;
import com.github.javaclub.jorm.annotation.PK;
import com.github.javaclub.jorm.annotation.Relation;
import com.github.javaclub.jorm.annotation.constant.RelationType;
import com.github.javaclub.jorm.jdbc.relation.RelationHelper;

/**
 * MultiKeyBeanSub
 * 
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: MultiKeySubBean.java 395 2011-09-18 11:14:21Z gerald.chen.hz $
 */
@Entity(table = "t_multi_key_sub")
@PK("id")
@Relation(relation = RelationType.ManyToOne, thisField = "type", thatField = "name", thatClass = MultiKeyBean.class)
public class MultiKeySubBean implements Serializable {

	/** desc */
	private static final long serialVersionUID = -3544308993794712573L;

	@Id
	long id;

	String type;

	String demo;

	public MultiKeySubBean() {
		super();
	}

	public MultiKeySubBean(String type, String demo) {
		super();
		this.type = type;
		this.demo = demo;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDemo() {
		return demo;
	}

	public void setDemo(String demo) {
		this.demo = demo;
	}

	public MultiKeyBean getParent() {
		return (MultiKeyBean) RelationHelper.one(this);
	}

	public String toString() {
		return "MultiKeySubBean [demo=" + demo + ", id=" + id + ", type="
				+ type + "]";
	}
	
	
}
