/*
 * @(#)DataTools.java	2011-9-22
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm;

import com.github.javaclub.jorm.common.Numbers;
import com.github.javaclub.jorm.common.Strings;

/**
 * DataTools
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: DataTools.java 2011-9-22 上午10:38:22 Exp $
 */
public abstract class DataTools {
	
	static final String NUMBER_CHARS = "5631249870";

	public static int age(int max) {
		return Numbers.random(max);
	}
	
	public static String numbers(int length) {
		return Strings.random(length, NUMBER_CHARS);
	}
}
