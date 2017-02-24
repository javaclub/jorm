/*
 * @(#)AbstractStepWork.java	2011-9-15
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.work;

import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.jdbc.JdbcException;

/**
 * AbstractStepWork
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: AbstractStepWork.java 2011-9-15 下午04:26:21 Exp $
 */
public abstract class AbstractStepWork implements StepWork {
	
	private final Object lock = new Object();

	protected AbstractStepWork() {
		super();
	}

	public void execute(Session session) throws JdbcException {
		synchronized (this.lock) {
			if(beforeWork(session)) {
				doWork(session);
			}
			finalWork(session);
		}
	}

}
