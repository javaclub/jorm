package com.github.javaclub.jorm.common.javabean;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.javaclub.jorm.common.DateTime;
import com.github.javaclub.jorm.common.Numbers;
import com.github.javaclub.jorm.common.Strings;

/**
 * The Java data type handler.
 * 
 */
@SuppressWarnings("unchecked")
public class JavaTypeHandlers {

	private static transient final Log log = LogFactory
			.getLog(JavaTypeHandlers.class);

	public static final Map COMMON_DATA_TYPE_HANDLERS = new HashMap();

	static {
		try {
			COMMON_DATA_TYPE_HANDLERS.put("byte", ByteHandler.class
					.newInstance());
			COMMON_DATA_TYPE_HANDLERS.put(Byte.class.getName(),
					ByteHandler.class.newInstance());

			COMMON_DATA_TYPE_HANDLERS.put("short", ShortHandler.class
					.newInstance());
			COMMON_DATA_TYPE_HANDLERS.put(Short.class.getName(),
					ShortHandler.class.newInstance());

			COMMON_DATA_TYPE_HANDLERS.put("char", CharacterHandler.class
					.newInstance());
			COMMON_DATA_TYPE_HANDLERS.put(Character.class.getName(),
					CharacterHandler.class.newInstance());

			COMMON_DATA_TYPE_HANDLERS.put("int", IntegerHandler.class
					.newInstance());
			COMMON_DATA_TYPE_HANDLERS.put(Integer.class.getName(),
					IntegerHandler.class.newInstance());

			COMMON_DATA_TYPE_HANDLERS.put("boolean", BooleanHandler.class
					.newInstance());
			COMMON_DATA_TYPE_HANDLERS.put(Boolean.class.getName(),
					BooleanHandler.class.newInstance());

			COMMON_DATA_TYPE_HANDLERS.put("long", LongHandler.class
					.newInstance());
			COMMON_DATA_TYPE_HANDLERS.put(Long.class.getName(),
					LongHandler.class.newInstance());

			COMMON_DATA_TYPE_HANDLERS.put("float", FloatHandler.class
					.newInstance());
			COMMON_DATA_TYPE_HANDLERS.put(Float.class.getName(),
					FloatHandler.class.newInstance());

			COMMON_DATA_TYPE_HANDLERS.put("double", DoubleHandler.class
					.newInstance());
			COMMON_DATA_TYPE_HANDLERS.put(Double.class.getName(),
					DoubleHandler.class.newInstance());

			COMMON_DATA_TYPE_HANDLERS.put("java.util.Date", DateHandler.class
					.newInstance());
			COMMON_DATA_TYPE_HANDLERS.put("java.sql.Date", SqlDateHandler.class
					.newInstance());
			COMMON_DATA_TYPE_HANDLERS.put("java.sql.Timestamp",
					TimestampHandler.class.newInstance());
			COMMON_DATA_TYPE_HANDLERS.put("java.sql.Time", TimeHandler.class
					.newInstance());

			COMMON_DATA_TYPE_HANDLERS.put("java.math.BigInteger",
					BigIntegerHandler.class.newInstance());
			COMMON_DATA_TYPE_HANDLERS.put("java.math.BigDecimal",
					BigDecimalHandler.class.newInstance());

			COMMON_DATA_TYPE_HANDLERS.put(String.class.getName(),
					StringHandler.class.newInstance());

		} catch (InstantiationException e) {
			log.error("init IDataTypeHandler failed.", e);
		} catch (IllegalAccessException e) {
			log.error("init IDataTypeHandler failed.", e);
		}
	}

	public static IDataTypeHandler getUnsupportedDataHandler(Class fieldType) {
		return new UnsupportedDataHandler(fieldType);
	}

	/**
	 * 将字符串转换为指定的数据类型。
	 * 
	 * @param value
	 *            要转换的字符串
	 * @param className
	 *            要转换成的类型，如int, float等。
	 * 
	 */
	public static Object convertValueToType(String value, String className) {
		IDataTypeHandler mh = (IDataTypeHandler) COMMON_DATA_TYPE_HANDLERS
				.get(className);

		if (mh == null) {
			throw new RuntimeException("unknown data type :" + className);
		}

		return mh.getValue(value);
	}

	public static void main(String[] args) {
		System.out.println(byte[].class);
	}

}

class BigDecimalHandler implements IDataTypeHandler {

	public Object getValue(String fieldValue) {
		return Numbers.createBigDecimal(fieldValue);
	}
}

class BigIntegerHandler implements IDataTypeHandler {

	public Object getValue(String fieldValue) {
		return Numbers.createBigInteger(fieldValue);
	}
}

class ByteHandler implements IDataTypeHandler {

	public Object getValue(String fieldValue) {
		return Byte.valueOf(fieldValue);
	}
}

class ShortHandler implements IDataTypeHandler {

	public Object getValue(String fieldValue) {
		return Short.valueOf(fieldValue);
	}
}

class IntegerHandler implements IDataTypeHandler {

	public Object getValue(String fieldValue) {
		return Integer.valueOf(fieldValue);
	}
}

class CharacterHandler implements IDataTypeHandler {

	public Object getValue(String fieldValue) {
		return Strings.isEmpty(fieldValue) ? null : Character
				.valueOf(fieldValue.charAt(0));
	}
}

class StringHandler implements IDataTypeHandler {

	public Object getValue(String fieldValue) {
		return fieldValue;
	}
}

class LongHandler implements IDataTypeHandler {

	public Object getValue(String fieldValue) {
		return Long.valueOf(fieldValue);
	}
}

class FloatHandler implements IDataTypeHandler {

	public Object getValue(String fieldValue) {
		return Float.valueOf(fieldValue);
	}
}

class DoubleHandler implements IDataTypeHandler {

	public Object getValue(String fieldValue) {
		return Double.valueOf(fieldValue);
	}
}

class DateHandler implements IDataTypeHandler {

	public Object getValue(String fieldValue) {
		return DateTime.toDate(fieldValue);
	}
}

class SqlDateHandler implements IDataTypeHandler {

	public Object getValue(String fieldValue) {
		return DateTime.toSqlDate(fieldValue);
	}
}

class TimeHandler implements IDataTypeHandler {

	public Object getValue(String fieldValue) {
		return DateTime.toTime(fieldValue);
	}
}

class TimestampHandler implements IDataTypeHandler {

	public Object getValue(String fieldValue) {
		return DateTime.toTimestamp(fieldValue);
	}
}

class BooleanHandler implements IDataTypeHandler {

	public Object getValue(String fieldValue) {
		char c = fieldValue.charAt(0);
		if (c == '1' || c == 'y' || c == 'Y' || c == 't' || c == 'T')
			return Boolean.TRUE;

		return Boolean.FALSE;
	}
}

@SuppressWarnings("unchecked")
class UnsupportedDataHandler implements IDataTypeHandler {

	private Class fieldType;

	public UnsupportedDataHandler(Class fieldType) {
		this.fieldType = fieldType;
	}

	public Object getValue(String fieldValue) {
		throw new RuntimeException("unknown data type :" + fieldType);
	}
}
