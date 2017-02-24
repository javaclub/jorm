/*
 * @(#)IdentifierGeneratorFactory.java	2011-8-5
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.id;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.github.javaclub.jorm.common.AssertUtil;
import com.github.javaclub.jorm.common.Reflections;
import com.github.javaclub.jorm.jdbc.JdbcException;
import com.github.javaclub.jorm.jdbc.sql.Dialect;

/**
 * Factory and helper methods for <tt>IdentifierGenerator</tt>
 * 
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: IdentifierGeneratorFactory.java 2011-8-5 下午07:25:04 Exp $
 */
@SuppressWarnings("unchecked")
public final class IdentifierGeneratorFactory {

	private static final Map GENERATORS = new HashMap();
	static {
		GENERATORS.put("auto", AutoIdGenerator.class);
		GENERATORS.put("assigned", AssignedGenerator.class);
		GENERATORS.put("uuid", UuidGenerator.class);
		GENERATORS.put("sequence", SequenceGenerator.class);
		GENERATORS.put("increment", IncrementGenerator.class);
		GENERATORS.put("foreign", ForeignGenerator.class);
		GENERATORS.put("guid", GuidGenerator.class);
		GENERATORS.put("identity", IdentityGenerator.class);
	}
	
	public static IdentifierGenerator create(String strategy, Dialect dialect)
			throws JdbcException {
		IdentifierGenerator idgen;
		try {
			Class clazz = getIdentifierGeneratorClass(strategy);
			idgen = (IdentifierGenerator) clazz.newInstance();
			if (null != idgen) {
				idgen.setDialect(dialect);
			}
			return idgen;
		} catch (Exception e) {
			throw new JdbcException(
					"could not instantiate id generator, whose strategy name => ["
							+ strategy + "]", e);
		} finally {
			idgen = null;
		}
	}

	public static Class getIdentifierGeneratorClass(String strategy)
			throws JdbcException {
		Class clazz = (Class) GENERATORS.get(strategy);
		try {
			if (clazz == null) {
				clazz = Reflections.classForName(strategy);
			}
		} catch (ClassNotFoundException e) {
			throw new JdbcException(
					"the id generated strategy is not supported, id-strategy-value ["
							+ strategy + "].");
		}
		return clazz;
	}
	
	public static Serializable createIdentifier(long value, Class clazz) throws JdbcException {
		String name = clazz.getName();
		if (name.equals("java.lang.Long") || name.equals("long")) {
			return new Long( value );
		}
		else if (name.equals("java.lang.Integer") || name.equals("int")) {
			return new Integer( ( int ) value );
		}
		else if (name.equals("java.lang.Short") || name.equals("short")) {
			return new Short( ( short ) value );
		}
		else if (name.equals("java.lang.String")) {
			return String.valueOf(value);
		}
		else {
			throw new JdbcException( "This id generator generates string, long, integer, short" );
		}
	}
	
	public static Serializable getGeneratedIdentity(Object id, Class type)
			throws JdbcException {
		AssertUtil.notNull(id, "The auto-generated key is null.");
		String name = type.getName();
		if (name.equals("java.lang.Long") || name.equals("long")) {
			return Long.valueOf(id.toString());
		} else if (name.equals("java.lang.Integer") || name.equals("int")) {
			return Integer.valueOf(id.toString());
		} else if (name.equals("java.lang.Short") || name.equals("short")) {
			return Short.valueOf(id.toString());
		} else if (name.equals("java.lang.String")) {
			return (String) id;
		} else {
			throw new JdbcException(
					"This id generator generates string, long, integer, short");
		}

	}
	
	public static boolean isFieldInitialized(Field identifier, Object obj) {
		if(null == identifier) {
			return false;
		}
		if(identifier.getType() == boolean.class) {
			return (Boolean) Reflections.getFieldValue(obj, identifier) != Boolean.FALSE;
		} else if(identifier.getType() == char.class) {
			return ((Character) Reflections.getFieldValue(obj, identifier)).charValue() != '\u0000';
		} else if(identifier.getType() == byte.class) {
			return ((Byte) Reflections.getFieldValue(obj, identifier)).byteValue() != (byte) 0;
		} else if(identifier.getType() == int.class) {
			return ((Integer) Reflections.getFieldValue(obj, identifier)).intValue() != 0;
		} else if(identifier.getType() == short.class) {
			return ((Short) Reflections.getFieldValue(obj, identifier)).shortValue() != (short) 0;
		} else if(identifier.getType() == float.class) {
			return ((Float) Reflections.getFieldValue(obj, identifier)).floatValue() != (float) 0;
		} else if(identifier.getType() == double.class) {
			return ((Double) Reflections.getFieldValue(obj, identifier)).doubleValue() != (double) 0;
		} else if(identifier.getType() == long.class) {
			return ((Long) Reflections.getFieldValue(obj, identifier)).longValue() != (long) 0;
		} else {
			return null != Reflections.getFieldValue(obj, identifier);
		}
	}
	
	private IdentifierGeneratorFactory() {
	}
	
}
