/*
 * @(#)DefaultSequence.java	2017年3月16日
 *
 * Copyright (c) 2017. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.seqence.impl;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.github.javaclub.jorm.seqence.Sequence;
import com.github.javaclub.jorm.seqence.SequenceDAO;
import com.github.javaclub.jorm.seqence.SequenceException;
import com.github.javaclub.jorm.seqence.SequenceRange;

/**
 * DefaultSequence
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: DefaultSequence.java 2017-3-16 22:04:05 Exp $
 */
public class DefaultSequence implements Sequence {

	private final Lock lock;
	private SequenceDAO sequenceDAO;
	private String name;
	private volatile SequenceRange currentRange;

	public DefaultSequence() {
		this.lock = new ReentrantLock();
	}

	public long nextValue() throws SequenceException {
		if (this.currentRange == null) {
			this.lock.lock();
			try {
				if (this.currentRange == null)
					this.currentRange = this.sequenceDAO.nextRange(this.name);
			} finally {
				this.lock.unlock();
			}
		}

		long value = this.currentRange.getAndIncrement();
		if (value == -1L) {
			this.lock.lock();
			try {
				while (true) {
					if (this.currentRange.isOver()) {
						this.currentRange = this.sequenceDAO.nextRange(this.name);
					}

					value = this.currentRange.getAndIncrement();
					if (value != -1L) {
						break;
					}
				}
			} finally {
				this.lock.unlock();
			}
		}

		if (value < 0L) {
			throw new SequenceException("Sequence value overflow, value = " + value);
		}

		return value;
	}

	public SequenceDAO getSequenceDAO() {
		return sequenceDAO;
	}

	public void setSequenceDAO(SequenceDAO sequenceDAO) {
		this.sequenceDAO = sequenceDAO;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
