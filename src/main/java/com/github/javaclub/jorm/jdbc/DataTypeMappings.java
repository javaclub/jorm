package com.github.javaclub.jorm.jdbc;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.Map;
import java.util.TreeMap;

/**
 * Default SQL data type to Java type mappings for object generator. Data types
 * depend on JDBC driver implementation.<br>
 * <br>
 * 
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: DataTypeMappings.java 85 2011-07-09 10:49:54Z gerald.chen.hz@gmail.com $
 */
@SuppressWarnings("unchecked")
public class DataTypeMappings {

	
	public static java.util.Map TYPE_MAPPINGS;

	protected static boolean isNumber(Object obj) {

		return obj instanceof Integer || obj instanceof BigDecimal
				|| obj instanceof Double || obj instanceof Long;
	}

	static {

		Map tm = new TreeMap();
		tm.put(new Integer(Types.BIT), "boolean");

		tm.put(new Integer(Types.BLOB), "java.sql.Blob");
		tm.put(new Integer(Types.CLOB), "java.sql.Clob");

		tm.put(new Integer(Types.DATE), "java.sql.Date");
		tm.put(new Integer(Types.TIME), "java.sql.Time");
		tm.put(new Integer(Types.TIMESTAMP), "java.sql.Timestamp");

		tm.put(new Integer(Types.VARCHAR), "String");
		tm.put(new Integer(Types.CHAR), "String");
		tm.put(new Integer(Types.LONGVARCHAR), "String");

		tm.put(new Integer(Types.INTEGER), "Integer");
		tm.put(new Integer(Types.TINYINT), "Integer");
		tm.put(new Integer(Types.SMALLINT), "Integer");

		tm.put(new Integer(Types.BIGINT), "long");

		tm.put(new Integer(Types.NUMERIC), "java.math.BigDecimal");
		tm.put(new Integer(Types.DECIMAL), "java.math.BigDecimal");

		tm.put(new Integer(Types.REAL), "float");

		tm.put(new Integer(Types.FLOAT), "double");
		tm.put(new Integer(Types.DOUBLE), "double");

		TYPE_MAPPINGS = tm;

	}

}
