/*
 * @(#)AssertUtil.java	2011-7-21
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.common;


/**
 * AssertUtil
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: AssertUtil.java 2011-7-21 18:24:28 Exp $
 */
public abstract class AssertUtil {

	public static void notNull(Object obj) {
		notNull(obj, "The input method parameter can't be null.");
	}
	
	public static void notEmpty(String input, String message) {
		if(Strings.isEmpty(input)) {
			throw new IllegalArgumentException(message);
		}
	}
	
	public static void notNull(Object obj, String message) {
		if(obj == null) {
			throw new IllegalArgumentException(message);
		}
	}
	
	public static void notNull(String message, Object... objs) {
		if(null != objs && objs.length > 0) {
			for (int i = 0; i < objs.length; i++) {
				if(null == objs[i]) {
					throw new IllegalArgumentException(message);
				}
			}
		}
	}
	
	public static void isTrue(boolean expression) {
		isTrue(expression, "[Assertion failed] - this expression must be true");
	}
	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}
	
	public static void hasText(String text, String message) {
		if (!Strings.hasText(text)) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void hasText(String text) {
		hasText(text, "The String argument must have text; it must not be null, empty, or blank");
	}
}
