/*
 * @(#)OneToMany.java	2011-9-5
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
 * OneToMany
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: OneToMany.java 568 2011-10-10 07:44:32Z gerald.chen.hz $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OneToMany {
	
	/**
	 * Presents the [many] item's class type
	 *
	 * @return class type
	 */
	Class<?> type();

	/**
	 * if support cascade operation, default is true.
	 *
	 * @return <code>true</code> if is cascade operation, otherwise <code>false</code>
	 */
	boolean cascade() default true;
	
	/**
	 * "Many" item's foreign key field names.
	 *
	 * @return selField names
	 */
	String[] selField() default {};
	
	/**
	 * "One" item's primary key field names.
	 *
	 * @return ownerField names
	 */
	String[] ownerField() default {};
}
