/*
 * @(#)Order.java	2011-7-22
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.criterion;

import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.jdbc.ClassMetadata;


/**
 * Order
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Order.java 2011-7-22 下午01:55:44 Exp $
 */
public class Order implements Criterion {

	private static final long serialVersionUID = 4785555347355641882L;
	private boolean ascending;
	private String propertyName;
	
	/**
	 * Constructor for Order.
	 */
	protected Order(String propertyName, boolean ascending) {
		this.propertyName = propertyName;
		this.ascending = ascending;
	}
	
	/**
	 * Ascending order
	 *
	 * @param propertyName
	 * @return Order
	 */
	public static Order asc(String propertyName) {
		return new Order(propertyName, true);
	}

	/**
	 * Descending order
	 *
	 * @param propertyName
	 * @return Order
	 */
	public static Order desc(String propertyName) {
		return new Order(propertyName, false);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (ascending ? 1231 : 1237);
		result = prime * result
				+ ((propertyName == null) ? 0 : propertyName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		if (ascending != other.ascending)
			return false;
		if (propertyName == null) {
			if (other.propertyName != null)
				return false;
		} else if (!propertyName.equals(other.propertyName))
			return false;
		return true;
	}

	/**
	 * Render the SQL fragment
	 *
	 */
	public String toSqlString() {
		return "ORDER BY " + toString();
	}
	
	public String toString() {
		return propertyName + ' ' + (ascending ? "ASC" : "DESC");
	}
	
	@SuppressWarnings("unchecked")
	public String toColumnString(Class objectClass) {
		String column = ClassMetadata.getClassMetadata(objectClass).column(propertyName);
		if(Strings.isEmpty(column)) {
			return "";
		}
		return column + ' ' + (ascending ? "ASC" : "DESC");
	}
	
}
