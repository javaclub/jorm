/*
 * @(#)DummyObject.java	2011-8-30
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.common;

/**
 * A dummy object.
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: DummyObject.java 2011-8-30 下午08:09:54 Exp $
 */
public class DummyObject {

	private String name;

	public DummyObject(String name) {
		this.name = name;
	}

	public String toString() {
		return this.name;
	}
	
}
