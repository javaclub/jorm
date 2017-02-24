/*
 * @(#)Relation.java	2011-7-9
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.javaclub.jorm.annotation.constant.RelationType;

/**
 * Relation
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Relation.java 297 2011-09-04 11:56:19Z gerald.chen.hz $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Relation {
	
	/**
	 * 关联关系，默认值：无任何关系
	 *
	 * @return
	 */
	int relation() default RelationType.NONE;

	/**
	 * 自己的关联属性
	 *
	 * @return
	 */
	String[] thisField();
	
	/**
	 * 关联目标的属性
	 *
	 * @return
	 */
	String[] thatField();
	
	/**
	 * 关联的目标对象对应的类型
	 *
	 * @return
	 */
	Class<?> thatClass();
}
