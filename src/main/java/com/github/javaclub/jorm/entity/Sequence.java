/*
 * @(#)Sequence.java	2017-3-16
 *
 * Copyright (c) 2017. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.entity;

import com.github.javaclub.jorm.annotation.Column;
import com.github.javaclub.jorm.annotation.Entity;
import com.github.javaclub.jorm.annotation.Id;
import com.github.javaclub.jorm.annotation.PK;
import com.github.javaclub.jorm.annotation.constant.GenerationType;

/**
 * Sequence
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Sequence.java 2017-3-16 02:02:59 Exp $
 */
@Entity(table="seqence")
@PK("seqenceName")
public class Sequence {
	
	@Id(GenerationType.ASSIGNED)
	@Column("seq_name")
	private String seqenceName;

	@Column("cur_value")
	private long currentValue = 0;
	
	@Column("inc_value")
	private long incrementValue = 1;
	
	public Sequence() {
	}

	public Sequence(long currentValue, long incrementValue) {
		this.currentValue = currentValue;
		this.incrementValue = incrementValue;
	}
	
	public String getSeqenceName() {
		return seqenceName;
	}

	public void setSeqenceName(String seqenceName) {
		this.seqenceName = seqenceName;
	}

	public long getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(long currentValue) {
		this.currentValue = currentValue;
	}

	public long getIncrementValue() {
		return incrementValue;
	}

	public void setIncrementValue(long incrementValue) {
		this.incrementValue = incrementValue;
	}

}
