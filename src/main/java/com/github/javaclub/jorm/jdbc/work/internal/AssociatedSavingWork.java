/*
 * @(#)AssociatedSavingWork.java	2011-9-15
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.work.internal;

import java.io.Serializable;

import com.github.javaclub.jorm.Environment;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.common.Reflections;
import com.github.javaclub.jorm.jdbc.ClassMetadata;
import com.github.javaclub.jorm.jdbc.JdbcException;
import com.github.javaclub.jorm.jdbc.batch.JdbcBatcher;
import com.github.javaclub.jorm.jdbc.work.AbstractStepWork;
import com.github.javaclub.jorm.jdbc.work.Isolater;

/**
 * AssociatedSavingWork
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: AssociatedSavingWork.java 2011-9-15 下午04:11:57 Exp $
 */
public class AssociatedSavingWork extends AbstractStepWork {

	private Object target;
	private ClassMetadata metadata;
	private Serializable identifier;
	private JdbcBatcher batcher;
	
	public AssociatedSavingWork() {
		super();
	}

	public AssociatedSavingWork(Object target, ClassMetadata metadata) {
		super();
		this.target = target;
		this.metadata = metadata;
	}

	public boolean beforeWork(Session session) throws JdbcException {
		if(session.getDialect().supportSpecifiedFeture(Environment.SUPPORT_BATCH_UPDATE)) {
			this.batcher = session.createBatcher();
		}
		// 先处理one-to-one关联对象,同时确保已经保存目标对象
		OneToOneWork work = new OneToOneWork(this.target, this.batcher, this.metadata);
		Isolater.doIsolatedWork(work, session);
		return true;
	}

	// one-to-many  many-to-one  many-to-many
	public boolean doWork(Session session) throws JdbcException {
		if(!this.metadata.OneToManyFields.isEmpty()) {
			OneToManyWork work = new OneToManyWork(this.target, this.batcher, this.metadata);
			Isolater.doIsolatedWork(work, session);
		}
		if(!this.metadata.ManyToOneFields.isEmpty()) {
			// 在ManyToOne关系中，由ManyToOneWork保存target对象
			ManyToOneWork work = new ManyToOneWork(this.target, this.batcher, this.metadata);
			Isolater.doIsolatedWork(work, session);
		}
		if(!this.metadata.ManyToManyFields.isEmpty()) {
			ManyToManyWork work = new ManyToManyWork(this.target, this.batcher, this.metadata);
			Isolater.doIsolatedWork(work, session);
		}
		return true;
	}
	
	public boolean finalWork(Session session) throws JdbcException {
		try {
			if(null != batcher) {
				batcher.execute();
			}
			this.identifier = (Serializable) Reflections.getFieldValue(target, metadata.identifierField);
			return true;
		} finally {
			metadata = null;
			batcher = null;
		}
	}

	public final Serializable getIdentifier() {
		return identifier;
	}
	
}
