/*
 * @(#)ManyToOne.java	2011-9-5
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
 * ManyToOne
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: ManyToOne.java 547 2011-10-09 10:10:45Z gerald.chen.hz $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToOne {
	
	

	/**
	 * "One" item's primary key field names.
	 *
	 * @return selField names
	 */
	String[] selField() default {};
	
	/**
	 * "Many" item's foreign key field names.
	 *
	 * @return ownerField names
	 */
	String[] ownerField() default {};
}
