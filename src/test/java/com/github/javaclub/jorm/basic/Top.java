/*
 * @(#)Top.java	2011-8-31
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.basic;

import com.github.javaclub.jorm.jdbc.sql.SqlParams;

/**
 * Top
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Top.java 2011-8-31 下午02:05:06 Exp $
 */
public abstract class Top {
	
	protected Top() {
		super();
	}

	public boolean supportLimit() {
		return false;
	}
	
	public <T> String generate(SqlParams<T> params) {
		return params.getSql(false);
	}
}
