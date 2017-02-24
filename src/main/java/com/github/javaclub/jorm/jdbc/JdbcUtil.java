/*
 * @(#)JdbcUtil.java	2010-4-23
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.jdbc;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A utility class for jdbc.
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: JdbcUtil.java 182 2011-07-23 11:08:39Z gerald.chen.hz@gmail.com $
 */
@SuppressWarnings("unchecked")
public class JdbcUtil {

	public static final Log LOG = LogFactory.getLog(JdbcUtil.class);
	/**
	 * Reads a column from the current row in the provided
	 * {@link java.sql.ResultSet} and returns an instance of the specified Java
	 * {@link java.lang.Class} containing the values read.
	 * <p>
	 * This method is used while converting {@link java.sql.ResultSet} rows to
	 * objects. The class type is the field type in the target bean.
	 * <p>
	 * Correspondence between class types and ResultSet.get methods is as
	 * follows:
	 * <ul>
	 * <li> Boolean/boolean: getBoolean
	 * <li> Byte/byte: getByte
	 * <li> Short/short: getShort
	 * <li> Integer/int: getInt
	 * <li> Long/long: getLong
	 * <li> Float/float: getFloat
	 * <li> Double/double: getDouble
	 * <li> Character/char: getString
	 * <li> Character[]/char[]: getString
	 * <li> Byte[]/byte[]: setBytes
	 * <li> String: setString
	 * <li> java.math.BigDecimal: getBigDecimal
	 * <li> java.io.Reader: getCharacterStream
	 * <li> java.io.InputStream: getBinaryStream
	 * <li> java.util.Date: getTimestamp
	 * <li> java.sql.Date: getDate
	 * <li> java.sql.Time: getTime
	 * <li> java.sql.Timestamp: getTimestamp
	 * <li> java.sql.Clob: getClob
	 * <li> java.sql.Blob: getBlob
	 * </ul>
	 * <p>
	 * null's will be respected for any non-native types. This means that if a
	 * field is of type Integer it will be able to receive a null value from the
	 * ResultSet; on the other hand, if a field is of type int it will receive 0
	 * for a null value from the {@link java.sql.ResultSet}.
	 * 
	 * @param resultSet {@link java.sql.ResultSet} (positioned in the row to be
	 * processed)
	 * @param column column index in the result set (starting with 1)
	 * @param type {@link java.lang.Class} of the object to be returned
	 */
	public static Object getValueFromResultSet(final ResultSet resultSet, final int column, final Class type) {
		Object value = null;
		try {

			if (type == boolean.class) {
				value = resultSet.getBoolean(column);
			} else if (type == Boolean.class) {
				value = resultSet.getObject(column) == null ? null : resultSet.getBoolean(column);
			} else if (type == byte.class) {
				value = resultSet.getByte(column);
			} else if (type == Byte.class) {
				value = resultSet.getObject(column) == null ? null : resultSet.getByte(column);
			} else if (type == short.class) {
				value = resultSet.getShort(column);
			} else if (type == Short.class) {
				value = resultSet.getObject(column) == null ? null : resultSet.getShort(column);
			} else if (type == int.class) {
				value = resultSet.getInt(column);
			} else if (type == Integer.class) {
				value = resultSet.getObject(column) == null ? null : resultSet.getInt(column);
			} else if (type == long.class) {
				value = resultSet.getLong(column);
			} else if (type == Long.class) {
				value = resultSet.getObject(column) == null ? null : resultSet.getLong(column);
			} else if (type == float.class) {
				value = resultSet.getFloat(column);
			} else if (type == Float.class) {
				value = resultSet.getObject(column) == null ? null : resultSet.getFloat(column);
			} else if (type == double.class) {
				value = resultSet.getDouble(column);
			} else if (type == Double.class) {
				value = resultSet.getObject(column) == null ? null : resultSet.getDouble(column);
			} else if (type == BigDecimal.class) {
				value = resultSet.getObject(column) == null ? null : resultSet.getBigDecimal(column);
			} else if (type == String.class) {
				value = resultSet.getString(column);
			} else if (type == Character.class || type == char.class) {
				final String str = resultSet.getString(column);
				if (str != null && str.length() > 1) {
					throw new JdbcException("Column [" + column + "] returned a string with length ["
							+ str.length() + "] but field type [" + type.getSimpleName()
							+ "] can only accept 1 character");
				}
				value = (str == null || str.length() == 0) ? null : str.charAt(0);
			} else if (type == byte[].class || type == Byte[].class) {
				value = resultSet.getBytes(column);
			} else if (type == char[].class || type == Character[].class) {
				final String str = resultSet.getString(column);
				value = (str == null) ? null : str.toCharArray();
			} else if (type == java.util.Date.class) {
				final java.sql.Timestamp timestamp = resultSet.getTimestamp(column);
				value = (timestamp == null) ? null : new java.util.Date(timestamp.getTime());
			} else if (type == java.sql.Date.class) {
				value = resultSet.getDate(column);
			} else if (type == java.sql.Time.class) {
				value = resultSet.getTime(column);
			} else if (type == java.sql.Timestamp.class) {
				value = resultSet.getTimestamp(column);
			} else if (type == java.io.InputStream.class) {
				value = resultSet.getBinaryStream(column);
			} else if (type == java.io.Reader.class) {
				value = resultSet.getCharacterStream(column);
			} else if (type == java.sql.Clob.class) {
				value = resultSet.getClob(column);
			} else if (type == java.sql.Blob.class) {
				value = resultSet.getBlob(column);
			} else {
				// this will work for database-specific types
				value = resultSet.getObject(column);
			}

		} catch (SQLException e) {
			throw new JdbcException(e);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Read ResultSet [" + resultSet + "] column [" + column + "]"
					+ (value == null ? "" : " type [" + value.getClass().getSimpleName() + "]") + " value ["
					+ value + "]");
		}

