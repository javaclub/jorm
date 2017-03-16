/*
 * @(#)Seqence.java	2017-3-16
 *
 * Copyright (c) 2017. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.seqence;

/**
 * Seqence
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Seqence.java 2017-3-16 21:59:40 Exp $
 */
public interface Sequence {

	public long nextValue() throws SequenceException;
	
}
