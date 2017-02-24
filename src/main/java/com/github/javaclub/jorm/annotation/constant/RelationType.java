/*
 * @(#)RelationType.java	2011-7-9
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.annotation.constant;

/**
 * RelationType
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: RelationType.java 297 2011-09-04 11:56:19Z gerald.chen.hz $
 */
public class RelationType {
	
	/** 没有关联关系 */
	public static final int NONE = 0;

	/** 一对一 */
	public static final int OneToOne = 11;
	
	/** 一对多 */
	public static final int OneToMany = 19;
	
	/** 多对一 */
	public static final int ManyToOne = 91;
	
	/** 多对多 */
	public static final int ManyToMany = 99;
}