		return value;
	}
	
	/**
	 * Sets parameters in the given prepared statement.
	 * <p>
	 * Parameters will be set using PreparedStatement set methods related with
	 * the Java types of the parameters, according with the following table:
	 * <ul>
	 * <li> Boolean/boolean: setBoolean
	 * <li> Byte/byte: setByte
	 * <li> Short/short: setShort
	 * <li> Integer/integer: setInt
	 * <li> Long/long: setLong
	 * <li> Float/float: setFloat
	 * <li> Double/double: setDouble
	 * <li> Character/char: setString
	 * <li> Character[]/char[]: setString
	 * <li> Byte[]/byte[]: setBytes
	 * <li> String: setString
	 * <li> java.math.BigDecimal: setBigDecimal
	 * <li> java.io.Reader: setCharacterStream
	 * <li> java.io.InputStream: setBinaryStream
	 * <li> java.util.Date: setTimestamp
	 * <li> java.sql.Date: setDate
	 * <li> java.sql.Time: setTime
	 * <li> java.sql.Timestamp: setTimestamp
	 * <li> java.sql.Clob : setClob
	 * <li> java.sql.Blob: setBlob
	 * </ul>
	 * 
	 * @param stmt {@link java.sql.PreparedStatement} to have parameters set
	 * into
	 * @param parameters varargs or Object[] with parameters values
	 * @throws RuntimeSQLException if a database access error occurs or this
	 * method is called on a closed PreparedStatement; if a parameter type does
	 * not have a matching set method (as outlined above)
	 * @throws RuntimeIOException if an error occurs while reading data from a
	 * Reader or InputStream parameter
	 */
	public static void setParameters(final PreparedStatement stmt, final Object[] parameters) {
		// if no parameters, do nothing
		if (parameters == null || parameters.length == 0) {
			return;
		}
		ParameterMetaData stmtMetaData = null;
		for (int i = 1; i <= parameters.length; i++) {
			final Object parameter = parameters[i - 1];
			if (parameter == null) {
				// lazy assignment of stmtMetaData
				if (stmtMetaData == null) {
					try {
						stmtMetaData = stmt.getParameterMetaData();
					} catch (SQLException e) {
						throw new JdbcException(e);
					}
				}
				// get sql type from prepared statement metadata
				int sqlType;
				try {
					sqlType = stmtMetaData.getParameterType(i);
				} catch (SQLException e2) {
					// feature not supported, use NULL
					sqlType = java.sql.Types.NULL;
				}
				try {
					stmt.setNull(i, sqlType);
				} catch (SQLException e) {
					throw new RuntimeException("Could not set null into parameter [" + i
							+ "] using java.sql.Types [" + sqlType + "] " + e.getMessage(), e);
				}
				if (LOG.isDebugEnabled()) {
					LOG.debug("Parameter [" + i + "] from PreparedStatement [" + stmt
							+ "] set to [null] using java.sql.Types [" + sqlType + "]");
				}
				
				continue;
			}

			try {
				final Class type = parameter.getClass();
				if (type == Boolean.class || type == boolean.class) {
					stmt.setBoolean(i, (Boolean) parameter);
				} else if (type == Byte.class || type == byte.class) {
					stmt.setByte(i, (Byte) parameter);
				} else if (type == Short.class || type == short.class) {
					stmt.setShort(i, (Short) parameter);
				} else if (type == Integer.class || type == int.class) {
					stmt.setInt(i, (Integer) parameter);
				} else if (type == Long.class || type == long.class) {
					stmt.setLong(i, (Long) parameter);
				} else if (type == Float.class || type == float.class) {
					stmt.setFloat(i, (Float) parameter);
				} else if (type == Double.class || type == double.class) {
					stmt.setDouble(i, (Double) parameter);
				} else if (type == Character.class || type == char.class) {
					stmt.setString(i, parameter == null ? null : "" + (Character) parameter);
				} else if (type == char[].class) {
					// not efficient, will create a new String object
					stmt.setString(i, parameter == null ? null : new String((char[]) parameter));
				} else if (type == Character[].class) {
					// not efficient, will duplicate the array and create a new String object
					final Character[] src = (Character[]) parameter;
					final char[] dst = new char[src.length];
					for (int j = 0; j < src.length; j++) { // can't use System.arraycopy here
						dst[j] = src[j];
					}
					stmt.setString(i, new String(dst));
				} else if (type == String.class) {
					stmt.setString(i, (String) parameter);
				} else if (type == BigDecimal.class) {
					stmt.setBigDecimal(i, (BigDecimal) parameter);
				} else if (type == byte[].class) {
					stmt.setBytes(i, (byte[]) parameter);
				} else if (type == Byte[].class) {
					// not efficient, will duplicate the array
					final Byte[] src = (Byte[]) parameter;
					final byte[] dst = new byte[src.length];
					for (int j = 0; j < src.length; j++) { // can't use System.arraycopy here
						dst[j] = src[j];
					}
					stmt.setBytes(i, dst);
				} else if (parameter instanceof java.io.Reader) {
					final java.io.Reader reader = (java.io.Reader) parameter;

					// the jdbc api for setCharacterStream requires the number
					// of characters to be read so this will end up reading 
					// data twice (here and inside the jdbc driver)
					// besides, the reader must support reset()
					int size = 0;
					try {
						reader.reset();
						while (reader.read() != -1) {
							size++;
						}
						reader.reset();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					stmt.setCharacterStream(i, reader, size);
				} else if (parameter instanceof java.io.InputStream) {
					final java.io.InputStream inputStream = (java.io.InputStream) parameter;

					// the jdbc api for setBinaryStream requires the number of
					// bytes to be read so this will end up reading the stream 
					// twice (here and inside the jdbc driver)
					// besides, the stream must support reset()
					int size = 0;
					try {
						inputStream.reset();
						while (inputStream.read() != -1) {
							size++;
						}
						inputStream.reset();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					stmt.setBinaryStream(i, inputStream, size);
				} else if (parameter instanceof Clob) {
					stmt.setClob(i, (Clob) parameter);
				} else if (parameter instanceof Blob) {
					stmt.setBlob(i, (Blob) parameter);
				} else if (type == java.util.Date.class) {
					final java.util.Date date = (java.util.Date) parameter;
					stmt.setTimestamp(i, new java.sql.Timestamp(date.getTime()));
				} else if (type == java.sql.Date.class) {
					stmt.setDate(i, (java.sql.Date) parameter);
				} else if (type == java.sql.Time.class) {
					stmt.setTime(i, (java.sql.Time) parameter);
				} else if (type == java.sql.Timestamp.class) {
					stmt.setTimestamp(i, (java.sql.Timestamp) parameter);
				} else {
					// last resort; this should cover all database-specific
					// object types
					stmt.setObject(i, parameter);
				}

				if (LOG.isDebugEnabled()) {
					LOG.debug("PreparedStatement [" + stmt + "] Parameter [" + i + "] type ["
							+ type.getSimpleName() + "] set to [" + parameter + "]");
				}

			} catch (SQLException e) {
				throw new JdbcException(e);
			}
		}
	}
}
