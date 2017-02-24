/*
 * @(#)FormatStyle.java	2011-8-13
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.sql.util;


/**
 * Represents the the understood types or styles of formatting.
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: FormatStyle.java 237 2011-08-14 11:50:32Z gerald.chen.hz@gmail.com $
 */
public class FormatStyle {

	public static final FormatStyle BASIC = new FormatStyle( "basic", new BasicFormatterImpl() );
	public static final FormatStyle DDL = new FormatStyle( "ddl", new DDLFormatterImpl() );
	public static final FormatStyle NONE = new FormatStyle( "none", new NoFormatImpl() );

	private final String name;
	private final Formatter formatter;

	private FormatStyle(String name, Formatter formatter) {
		this.name = name;
		this.formatter = formatter;
	}

	public String getName() {
		return name;
	}

	public Formatter getFormatter() {
		return formatter;
	}

	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		FormatStyle that = ( FormatStyle ) o;

		return name.equals( that.name );

	}

	public int hashCode() {
		return name.hashCode();
	}

	private static class NoFormatImpl implements Formatter {
		public String format(String source) {
			return source;
		}
	}
}
