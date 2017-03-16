/*
 * @(#)SequenceRange.java	2017年3月16日
 *
 * Copyright (c) 2017. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.seqence;

import java.util.concurrent.atomic.AtomicLong;

/**
 * SequenceRange
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: SequenceRange.java 2017年3月16日 下午10:00:53 Exp $
 */
public class SequenceRange {

	private final long min;
	private final long max;
	private final AtomicLong value;
	private volatile boolean over = false;

	public SequenceRange(long min, long max) {
		this.min = min;
		this.max = max;
		this.value = new AtomicLong(min);
	}

	public long getAndIncrement() {
		long currentValue = this.value.getAndIncrement();
		if (currentValue > this.max) {
			this.over = true;
			return -1L;
		}

		return currentValue;
	}

	public long getMin() {
		return this.min;
	}

	public long getMax() {
		return this.max;
	}

	public boolean isOver() {
		return this.over;
	}
}
