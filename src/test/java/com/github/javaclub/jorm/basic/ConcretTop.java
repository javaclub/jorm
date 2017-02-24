/*
 * @(#)ConcretTop.java	2011-8-31
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.basic;


/**
 * ConcretTop
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: ConcretTop.java 2011-8-31 下午02:08:56 Exp $
 */
public class ConcretTop extends AbstractTop {
	

	public ConcretTop() {
		super();
	}

	public boolean supportLimit() {
		System.out.println("ConcretTop --> supportLimit() --> " + true);
		return true;
	}
	
	
}
