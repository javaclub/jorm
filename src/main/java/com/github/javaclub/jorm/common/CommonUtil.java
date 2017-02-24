/*
 * @(#)CommonUtil.java	2011-7-21
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

/**
 * CommonUtil
 * 
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: CommonUtil.java 2011-7-21 下午06:00:16 Exp $
 */
public abstract class CommonUtil {
	
	private static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7',
		'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * Returns true if the provided class is a type supported natively (as
	 * opposed to a bean).
	 * 
	 * @param type
	 *            {@link java.lang.Class} type to be tested
	 */
	public static boolean isNativeType(final Class<?> type) {

		// to return an arbitrary object use Object.class
		return (type == boolean.class || type == Boolean.class
				|| type == byte.class || type == Byte.class
				|| type == short.class || type == Short.class
				|| type == int.class || type == Integer.class
				|| type == long.class || type == Long.class
				|| type == float.class || type == Float.class
				|| type == double.class || type == Double.class
				|| type == char.class || type == Character.class
				|| type == byte[].class || type == Byte[].class
				|| type == char[].class || type == Character[].class
				|| type == String.class || type == BigDecimal.class
				|| type == java.util.Date.class || type == java.sql.Date.class
				|| type == java.sql.Time.class
				|| type == java.sql.Timestamp.class
				|| type == java.io.InputStream.class
				|| type == java.io.Reader.class || type == java.sql.Clob.class
				|| type == java.sql.Blob.class || type == Object.class);
	}
	
	public static boolean matched(Class<?> nativeType, String className) {
		if(nativeType.getName().equals(className)) {
			return true;
		}
		if((nativeType == int.class || nativeType == Integer.class) 
			&& ("int".equals(className) || "java.lang.Integer".equals(className))) {
			return true;
		}
		if((nativeType == int.class || nativeType == Integer.class) 
				&& ("long".equals(className) || "java.lang.Long".equals(className))) {
				return true;
			}
		if((nativeType == long.class || nativeType == Long.class) 
				&& ("long".equals(className) || "java.lang.Long".equals(className))) {
			return true;
		}
		if((nativeType == long.class || nativeType == Long.class) 
				&& ("int".equals(className) || "java.lang.Long".equals(className))) {
			return true;
		}
		if((nativeType == double.class || nativeType == Double.class) 
				&& ("double".equals(className) || "java.lang.Integer".equals(className))) {
			return true;
		}
		if((nativeType == float.class || nativeType == Float.class) 
				&& ("float".equals(className) || "java.lang.Float".equals(className))) {
			return true;
		}
		if((nativeType == byte.class || nativeType == Byte.class) 
				&& ("byte".equals(className) || "java.lang.Byte".equals(className))) {
			return true;
		}
		if((nativeType == char.class || nativeType == Character.class) 
				&& ("char".equals(className) || "java.lang.Character".equals(className))) {
			return true;
		}
		if((nativeType == short.class || nativeType == Short.class) 
				&& ("short".equals(className) || "java.lang.Short".equals(className))) {
			return true;
		}
		return false;
	}

	public static String join(String[] array, String sep) {
		if (isEmpty(array)) {
			return null;
		}
		StringBuilder sbf = new StringBuilder();
		try {
			for (int i = 0; i < array.length; i++) {
				if (i > 0) {
					sbf.append(sep);
				}
				if (array[i] != null) {
					sbf.append(array[i]);
				}
			}
			return sbf.toString();
		} finally {
			sbf.setLength(0);
			sbf = null;
		}
	}

	public static boolean isEmpty(Object source) {
		return 0 == length(source);
	}

	public static int length(Object obj) {
		if (null == obj)
			return 0;
		if (obj.getClass().isArray()) {
			return Array.getLength(obj);
		} else if (obj instanceof Collection<?>) {
			return ((Collection<?>) obj).size();
		} else if (obj instanceof Map<?, ?>) {
			return ((Map<?, ?>) obj).size();
		}
		return 1;
	}

	public static Object first(Object obj) {
		if (null == obj) {
			return null;
		}
		if (obj.getClass().isArray()) {
			if (0 == Array.getLength(obj)) {
				return null;
			}
			return Array.get(obj, 0);
		} else if (obj instanceof Collection<?>) {
			return ((Collection<?>) obj).iterator().next();
		} else if (obj instanceof Map<?, ?>) {
			return ((Map<?, ?>) obj).entrySet().iterator().next();
		}
		return obj;
	}
	
	public static Object get(Object obj, int index) {
		if(0 == length(obj)) {
			return null;
		}
		int i = 0;
		Iterator<?> iter = null;
		if (obj.getClass().isArray()) {
			if (0 == Array.getLength(obj)) {
				return null;
			}
			return Array.get(obj, index);
		} else if (obj instanceof Collection<?>) {
			if(obj instanceof List<?>) {
				return ((List<?>) obj).get(index);
			} else {
				Collection<?> items = (Collection<?>) obj;
				if(index >= items.size()) {
					throw new ArrayIndexOutOfBoundsException(index);
				}
				iter = items.iterator();
				while (iter.hasNext()) {
					if(index == i++) {
						return iter.next();
					} else {
						iter.next();
					}
				}
				return null;
			}
		} else if (obj instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?, ?>) obj;
			if(index >= map.size()) {
				throw new ArrayIndexOutOfBoundsException(index);
			}
			iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				if(index == i++) {
					return iter.next();
				} else {
					iter.next();
				}
			}
			return null;
		}
		return obj;
	}

	/**
	 * 将Map转换为元素为List类型的List，得到的List只有两个List类型元素
	 * <p>
	 * 第一个是该Map的key列表，第二个是该Map的value列表
	 * 
	 * @param map
	 * @return size为2的List
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> List<List> mapToList(Map<K, V> map) {
		List<K> fields = new ArrayList<K>();
		List<V> values = new ArrayList<V>();

		Set<Entry<K, V>> set = map.entrySet();
		Iterator<Entry<K, V>> iter = set.iterator();
		while (iter.hasNext()) {
			Entry<K, V> entry = (Entry<K, V>) iter.next();
			fields.add((K) entry.getKey());
			values.add(entry.getValue());
		}
		List<List> result = new ArrayList<List>();
		result.add(fields);
		result.add(values);

		return result;
	}

	public static String newUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replace("-", "");
	}

	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ex) {
			// Cannot access thread context ClassLoader - falling back to system
			// class loader...
		}
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = CommonUtil.class.getClassLoader();
		}
		return cl;
	}
	
	public static String toAbsolutePath(String path) {
		if(Strings.isEmpty(path)) {
			return null;
		}
		File file = new File(path);
		if(!file.exists() || path.startsWith("classpath:")) {
			file = getClasspathFile(path);
			return file.getAbsolutePath();
		}
		return path;
	}

	public static File getClasspathFile(String classpath) {
		String input = classpath;
		if(classpath.startsWith("classpath:")) {
			input = classpath.substring(10);
		}
		URL url = getDefaultClassLoader().getResource(input);
		if (url == null || url.getProtocol().equals("file") == false) {
			return null;
		}
		return toFile(url);
	}

	public static File toFile(URL url) {
		if (url == null || !url.getProtocol().equals("file")) {
			return null;
		} else {
			String filename = url.getFile().replace('/', File.separatorChar);
			int pos = 0;
			while ((pos = filename.indexOf('%', pos)) >= 0) {
				if (pos + 2 < filename.length()) {
					String hexStr = filename.substring(pos + 1, pos + 3);
					char ch = (char) Integer.parseInt(hexStr, 16);
					filename = filename.substring(0, pos) + ch
							+ filename.substring(pos + 3);
				}
			}
			return new File(filename);
		}
	}

	public static void close(InputStream input) {
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
			}
		}
	}

	public static void close(OutputStream output) {
		if (output != null) {
			try {
				output.close();
			} catch (IOException e) {
			}
		}
	}

	public static boolean contains(Object[] array, Object element) {
		if (array == null) {
			return false;
		}
		for (int i = 0; i < array.length; i++) {
			if (nullSafeEquals(array[i], element)) {
				return true;
			}
		}
		return false;
	}

	public static boolean nullSafeEquals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		if (o1 == null || o2 == null) {
			return false;
		}
		if (o1.equals(o2)) {
			return true;
		}
		if (o1.getClass().isArray() && o2.getClass().isArray()) {
			if (o1 instanceof Object[] && o2 instanceof Object[]) {
				return Arrays.equals((Object[]) o1, (Object[]) o2);
			}
			if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
				return Arrays.equals((boolean[]) o1, (boolean[]) o2);
			}
			if (o1 instanceof byte[] && o2 instanceof byte[]) {
				return Arrays.equals((byte[]) o1, (byte[]) o2);
			}
			if (o1 instanceof char[] && o2 instanceof char[]) {
				return Arrays.equals((char[]) o1, (char[]) o2);
			}
			if (o1 instanceof double[] && o2 instanceof double[]) {
				return Arrays.equals((double[]) o1, (double[]) o2);
			}
			if (o1 instanceof float[] && o2 instanceof float[]) {
				return Arrays.equals((float[]) o1, (float[]) o2);
			}
			if (o1 instanceof int[] && o2 instanceof int[]) {
				return Arrays.equals((int[]) o1, (int[]) o2);
			}
			if (o1 instanceof long[] && o2 instanceof long[]) {
				return Arrays.equals((long[]) o1, (long[]) o2);
			}
			if (o1 instanceof short[] && o2 instanceof short[]) {
				return Arrays.equals((short[]) o1, (short[]) o2);
			}
		}
		return false;
	}

	public static int toInt(byte[] bytes) {
		int result = 0;
		for (int i = 0; i < 4; i++) {
			result = (result << 8) - Byte.MIN_VALUE + (int) bytes[i];
		}
		return result;
	}

	public static String md5(String text) {
		return md5(text.getBytes());
	}

	/**
	 * Make MD5 diaguest.
	 */
	public static String md5(byte[] data) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] buf = md.digest(data);
			return toHexString(buf);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private static String toHexString(byte[] bytes) {
		int length = bytes.length;
		StringBuffer sb = new StringBuffer(length * 2);
		int x = 0;
		int n1 = 0, n2 = 0;
		for (int i = 0; i < length; i++) {
			if (bytes[i] >= 0)
				x = bytes[i];
			else
				x = 256 + bytes[i];
			n1 = x >> 4;
			n2 = x & 0x0f;
			sb = sb.append(HEX[n1]);
			sb = sb.append(HEX[n2]);
		}
		return sb.toString();
	}

	/**
	 * 将一个给定的值转换为某个POJO的一个field的对应数据类型对象
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public static Object convert(Field field, Object value) {
		if (value == null) {
			return null;
		}
		Object result = null;
		String name = null;
		try {
			name = field.getType().getName();
		} catch (Exception e) {
			throw new RuntimeException("Failed to get field's type name.", e);
		}
		String stringVal = value.toString();
		if (name.equals("java.lang.Byte") || name.equals("byte")) {
			result = Byte.valueOf(stringVal);
		} else if (name.equals("java.lang.Short") || name.equals("short")) {
			result = Short.valueOf(stringVal);
		} else if (name.equals("java.lang.Integer") || name.equals("int")) {
			result = Integer.valueOf(stringVal);
		} else if (name.equals("java.lang.Boolean") || name.equals("boolean")) {
			result = Boolean.valueOf(stringVal);
		} else if (name.equals("java.lang.Character") || name.equals("char")) {
			result = new Character(stringVal.charAt(0));
		} else if (name.equals("java.lang.Double") || name.equals("double")) {
			result = Double.valueOf(stringVal);
		} else if (name.equals("java.lang.Float") || name.equals("float")) {
			result = Float.valueOf(stringVal);
		} else if (name.equals("java.lang.Long") || name.equals("long")) {
			result = Long.valueOf(stringVal);
		} else if (name.equals("java.lang.String")) {
			result = String.valueOf(stringVal);
		} else if (name.equals("java.util.Date")) {
			result = DateTime.toDate(value);
		} else if (name.equals("java.sql.Date")) {
			result = DateTime.toSqlDate(value);
		} else if (name.equals("java.sql.Timestamp")) {
			result = DateTime.toTimestamp(value);
		} else if (name.equals("java.sql.Time")) {
			result = DateTime.toTime(value);
		}
		return result;
	}

	public static void main(String[] args) {
		File file = new File("src/kk.txt");
		System.out.println(file.exists());
		
		System.out.println(int.class.getName() + "   " + Integer.class.getName());
		
		System.out.println(int.class == Integer.class);
	}
	
}
