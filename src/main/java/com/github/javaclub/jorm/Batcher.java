/*
 * @(#)Batcher.java	2011-8-21
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm;

import com.github.javaclub.jorm.jdbc.JdbcException;

/**
 * Executing Batch operation.
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Batcher.java 2011-8-21 下午05:05:42 Exp $
 */
public interface Batcher {
	
	public void executeBatch() throws JdbcException;

	public void clearBatch() throws JdbcException;
	
	public void setAutoExecute(boolean auto);
	
	public boolean isAutoExecute();
	
	public void setBatchSize(int batchSize);
	
	public int getBatchSize();
	
	public Session getSession();
}
