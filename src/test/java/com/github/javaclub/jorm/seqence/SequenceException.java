/*
 * @(#)SequenceException.java	2017年3月16日
 *
 * Copyright (c) 2017. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.seqence;

/**
 * SequenceException
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: SequenceException.java 2017年3月16日 下午10:01:54 Exp $
 */
public class SequenceException extends Exception {

	private static final long serialVersionUID = 1L;

	public SequenceException() {
	}

	public SequenceException(String message) {
		super(message);
	}

	public SequenceException(String message, Throwable cause) {
		super(message, cause);
	}

	public SequenceException(Throwable cause) {
		super(cause);
	}
}
