/*
 * @(#)ManyToMany.java	2011-9-5
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ManyToMany
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: ManyToMany.java 547 2011-10-09 10:10:45Z gerald.chen.hz $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToMany {
	
	/**
	 * if support cascade operation, default is true.
	 *
	 * @return <code>true</code> if is cascade operation, otherwise <code>false</code>
	 */
	boolean cascade() default true;
	
	String table() default "";
	
	String mappedId();
	
	Class<?> type();
}
