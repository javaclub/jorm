/*
 * @(#)GeneratedStrategy.java	2011-8-14
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.id;

import java.io.Serializable;

import com.github.javaclub.jorm.Database;

/**
 * 数据库主键(由数据库自动生成)的生成策略
 * 
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: GeneratedStrategy.java 237 2011-08-14 11:50:32Z gerald.chen.hz@gmail.com $
 */
public class GeneratedStrategy implements Serializable {

	/** desc */
	private static final long serialVersionUID = 6943472023193692788L;

	public static final GeneratedStrategy NONE = new GeneratedStrategy();

	/** For Auto generated key of MySQL */
	public static final GeneratedStrategy AUTO_INCREMENT = new GeneratedStrategy(Database.MYSQL, "_MYSQL_AUTO_INCREMENT");

	private Database database;

	private String name;

	private GeneratedStrategy() {
		this(null, null);
	}

	private GeneratedStrategy(Database database, String name) {
		this.database = database;
		this.name = name;
	}
	
	public static GeneratedStrategy parse(Database database, String strategyName) {
		if(database == Database.MYSQL) {
			
		}
		return new GeneratedStrategy(database, strategyName);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((database == null) ? 0 : database.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeneratedStrategy other = (GeneratedStrategy) obj;
		if (database == null) {
			if (other.database != null)
				return false;
		} else if (!database.equals(other.database))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
