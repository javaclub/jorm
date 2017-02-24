/*
 * @(#)DatabaseInfo.java	May 16, 2010
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.jdbc;

import java.io.Serializable;

/**
 * The simple infomation of database, including productname, productversion, drivername, ect.
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: DatabaseInfo.java 182 2011-07-23 11:08:39Z gerald.chen.hz@gmail.com $
 */
public class DatabaseInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String databaseProductName;
    private String databaseProductVersion;
    private String databaseUrl;
    private String databaseUsername;
    private String driverName;
    private String driverVersion;
    
	public DatabaseInfo() {
		super();
	}

	public DatabaseInfo(String databaseProductName, String databaseProductVersion,
			String databaseUrl, String databaseUsername, String driverName,
			String driverVersion) {
		super();
		this.databaseProductName = databaseProductName;
		this.databaseProductVersion = databaseProductVersion;
		this.databaseUrl = databaseUrl;
		this.databaseUsername = databaseUsername;
		this.driverName = driverName;
		this.driverVersion = driverVersion;
	}

	public String getDatabaseProductName() {
		return databaseProductName;
	}

	public String getDatabaseProductVersion() {
		return databaseProductVersion;
	}

	public String getDatabaseUrl() {
		return databaseUrl;
	}

	public String getDatabaseUsername() {
		return databaseUsername;
	}

	public String getDriverName() {
		return driverName;
	}

	public String getDriverVersion() {
		return driverVersion;
	}

	public void setDatabaseProductName(String databaseProductName) {
		this.databaseProductName = databaseProductName;
	}

	public void setDatabaseProductVersion(String databaseProductVersion) {
		this.databaseProductVersion = databaseProductVersion;
	}

	public void setDatabaseUrl(String databaseUrl) {
		this.databaseUrl = databaseUrl;
	}

	public void setDatabaseUsername(String databaseUsername) {
		this.databaseUsername = databaseUsername;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public void setDriverVersion(String driverVersion) {
		this.driverVersion = driverVersion;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{databaseProductName:" + databaseProductName);
		sb.append(", databaseProductVersion:" + databaseProductVersion);
		sb.append(", databaseUrl:" + databaseUrl);
		sb.append(", databaseUsername:" + databaseUsername);
		sb.append(", driverName:" + driverName);
		sb.append(", driverVersion:" + driverVersion + "}");
		return sb.toString();
	}

}