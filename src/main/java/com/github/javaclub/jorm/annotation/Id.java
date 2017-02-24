/*
 * @(#)Id.java	2010-4-21
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.github.javaclub.jorm.annotation.constant.GenerationType;


/**
 * Specifies the primary key property or field of an entity.
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: Id.java 547 2011-10-09 10:10:45Z gerald.chen.hz $
 */
@Target({FIELD}) 
@Retention(RUNTIME)
public @interface Id {

	/**
	 * The primary key generation strategy that the persistence provider must use
	 * to generate the annotated entity primary key.
	 */
	String value() default GenerationType.AUTO;
	
	/**
	 * 
	 * This attribute takes effect only when attribute[value]'s value is
	 * <tt>GenerationType.SEQUENCE</tt>
	 * 
	 * @return sequence name
	 */
	String sequenceName() default "";
	
	/**
	 * This attribute takes effect only attribute[value]'s value is
	 * <tt>GenerationType.INCREMENT</tt>
	 *
	 * @return increment step
	 */
	int incrementBy() default 1;
}
