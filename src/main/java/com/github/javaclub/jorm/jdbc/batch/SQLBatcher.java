/*
 * @(#)SQLBatcher.java	2011-8-21
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.batch;

import com.github.javaclub.jorm.Batcher;
import com.github.javaclub.jorm.jdbc.JdbcException;

/**
 * SQLBatcher
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: SQLBatcher.java 2011-8-21 下午06:52:17 Exp $
 */
public interface SQLBatcher extends Batcher {

	public void addBatch(final String sql, Object... params) throws JdbcException;
}
