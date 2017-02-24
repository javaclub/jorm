/*
 * @(#)AbstractBatcher.java	2011-8-21
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.javaclub.jorm.Batcher;
import com.github.javaclub.jorm.config.ConstantElement;
import com.github.javaclub.jorm.config.JdbcConfigXmlParser;
import com.github.javaclub.jorm.jdbc.JdbcException;

/**
 * AbstractBatcher
 * 
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: AbstractBatcher.java 2011-8-21 下午05:24:45 Exp $
 */
public abstract class AbstractBatcher implements Batcher {

	private int batchSize;

	private boolean autoExecute = true;

	private static int DEFAULT_JDBC_BATCH_SIZE = 100;
	
	protected static final Log LOG = LogFactory.getLog(AbstractBatcher.class);
	
	static {
		ConstantElement ce = JdbcConfigXmlParser.constant("jdbc.batch_size");
		DEFAULT_JDBC_BATCH_SIZE = ce == null ? 58 : ce.intValue();
	}

	protected boolean checkAndAutoExecuteBatch(int objectsCountInBatch)
			throws JdbcException {
		// exceeds the batchSize
		if (autoExecute) {
			int size = batchSize > 0 ? batchSize : DEFAULT_JDBC_BATCH_SIZE;

			if (objectsCountInBatch >= size) {
				this.executeBatch();
				this.clearBatch();
				return true;
			}
		}
		return false;
	}

	public final int getBatchSize() {
		return batchSize;
	}

	public final boolean isAutoExecute() {
		return autoExecute;
	}

	public final void setAutoExecute(boolean auto) {
		this.autoExecute = auto;
	}

	public final void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

}
