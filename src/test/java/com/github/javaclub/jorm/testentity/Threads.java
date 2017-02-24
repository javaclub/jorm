/*
 * @(#)Thread.java	2011-8-2
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
 * Thread
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Thread.java 2011-8-2 下午04:26:11 Exp $
 */
@Entity(table = "t_m_Threads")
@PK("threadId")
@Relations({
	@Relation(relation=RelationType.OneToMany, thisField="threadId", thatField="thread", thatClass=Computer.class),
	@Relation(relation=RelationType.OneToMany, thisField="threadId", thatField="thread", thatClass=Item.class)
})
public class Threads extends RelatedModel {

	@Id(GenerationType.UUID)
	@Column("thread_id")
	String threadId;
	
	@Column("name")
	String threadName;

	public Threads() {
		super();
	}

	public Threads(String threadName) {
		super();
		this.threadName = threadName;
	}
	
	protected Object self() {
		return this;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	@Override
	public String toString() {
		return "Threads [threadId=" + threadId + ", threadName=" + threadName
				+ "]";
	}
	
}
