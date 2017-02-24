/*
 * @(#)SerializableProxy.java	2011-9-1
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.proxy.pojo.cglib;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.github.javaclub.jorm.JormException;

/**
 * SerializableProxy
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: SerializableProxy.java 2011-9-1 上午11:36:52 Exp $
 */
@SuppressWarnings("unchecked")
public final class SerializableProxy implements Serializable {

	private static final long serialVersionUID = 7165466670441854429L;
	
	private Class persistentClass;
	private Class[] interfaces;
	private Serializable id;
	private Class getIdentifierMethodClass;
	private Class setIdentifierMethodClass;
	private String getIdentifierMethodName;
	private String setIdentifierMethodName;
	private Class[] setIdentifierMethodParams;
	// private AbstractComponentType componentIdType;

	public SerializableProxy() {}

	public SerializableProxy(
		final Class persistentClass,
		final Class[] interfaces,
		final Serializable id,
		final Method getIdentifierMethod,
		final Method setIdentifierMethod
	) {
		this.persistentClass = persistentClass;
		this.interfaces = interfaces;
		this.id = id;
		if (getIdentifierMethod!=null) {
			getIdentifierMethodClass = getIdentifierMethod.getDeclaringClass();
			getIdentifierMethodName = getIdentifierMethod.getName();
		}
		if (setIdentifierMethod!=null) {
			setIdentifierMethodClass = setIdentifierMethod.getDeclaringClass();
			setIdentifierMethodName = setIdentifierMethod.getName();
			setIdentifierMethodParams = setIdentifierMethod.getParameterTypes();
		}
		// this.componentIdType = componentIdType;
	}

	private Object readResolve() {
		try {
			return CGLIBLazyInitializer.getProxy(
				persistentClass,
				interfaces,
				getIdentifierMethodName==null ?
					null :
					getIdentifierMethodClass.getDeclaredMethod(getIdentifierMethodName),
				setIdentifierMethodName==null ?
					null :
					setIdentifierMethodClass.getDeclaredMethod(setIdentifierMethodName, setIdentifierMethodParams),
				id,
				null
			);
		} catch (NoSuchMethodException nsme) {
			throw new JormException("could not create proxy for entity: " + persistentClass, nsme);
		}
	}
}
