/*
 * @(#)Reflections.java	2010-4-2
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.common;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.javaclub.jorm.FailureLog;
import com.github.javaclub.jorm.common.javabean.JavaTypeHandlers;
import com.github.javaclub.jorm.jdbc.JdbcException;

/**
 * 反射工具类.
 * 
 * 提供访问私有变量,获取泛型类型Class,提取集合中元素的属性,转换字符串到对象等工具方法.
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: Reflections.java 131 2011-07-19 10:54:36Z gerald.chen.hz@gmail.com $
 */
@SuppressWarnings("unchecked")
public class Reflections {
	
	private static Log LOG = LogFactory.getLog(Reflections.class);
	
	private static Map cache = new HashMap();
	
	public static final Class[] NO_PARAM_SIGNATURE = new Class[0];
	public static final Class[] SINGLE_OBJECT_PARAM_SIGNATURE = new Class[] { Object.class };
	
	private static final Method OBJECT_EQUALS;
	private static final Method OBJECT_HASHCODE;

	static {
		Method eq;
		Method hash;
		try {
			eq = extractEqualsMethod( Object.class );
			hash = extractHashCodeMethod( Object.class );
		} catch ( Exception e ) {
			throw new FailureLog( "Could not find Object.equals() or Object.hashCode()", e );
		}
		OBJECT_EQUALS = eq;
		OBJECT_HASHCODE = hash;
	}

	/**
     * This method performs in a case-sensitive manner
     * <p>
     * It will not contain list or array properties.
     *
     * @param target a entity bean
     * @return Map Map<property, propertyValue>
     */
	public static Map describe(Object target) {
		Map retMap = new HashMap();
        List retList = new LinkedList();
        Method[] methods = (Method[]) cache.get(target.getClass());
        if (methods == null) {
            methods = target.getClass().getMethods();
            cache.put(target.getClass(), methods);
        }

        for (int i = 0; i < methods.length; i++) {
            String method = methods[i].getName();

            // to remove arrays and Lists from POJOs
            if (methods[i].getReturnType().isArray()
                    || methods[i].getReturnType().equals(List.class)
                    || (methods[i].getParameterTypes().length == 1 && methods[i]
                    .getParameterTypes()[0].equals(List.class))) {
            	if(LOG.isDebugEnabled()) {
            		LOG.debug("Removing a List or Array field! " + method);
            	}
                continue;
            }

            if (method.indexOf("set") == 0 || method.indexOf("get") == 0) {
                String name = method.substring(3, method.length());
                if(name.equalsIgnoreCase("class")) {
                	continue;
                }
				retList.add(name);
            } else if (method.indexOf("is") == 0) {// added boolean property support.
                retList.add(method.substring(2, method.length()));
            }
        }
        Collections.sort(retList);
        Object[] props = retList.toArray();
        retList.clear();
		for (int i = 0; i < props.length - 1; i++) {
			String prop = decapitalize(props[i].toString());
			retMap.put(prop, getFieldValue(target, prop));
		}
        retList = null;
        return retMap;
    }
	
	public static Method getMethod(Class target, Field field, MethodType mt) {
		String fieldName = field.getName();
		Class  fieldType = field.getType();
		Method method;
		if(mt == MethodType.GET) {
			String getMethodName = "get" + Strings.capitalize(fieldName);
			method = getDeclaredMethod(target, getMethodName, new Class[] {});
			if(null == method) {
				if(fieldName.startsWith("is")) {
					method = getDeclaredMethod(target, "get" + Strings.capitalize(fieldName.substring(2)), new Class[] {});
				}
			}
		} else if(mt == MethodType.SET) {
			String setMethodName = "set" + Strings.capitalize(fieldName);
			method = getDeclaredMethod(target, setMethodName, new Class[] { fieldType });
			if(null == method) {
				if(fieldName.startsWith("is")) {
					method = getDeclaredMethod(target, "set" + Strings.capitalize(fieldName.substring(2)), new Class[] { fieldType });
				}
			}
		} else {
			throw new IllegalArgumentException("Unsupported MethodType => " + mt);
		}
		return method;
	}

	/**
	 * 调用Getter方法.
	 */
	public static Object invokeGetterMethod(Object target, String propertyName) {
		String getterMethodName = "get" + Strings.capitalize(propertyName);
		return invokeMethod(target, getterMethodName, new Class[] {}, new Object[] {});
	}

	/**
	 * 调用Setter方法.使用value的Class来查找Setter方法.
	 */
	public static void invokeSetterMethod(Object target, String propertyName, Object value) {
		invokeSetterMethod(target, propertyName, value, null);
	}

