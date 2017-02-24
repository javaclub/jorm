/*
 * @(#)Computer.java	2011-8-2
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.testentity;

import com.github.javaclub.jorm.annotation.Column;
import com.github.javaclub.jorm.annotation.Entity;
import com.github.javaclub.jorm.annotation.Id;
import com.github.javaclub.jorm.annotation.PK;
import com.github.javaclub.jorm.annotation.Relation;
import com.github.javaclub.jorm.annotation.Relations;
import com.github.javaclub.jorm.annotation.constant.RelationType;
import com.github.javaclub.jorm.jdbc.relation.RelatedModel;

/**
 * Computer
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Computer.java 2011-8-2 下午04:08:15 Exp $
 */
@Entity(table = "t_m_computer")
@PK("id")
@Relations({
	@Relation(relation=RelationType.ManyToOne, thisField="thread", thatField="threadId", thatClass=Threads.class),
	@Relation(relation=RelationType.OneToMany, thisField="id", thatField="pcId", thatClass=Item.class)
})
public class Computer extends RelatedModel {

	@Id
	private long id;

	private String thread;
	
	@Column("product_no")
	private String productNo;

	public Computer() {
	}

	public Computer(String thread, String productNo) {
		this.thread = thread;
		this.productNo = productNo;
	}

	protected Object self() {
		return this;
	}

	public String getThread() {
		return thread;
	}

	public void setThread(String thread) {
		this.thread = thread;
	}

	public String getProductNo() {
		return productNo;
	}

	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Computer [id=" + id + ", productNo=" + productNo + ", thread="
				+ thread + "]";
	}
	
}
