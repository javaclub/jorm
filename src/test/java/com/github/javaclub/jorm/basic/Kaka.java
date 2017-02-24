/*
 * @(#)Kaka.java	2011-9-19
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.basic;

/**
 * Kaka
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Kaka.java 2011-9-19 下午02:41:24 Exp $
 */
public class Kaka {

	protected int size = -1;

	public Kaka() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected final int getSize() {
		return size;
	}
	
	public static void main(String[] args) {
		Kaka ka = new Kaka();
		ka.size = 8;
		System.out.println(ka.getSize());
	}
}