	/**
	 * 调用Setter方法.
	 * 
	 * @propertyType 用于查找Setter方法,为空时使用value的Class替代.
	 */
	public static void invokeSetterMethod(Object target, String propertyName, Object value, Class<?> propertyType) {
		Class<?> type = propertyType != null ? propertyType : value.getClass();
		String setterMethodName = "set" + Strings.capitalize(propertyName);
		invokeMethod(target, setterMethodName, new Class[] { type }, new Object[] { value });
	}
	
	/**
	 * 直接调用对象方法, 无视private/protected修饰符.
	 */
	public static Object invokeMethod(final Object object, final String methodName, final Class<?>[] parameterTypes,
			final Object[] parameters) {
		Method method = getDeclaredMethod(object, methodName, parameterTypes);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + object + "]");
		}

		method.setAccessible(true);

		try {
			return method.invoke(object, parameters);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}
	
	public static Object invokeMethod(Object obj, Method method, Object... parameters) {
		AssertUtil.notNull("The method's parameter can't be null.", obj, method);
		method.setAccessible(true);
		try {
			return method.invoke(obj, parameters);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}
	
	/**
	 * 循环向上转型, 获取对象的DeclaredField.
	 * 
	 * 如向上转型到Object仍无法找到, 返回null.
	 */
	public static Field getField(final Class<?> objectClass, final String fieldName) {
		AssertUtil.notNull(objectClass, "object不能为空");
		AssertUtil.hasText(fieldName, "fieldName");
		for (Class<?> superClass = objectClass; superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				return superClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				// Field不在当前类定义,继续向上转型
			}
		}
		return null;
	}


	/**
	 * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
	 */
	public static Object getFieldValue(final Object object, final String fieldName) {
		Field field = getDeclaredField(object, fieldName);

		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
		}

		makeAccessible(field);

		Object result = null;
		try {
			result = field.get(object);
		} catch (IllegalAccessException e) {
			LOG.error("不可能抛出的异常: ", e);
		}
		return result;
	}
	
	/**
	 * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
	 */
	public static Object getFieldValue(final Object object, Field field) {
		if (field == null) {
			throw new IllegalArgumentException("Could not find field on target [" + object + "]");
		}

		makeAccessible(field);

		Object result = null;
		try {
			result = field.get(object);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			LOG.error("Throwed Unexpected exception while getting field value.", e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			LOG.error("Throwed Unexpected exception while getting field value.", e);
		}

		return result;
	}
	
	/**
	 * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
	 */
	public static void setFieldValue(final Object object, final String fieldName, final Object value) {
		Field field = getDeclaredField(object, fieldName);

		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
		}

		makeAccessible(field);

		try {
			field.set(object, value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			LOG.error("Throwed Unexpected exception while setting field value.", e);
		} catch (IllegalAccessException e) {
			LOG.error("Throwed Unexpected exception while setting field value.", e);
			e.printStackTrace();
		}
		
	}
	
	public static void setFieldValue(final Object object, final Field field, final Object value) {
		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + field + "] on target [" + object + "]");
		}
		makeAccessible(field);
		
		try {
			field.set(object, value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			LOG.error("Throwed Unexpected exception while setting field value.", e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			LOG.error("Throwed Unexpected exception while setting field value.", e);
		}
		
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredField.
	 * 
	 * 如向上转型到Object仍无法找到, 返回null.
	 */
	protected static Field getDeclaredField(final Object object, final String fieldName) {
		AssertUtil.notNull(object, "object不能为空");
		AssertUtil.hasText(fieldName, "fieldName");
		for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				return superClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				// Field不在当前类定义,继续向上转型
			}
		}
		return null;
	}
	
	/**
	 * 强行设置Field可访问.
	 */
	protected static void makeAccessible(final Field field) {
		if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
			field.setAccessible(true);
		}
	}

	/**
	 * 循环向上转型,获取对象的DeclaredMethod.
	 * 
	 * 如向上转型到Object仍无法找到, 返回null.
	 */
	protected static Method getDeclaredMethod(Object object, String methodName, Class<?>[] parameterTypes) {
		AssertUtil.notNull(object, "object can't be null.");

		for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				return superClass.getDeclaredMethod(methodName, parameterTypes);
			} catch (NoSuchMethodException e) {
				// Method不在当前类定义,继续向上转型
			}
		}
		return null;
	}
	
	public static Method getDeclaredMethod(Class<?> clazz, String methodName,
			Class<?>[] parameterTypes) {
		AssertUtil.notNull(clazz, "The object class can't be null.");
		AssertUtil.notNull(clazz, "The methodName is null.");
		for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				return superClass.getDeclaredMethod(methodName, parameterTypes);
			} catch (NoSuchMethodException e) {
				// Method不在当前类定义,继续向上转型
			}
		}
		return null;
	}

	/**
	 * 通过反射,获得Class定义中声明的父类的泛型参数的类型.
	 * 如无法找到, 返回Object.class.
	 * eg.
	 * public UserDao extends HibernateDao<User>
	 *
	 * @param clazz The class to introspect
	 * @return the first generic declaration, or Object.class if cannot be determined
	 */
	public static <T> Class<T> getSuperClassGenricType(final Class clazz) {
		return getSuperClassGenricType(clazz, 0);
	}

	/**
	 * 通过反射,获得定义Class时声明的父类的泛型参数的类型.
	 * 如无法找到, 返回Object.class.
	 * 
	 * 如public UserDao extends HibernateDao<User,Long>
	 *
	 * @param clazz clazz The class to introspect
	 * @param index the Index of the generic ddeclaration,start from 0.
	 * @return the index generic declaration, or Object.class if cannot be determined
	 */
	public static Class getSuperClassGenricType(final Class clazz, final int index) {

		Type genType = clazz.getGenericSuperclass();

		if (!(genType instanceof ParameterizedType)) {
			LOG.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
			return Object.class;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			LOG.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
					+ params.length);
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			LOG.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
			return Object.class;
		}

		return (Class) params[index];
	}

	/**
	 * 将反射时的checked exception转换为unchecked exception.
	 */
	public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
		if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
				|| e instanceof NoSuchMethodException) {
			return new IllegalArgumentException("Reflection Exception.", e);
		} else if (e instanceof InvocationTargetException) {
			return new RuntimeException("Reflection Exception.", ((InvocationTargetException) e).getTargetException());
		} else if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		}
		return new RuntimeException("Unexpected Checked Exception.", e);
	}
	
	/**
	 * 检测属性是否存在
	 * 
	 * @param obj Object
	 * @param propertyName String
	 * @return boolean
	 */
	public static boolean hasProperty(Object obj, String propertyName) {
		if (obj == null) {
			return false;
		}
		if (propertyName == null || propertyName.equals("")) {
			return false;
		}

		try {
			Class objClass = obj.getClass();
			String propertyNameA = null;
			if (propertyName.length() > 1) {
				propertyNameA = propertyName.substring(0, 1).toUpperCase()
						+ propertyName.substring(1);
			} else {
				propertyNameA = propertyName.toUpperCase();
			}

			String methodName = null;
			Method m = null;

			try {
				methodName = "get" + propertyNameA;
				m = objClass.getMethod(methodName);
			} catch (Exception ex) {
			}

			if (m == null) {
				try {
					methodName = "is" + propertyNameA;
					m = objClass.getMethod(methodName);
				} catch (Exception ex) {
				}
			}

			if (m == null) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 循环向上获得所有的属性,包括私有属性,不包括 属性[class].
	 */
	public static <T> Field[] getFields(Class<T> clazz) {
		Class<?> theClass = clazz;
		Map<String, Field> list = new HashMap<String, Field>();
		while (null != theClass && !(theClass == Object.class)) {
			Field[] fs = theClass.getDeclaredFields();
			for (int i = 0; i < fs.length; i++) {
				if (isIgnoredField(fs[i]))
					continue;
				if (list.containsKey(fs[i].getName()))
					continue;
				list.put(fs[i].getName(), fs[i]);
			}
			theClass = theClass.getSuperclass();
		}
		return list.values().toArray(new Field[list.size()]);
	}
	
	/**
	 * 获得所有的JDBC支持的数据类型属性,包括私有属性,不包括 属性[class].
	 */
	public static <T> Field[] getJavaTypeFields(Class<T> clazz) {
		Class<?> theClass = clazz;
		Map<String, Field> list = new HashMap<String, Field>();
		while (null != theClass && !(theClass == Object.class)) {
			Field[] fs = theClass.getDeclaredFields();
			for (int i = 0; i < fs.length; i++) {
				if (isIgnoredField(fs[i])) {
					continue;
				}
				if (list.containsKey(fs[i].getName())) {
					continue;
				}
				if(!JavaTypeHandlers.COMMON_DATA_TYPE_HANDLERS.containsKey(fs[i].getType().getName())) {
					continue;
				}
				list.put(fs[i].getName(), fs[i]);
			}
			theClass = theClass.getSuperclass();
		}
		return list.values().toArray(new Field[list.size()]);
	}
	
	public static boolean isStatic(Field field) {
		return Modifier.isStatic(field.getModifiers());
	}
	
	public static boolean isFinal(Field field) {
		return Modifier.isFinal(field.getModifiers());
	}
	
	public static boolean isPrimitive(Field field) {
		return (field.getType() == int.class || field.getType() == short.class 
			|| field.getType() == byte.class || field.getType() == float.class 
			|| field.getType() == double.class || field.getType() == char.class 
			|| field.getType() == boolean.class || field.getType() == long.class);
	}
	
	public static Object newInstance(String className)
			throws ClassNotFoundException, IllegalAccessException,
			InstantiationException {
		Object object = null;
		try {
			object = (Object) Class.forName(className).newInstance();
		} catch (ClassNotFoundException e) {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			object = (Object) cl.loadClass(className).newInstance();
		}
		return object;
	}
	
	public static <T> T newInstance(Class<T> c) throws JdbcException {
        try {
            return c.newInstance();

        } catch (InstantiationException e) {
            throw new JdbcException(
                "Cannot create " + c.getName() + ": " + e.getMessage());

        } catch (IllegalAccessException e) {
            throw new JdbcException(
                "Cannot create " + c.getName() + ": " + e.getMessage());
        }
    }
	
	public static Class classForName(String name) throws ClassNotFoundException {
		try {
			ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
			if ( contextClassLoader != null ) {
				return contextClassLoader.loadClass(name);
			}
		} catch ( Throwable ignore ) {
		}
		return Class.forName( name );
	}
	
	/**
	 * Encapsulation of getting hold of a class's {@link Object#equals equals}  method.
	 *
	 * @param clazz The class from which to extract the equals method.
	 * @return The equals method reference
	 * @throws NoSuchMethodException Should indicate an attempt to extract equals method from interface.
	 */
	public static Method extractEqualsMethod(Class clazz) throws NoSuchMethodException {
		return clazz.getMethod( "equals", SINGLE_OBJECT_PARAM_SIGNATURE );
	}
	
	/**
	 * Encapsulation of getting hold of a class's {@link Object#hashCode hashCode} method.
	 *
	 * @param clazz The class from which to extract the hashCode method.
	 * @return The hashCode method reference
	 * @throws NoSuchMethodException Should indicate an attempt to extract hashCode method from interface.
	 */
	public static Method extractHashCodeMethod(Class clazz) throws NoSuchMethodException {
		return clazz.getMethod( "hashCode", NO_PARAM_SIGNATURE );
	}
	
	/**
	 * Determine if the given class defines a {@link Object#hashCode} override.
	 *
	 * @param clazz The class to check
	 * @return True if clazz defines an hashCode override.
	 */
	public static boolean overridesHashCode(Class clazz) {
		Method hashCode;
		try {
			hashCode = extractHashCodeMethod( clazz );
		}
		catch ( NoSuchMethodException nsme ) {
			return false; //its an interface so we can't really tell anything...
		}
		return !OBJECT_HASHCODE.equals( hashCode );
	}

	/**
	 * Determine if the given class implements the given interface.
	 *
	 * @param clazz The class to check
	 * @param intf The interface to check it against.
	 * @return True if the class does implement the interface, false otherwise.
	 */
	public static boolean implementsInterface(Class clazz, Class intf) {
		assert intf.isInterface() : "Interface to check was not an interface";
		return intf.isAssignableFrom( clazz );
	}
	
	/**
	 * Determine if the given class defines an {@link Object#equals} override.
	 *
	 * @param clazz The class to check
	 * @return True if clazz defines an equals override.
	 */
	public static boolean overridesEquals(Class clazz) {
		Method equals;
		try {
			equals = extractEqualsMethod( clazz );
		} catch ( NoSuchMethodException nsme ) {
			return false; //its an interface so we can't really tell anything...
		}
		return !OBJECT_EQUALS.equals( equals );
	}
	
	/**
	 * Is this member publicly accessible.
	 *
	 * @param clazz The class which defines the member
	 * @param member The memeber.
	 * @return True if the member is publicly accessible, false otherwise.
	 */
	public static boolean isPublic(Class clazz, Member member) {
		return Modifier.isPublic( member.getModifiers() ) && Modifier.isPublic( clazz.getModifiers() );
	}

	private static boolean isIgnoredField(Field f) {
		if (Modifier.isStatic(f.getModifiers()))
			return true;
		if (Modifier.isFinal(f.getModifiers()))
			return true;
		if (f.getName().startsWith("this$"))
			return true;
		return false;
	}
	
	private static String decapitalize(String fieldName) {
        return Introspector.decapitalize(fieldName);
    }
	
}
