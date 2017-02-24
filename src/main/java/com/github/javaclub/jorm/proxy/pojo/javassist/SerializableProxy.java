package com.github.javaclub.jorm.proxy.pojo.javassist;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.github.javaclub.jorm.JormException;

/**
 * Serializable placeholder for Javassist proxies
 */
@SuppressWarnings("unchecked")
public final class SerializableProxy implements Serializable {

	private static final long serialVersionUID = 8797411809196869842L;
	
	private Class persistentClass;
	private Class[] interfaces;
	private Serializable id;
	private Class getIdentifierMethodClass;
	private Class setIdentifierMethodClass;
	private String getIdentifierMethodName;
	private String setIdentifierMethodName;
	private Class[] setIdentifierMethodParams;

	public SerializableProxy() {}

	public SerializableProxy(
	    final Class persistentClass,
	    final Class[] interfaces,
	    final Serializable id,
	    final Method getIdentifierMethod,
	    final Method setIdentifierMethod) {
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
	}

	private Object readResolve() {
		try {
			return JavassistLazyInitializer.getProxy(
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
			throw new JormException("could not create proxy for entity: " + persistentClass.getName(), nsme);
		}
	}

}
