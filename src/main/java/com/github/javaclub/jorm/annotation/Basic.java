/*
 * @(#)Basic.java	2011-8-30
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.javaclub.jorm.annotation.constant.FetchType;
import com.github.javaclub.jorm.jdbc.process.DummyFieldProcessor;

/**
 * Basic
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Basic.java 2011-8-30 21:49:40 Exp $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Basic {

	FetchType fetch() default FetchType.EAGER;
	
	Class processor() default DummyFieldProcessor.class;
}
