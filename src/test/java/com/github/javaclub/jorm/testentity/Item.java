/*
 * @(#)Item.java	2011-8-2
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
import com.github.javaclub.jorm.annotation.constant.GenerationType;
import com.github.javaclub.jorm.annotation.constant.RelationType;
import com.github.javaclub.jorm.jdbc.relation.RelatedModel;

/**
 * Item
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Item.java 2011-8-2 下午04:12:43 Exp $
 */
@Entity(table = "t_m_item")
@PK("id")
@Relations({
	@Relation(relation=RelationType.ManyToOne, thisField="thread", thatField="threadId", thatClass=Threads.class),
	@Relation(relation=RelationType.ManyToOne, thisField="pcId", thatField="id", thatClass=Computer.class)
})
public class Item extends RelatedModel {

	@Id(GenerationType.INCREMENT)
	@Column("item_id")
	private long id;
	
	@Column("pc_id")
	private long pcId;
	
	private String thread;
	
	@Column("pc_name")
	private String name;

	public Item() {
	}

	public Item(String name) {
		this.name = name;
	}
	
	protected Object self() {
		return this;
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

	public long getPcId() {
		return pcId;
	}

	public void setPcId(long pcId) {
		this.pcId = pcId;
	}

	public String getThread() {
		return thread;
	}

	public void setThread(String thread) {
		this.thread = thread;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", pcId=" + pcId
				+ ", thread=" + thread + "]";
	}
	
	
}
