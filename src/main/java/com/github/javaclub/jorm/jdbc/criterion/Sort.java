/*
 * @(#)Sort.java	2011-8-2
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.criterion;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Sort
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Sort.java 2011-8-2 下午03:28:19 Exp $
 */
public class Sort implements Serializable {

	/** desc */
	private static final long serialVersionUID = -8713775018578322827L;

	private Set<Order> sortFields = new HashSet<Order>();
	
	public Sort addOrder(Order order) {
		if(!sortFields.contains(order)) {
			sortFields.add(order);
		}
		return this;
	}
	
	public String toSqlString() {
		StringBuilder sbf = new StringBuilder();
		if(!sortFields.isEmpty()) {
			sbf.append("ORDER BY");
			int i = 0;
			for (Order order : sortFields) {
				if(i > 0) {
					sbf.append(",");
				}
				sbf.append(" " + order);
				i++;
			}
		}
		try {
			return sbf.toString();
		} finally {
			sbf.setLength(0);
			sbf = null;
		}
	}
	
	public int size() {
		return sortFields.size();
	}
	
}
