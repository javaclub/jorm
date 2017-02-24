/*
 * @(#)AssociatedWork.java	2011-9-15
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.work;

import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.jdbc.JdbcException;

/**
 * AssociatedWork
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: AssociatedWork.java 2011-9-15 下午03:49:40 Exp $
 */
public interface StepWork {

	public boolean beforeWork(Session session) throws JdbcException;
	
	public boolean doWork(Session session) throws JdbcException;
	
	public boolean finalWork(Session session) throws JdbcException;
	
	public void execute(Session session) throws JdbcException;
}
