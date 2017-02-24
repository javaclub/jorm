/*
 * @(#)JdbcBatcher.java	2011-8-21
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.batch;

import com.github.javaclub.jorm.jdbc.JdbcException;

/**
 * JdbcBatcher
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: JdbcBatcher.java 2011-8-21 下午08:32:14 Exp $
 */
public interface JdbcBatcher extends SQLBatcher {
	
	public void save(Object obj) throws JdbcException;
	
	public void update(Object obj) throws JdbcException;
	
	public void delete(Object obj) throws JdbcException;

	public void execute() throws JdbcException;
	
	public boolean hadSubmit();
}
