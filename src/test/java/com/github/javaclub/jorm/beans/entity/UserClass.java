/*
 * @(#)UserClass.java	2011-8-17
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.beans.entity;

/**
 * UserClass
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: UserClass.java 2011-8-17 下午05:43:28 Exp $
 */
public class UserClass {

	long id;
	long pcId;
	String pcNo;
	String thread;
	String threadName;
	
	public UserClass() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPcId() {
		return pcId;
	}

	public void setPcId(long pcId) {
		this.pcId = pcId;
	}

	public String getPcNo() {
		return pcNo;
	}

	public void setPcNo(String pcNo) {
		this.pcNo = pcNo;
	}

	public String getThread() {
		return thread;
	}

	public void setThread(String thread) {
		this.thread = thread;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public String toString() {
		return "UserClass [id=" + id + ", pcId=" + pcId + ", pcNo=" + pcNo
				+ ", thread=" + thread + ", threadName=" + threadName + "]";
	}
	
	public static void main(String[] args) {
		System.out.println(UserClass.class.getName());
	}
}
