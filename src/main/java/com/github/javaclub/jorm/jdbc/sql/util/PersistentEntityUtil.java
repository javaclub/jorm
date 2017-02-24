/*
 * @(#)PersistentEntityUtil.java	2011-10-8
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.sql.util;

import com.github.javaclub.jorm.common.Strings;

/**
 * PersistentColumnUtil
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: PersistentEntityUtil.java 2011-10-8 下午04:48:15 Exp $
 * @since 1.0.7
 */
public abstract class PersistentEntityUtil {

	public static String entityIdColumname(Class<?> clazz) {
		return entityName(clazz) + "_id";
	}
	
	public static String entityName(Class<?> clazz) {
		return Strings.lowerCase(clazz.getSimpleName());
	}
}
