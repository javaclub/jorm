/*
 * @(#)Formatter.java	2011-8-13
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.sql.util;

/**
 * Formatter contract
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Formatter.java 237 2011-08-14 11:50:32Z gerald.chen.hz@gmail.com $
 */
public interface Formatter {

	public String format(String source);
}
