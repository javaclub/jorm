/*
 * @(#)GenerationType.java	2010-4-21
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.annotation.constant;


/**
 * Defines the types of primary key generation.
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: GenerationType.java 547 2011-10-09 10:10:45Z gerald.chen.hz $
 */
public class GenerationType {

	/** user assigned type */
	public static final String ASSIGNED = "assigned";
	
	/**
	 * auto increment number type for number by jorm inner impl
	 * <p>
	 * 
	 * same impl with {@link #INCREMENT}
	 */
	public static final String AUTO = "auto";
	
	/** uuid identityfier type by jorm inner impl, using jdkApi */
	public static final String UUID = "uuid";
	
	/** 
	 * Guid generated by database 
	 * 
	 * <li>SQLServer	=> select newid()
	 * <li>MySQL		=> select uuid()
	 * <li>Oracle		=> select rawtohex(sys_guid()) from dual
	 * <li>PostgreSQL	=> select uuid_generate_v1()
	 */
	public static final String GUID = "guid";
	
	/** one-to-one */
	public static final String FOREIGN = "foreign";
	
	/** 
	 *  database, such as oracle
	 */
	public static final String SEQUENCE = "sequence";
	
	/** Jorm framework 实现的自增ID策略 */
	public static final String INCREMENT = "increment";
	
	/** MySQL的AUTO_INCREMENT、MSSQL的identity、PostgreSQL的serial等auto-generated keys */
	public static final String IDENTITY = "identity";
	
	/**
	 * If primary key must be valued while inserting a record to database.
	 *
	 * @param type id GenerationType value
	 * @return <code>true</code> if primary key must be valued while inserting a record to database, 
	 * 			otherwise <code>false</code>.
	 */
	public static boolean isIdNeedManage(final String type) {
		return !isAutoGenerated(type);
	}
	
	/**
	 * If primary key is generated by database, that not must be valued before stored to database.
	 *
	 * @return <code>true</code> if the strategy type indicates that identifier is auto-generated by database, 
	 * 			otherwise <code>false</code>.
	 */
	public static boolean isAutoGenerated(final String type) {
		return (null != type) && (type.equals(IDENTITY));
	}
	
}
