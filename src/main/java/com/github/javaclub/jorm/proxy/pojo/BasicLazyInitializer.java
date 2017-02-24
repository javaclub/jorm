/*
 * @(#)BasicLazyInitializer.java	2011-9-1
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.proxy.pojo;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.common.DummyObject;
import com.github.javaclub.jorm.common.Reflections;
import com.github.javaclub.jorm.proxy.AbstractLazyInitializer;

/**
 * BasicLazyInitializer
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: BasicLazyInitializer.java 2011-9-1 上午10:36:31 Exp $
 */
public abstract class BasicLazyInitializer extends AbstractLazyInitializer {
	
	protected static final Object INVOKE_IMPLEMENTATION = new DummyObject("INVOKE_IMPLEMENTATION");
	
	protected Class<?> persistentClass;
	protected Method getIdentifierMethod;
	protected Method setIdentifierMethod;
	protected boolean overridesEquals;
	private Object replacement;

	protected BasicLazyInitializer(Class<?> persistentClass, Serializable id,
			Method getIdentifierMethod, Method setIdentifierMethod,
			Session session) {
		super(id, session);
		this.persistentClass = persistentClass;
		this.getIdentifierMethod = getIdentifierMethod;
		this.setIdentifierMethod = setIdentifierMethod;
		overridesEquals = Reflections.overridesEquals(persistentClass);
	}

	protected abstract Object serializableProxy();

	public Class<?> getPersistentClass() {
		return persistentClass;
	}
	
	protected final Object invoke(Method method, Object[] args, Object proxy) throws Throwable {

		String methodName = method.getName();
		int params = args.length;
		if ( params==0 ) {
			if ( "writeReplace".equals(methodName) ) {
				return getReplacement();
			} else if ( !overridesEquals && "hashCode".equals(methodName) ) {
				return new Integer( System.identityHashCode(proxy) );
			} else if ( isUninitialized() && method.equals(getIdentifierMethod) ) {
				return getIdentifier();
			} else if ( "getLazyInitializer".equals(methodName) ) {
				return this;
			}
		} else if ( params==1 ) {
			if ( !overridesEquals && "equals".equals(methodName) ) {
				return args[0] == proxy ? Boolean.TRUE : Boolean.FALSE;
			} else if ( method.equals(setIdentifierMethod) ) {
				initialize();
				setIdentifier( (Serializable) args[0] );
				return INVOKE_IMPLEMENTATION;
			}
		}

		//if it is a property of an embedded component, invoke on the "identifier"
		/*if ( componentIdType!=null && componentIdType.isMethodOf(method) ) {
			return method.invoke( getIdentifier(), args );
		}*/

		// otherwise:
		return INVOKE_IMPLEMENTATION;

	}

	private Object getReplacement() {
		final Session session = getSession();
		if ( isUninitialized() && session != null && !session.isClosed()) {
			/*final EntityKey key = new EntityKey(
					getIdentifier(),
			        session.getFactory().getEntityPersister( getEntityName() ),
			        session.getEntityMode()
				);*/
			final Object entity = session.read(getPersistentClass(), getIdentifier());
			if (entity!=null) setImplementation( entity );
		}
		if ( isUninitialized() ) {
			if (replacement==null) {
				replacement = serializableProxy();
			}
			return replacement;
		} else {
			return getTarget();
		}
	}

}
