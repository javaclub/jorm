/*
 * @(#)Database.java	2011-8-6
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm;

import java.io.Serializable;
import java.util.Map;

import com.github.javaclub.jorm.common.CaseInsensitiveMap;

/**
 * Database
 * 
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Database.java 237 2011-08-14 11:50:32Z gerald.chen.hz@gmail.com $
 */
public class Database implements Serializable {

	private static final long serialVersionUID = 3291209643375807884L;

	private final String name;

	private static final Map<String, Database> INSTANCES = new CaseInsensitiveMap<String, Database>();

	public static final Database H2 = new Database("H2");
	
	public static final Database DB2 = new Database("DB2");
	
	public static final Database DERBY = new Database("Derby");

	public static final Database MYSQL = new Database("MySQL");
	
	public static final Database ORACLE = new Database("Oracle");
	
	public static final Database HSQLDB = new Database("Hsqldb");
	
	public static final Database FIREBIRD = new Database("Firebird");
	
	public static final Database SQLSERVER = new Database("SQLServer");
	
	public static final Database POSTGRESQL = new Database("PostgreSQL");
	
	private Database(String name) {
		this.name = name;
	}

	public static Database parse(String name) {
		return (Database) INSTANCES.get(name);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		Database other = (Database) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String toString() {
		return name;
	}

	static {
		INSTANCES.put(H2.name, H2);
		INSTANCES.put(DB2.name, DB2);
		INSTANCES.put(DERBY.name, DERBY);
		INSTANCES.put(MYSQL.name, MYSQL);
		INSTANCES.put(ORACLE.name, ORACLE);
		INSTANCES.put(HSQLDB.name, HSQLDB);
		INSTANCES.put(FIREBIRD.name, FIREBIRD);
		INSTANCES.put(SQLSERVER.name, SQLSERVER);
		INSTANCES.put(POSTGRESQL.name, POSTGRESQL);
	}
}
