/*
 * @(#)TestReflect.java	2011-9-15
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.basic;

import java.lang.reflect.Field;

/**
 * TestReflect
 * 
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: TestReflect.java 2011-9-15 下午02:08:12 Exp $
 */
public class TestReflect {
	public TestReflect() {
		super();
	}

	private boolean f;
	private byte b = 1;
	private char c;
	private int i;
	private short s;
	private long l;
	private float fl;
	private double d;

	private Byte ba;
	private Integer bb;

	public static void main(String[] args) throws SecurityException,
			NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
		Integer a = new Integer(13);
		TestReflect tr = new TestReflect();
		Class<?> clazz = tr.getClass();
		Field field = clazz.getDeclaredField("b");
		System.out.println(Integer.parseInt(field.get(tr).toString()));
		System.out.println(field.getType());
		System.out.println(field.getType() == Byte.class);
		System.out.println(field.getType() == byte.class);
		System.out.println(Boolean.TYPE);
		System.out.println(a.getClass().getName());
	}

	@Override
	public String toString() {
		return "TestReflect [b=" + b + ", ba=" + ba + ", bb=" + bb + ", c=" + c
				+ ", d=" + d + ", f=" + f + ", fl=" + fl + ", i=" + i + ", l="
				+ l + ", s=" + s + "]";
	}
	
	

}
