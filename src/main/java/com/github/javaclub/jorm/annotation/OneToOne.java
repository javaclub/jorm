/*
 * @(#)OneToOne.java	2011-9-4
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
 * OneToOne
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: OneToOne.java 2011-9-4 下午07:36:15 Exp $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OneToOne {

	/**
	 * Presents the one-to-one field entity's mapped ids.
	 *
	 * @return selField names
	 */
	String[] selField() default {};
	
	/**
	 * Presents the owner entity's mapped ids.
	 *
	 * @return ownerField names
	 */
	String[] ownerField() default {};
}
