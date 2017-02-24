/*
 * @(#)XmlParserException.java	2011-6-27
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.common.xml;


/**
 * desc
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: XmlParserException.java 66 2011-06-27 03:21:59Z gerald.chen.hz@gmail.com $
 */
public class XmlParserException extends RuntimeException {

	/** desc */
	private static final long serialVersionUID = -3127018933751782523L;

	public XmlParserException() {
		super();
	}

	public XmlParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public XmlParserException(String message) {
		super(message);
	}

	public XmlParserException(Throwable cause) {
		super(cause);
	}
	
}
