/*
 * @(#)AnnotationUtil.java	2011-7-9
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * AnnotationUtil
 * 
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: AnnotationUtil.java 92 2011-07-09 16:24:52Z
 *          gerald.chen.hz@gmail.com $
 */
@SuppressWarnings("unchecked")
public abstract class Annotations {

	public static Annotation[] getAnnotations(Field field) {
		return field.getAnnotations();
	}

	public static <T extends Annotation> boolean hasAnnotation(Field field,
			Class<T> annCls) {
		T ann = field.getAnnotation(annCls);
		return ann != null;
	}

	public static <T extends Annotation> T getAnnotation(Field field,
			Class<T> annotationType) {
		return field.getAnnotation(annotationType);
	}

	public static Annotation[] getAnnotations(Method method) {
		return method.getAnnotations();
	}

	public static <T extends Annotation> boolean hasAnnotation(Method method,
			Class<T> annCls) {
		T ann = method.getAnnotation(annCls);
		return ann != null;
	}

	public static <T extends Annotation> T getAnnotation(Method method,
			Class<T> annotationType) {
		return method.getAnnotation(annotationType);
	}

	public static <T extends Annotation> T findAnnotation(Method method, Class<T> annotationType) {
		T annotation = getAnnotation(method, annotationType);
		Class cl = method.getDeclaringClass();
		do {
			if (annotation != null)
				break;
			cl = cl.getSuperclass();
			if (cl == null || cl.equals(Object.class))
				break;
			try {
				Method equivalentMethod = cl.getDeclaredMethod(
						method.getName(), method.getParameterTypes());
				annotation = getAnnotation(equivalentMethod, annotationType);
			} catch (NoSuchMethodException ex) {
			}
		} while (true);
		return annotation;
	}

	public static boolean hasAnnotation(Class clazz, Class annotationType) {
		return findAnnotation(clazz, annotationType) != null;
	}
	
	public static <T extends Annotation> T findAnnotation(Class clazz, Class<T> annotationType) {
		T annotation = (T) clazz.getAnnotation(annotationType);
		if (annotation != null)
			return annotation;
		Class clazzes[] = clazz.getInterfaces();
		int len = clazzes.length;
		for (int i = 0; i < len; i++) {
			Class ifc = clazzes[i];
			annotation = findAnnotation(ifc, annotationType);
			if (annotation != null)
				return annotation;
		}
		if (clazz.getSuperclass() == null
				|| Object.class.equals(clazz.getSuperclass()))
			return null;
		else
			return findAnnotation(clazz.getSuperclass(), annotationType);
	}

	/**
	 * 查找包含某种注解类型的Class类型的Class
	 * 
	 * @param annotationType
	 * @param clazz
	 * @return
	 */
	public static Class findAnnotationDeclaringClass(Class annotationType,
			Class clazz) {
		if (clazz == null || clazz.equals(Object.class))
			return null;
		else
			return isAnnotationDeclaredLocally(annotationType, clazz) ? clazz
					: findAnnotationDeclaringClass(annotationType, clazz
							.getSuperclass());
	}

	/**
	 * 检查一个类是否包含一个特定的注解类型
	 * 
	 * @param annotationType
	 * @param clazz
	 * @return
	 */
	public static boolean isAnnotationDeclaredLocally(Class annotationType,
			Class clazz) {
		boolean declaredLocally = false;
		Iterator iterator = Arrays.asList(clazz.getDeclaredAnnotations())
				.iterator();
		do {
			if (!iterator.hasNext())
				break;
			Annotation annotation = (Annotation) iterator.next();
			if (!annotation.annotationType().equals(annotationType))
				continue;
			declaredLocally = true;
			break;
		} while (true);
		return declaredLocally;
	}

	public static boolean isAnnotationInherited(Class annotationType,
			Class clazz) {
		return clazz.isAnnotationPresent(annotationType)
				&& !isAnnotationDeclaredLocally(annotationType, clazz);
	}

	/**
	 * 获取注解所有的属性
	 * 
	 * @param annotation
	 * @return
	 */
	public static Map getAnnotationAttributes(Annotation annotation) {
		Map attrs = new HashMap();
		Method methods[] = annotation.annotationType().getDeclaredMethods();
		for (int j = 0; j < methods.length; j++) {
			Method method = methods[j];
			if (method.getParameterTypes().length != 0
					|| method.getReturnType() == Void.TYPE)
				continue;
			try {
				attrs.put(method.getName(), method.invoke(annotation,
						new Object[0]));
			} catch (Exception ex) {
				throw new IllegalStateException(
						"Could not obtain annotation attribute values", ex);
			}
		}

		return attrs;
	}

	public static Object getValue(Annotation annotation) {
		return getValue(annotation, "value");
	}

	/**
	 * 获取注解对应的属性值
	 * 
	 * @param annotation
	 * @param attributeName
	 * @return
	 */
	public static Object getValue(Annotation annotation, String attributeName) {
		try {
			Method method = annotation.annotationType().getDeclaredMethod(
					attributeName, new Class[0]);
			return method.invoke(annotation, new Object[0]);
		} catch (Exception ex) {
			return null;
		}
	}

	public static Object getDefaultValue(Annotation annotation) {
		return getDefaultValue(annotation, "value");
	}

	public static Object getDefaultValue(Annotation annotation,
			String attributeName) {
		return getDefaultValue(annotation.annotationType(), attributeName);
	}

	public static Object getDefaultValue(Class annotationType) {
		return getDefaultValue(annotationType, "value");
	}

	public static Object getDefaultValue(Class annotationType,
			String attributeName) {
		try {
			Method method = annotationType.getDeclaredMethod(attributeName,
					new Class[0]);
			return method.getDefaultValue();
		} catch (Exception ex) {
			return null;
		}
	}

	static final String VALUE = "value";
}
