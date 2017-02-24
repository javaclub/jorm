/*
 * @(#)CGLIBLazyInitializer.java	2011-9-1
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.proxy.pojo.cglib;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.NoOp;

import org.apache.commons.logging.LogFactory;
import com.github.javaclub.jorm.JormException;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.common.Reflections;
import com.github.javaclub.jorm.proxy.JormProxy;
import com.github.javaclub.jorm.proxy.LazyInitializationException;
import com.github.javaclub.jorm.proxy.pojo.BasicLazyInitializer;

/**
 * A <tt>LazyInitializer</tt> implemented using the CGLIB bytecode generation library.
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: CGLIBLazyInitializer.java 2011-9-1 上午11:14:09 Exp $
 */
@SuppressWarnings("unchecked")
public class CGLIBLazyInitializer extends BasicLazyInitializer implements
		InvocationHandler {
	
	private static final CallbackFilter FINALIZE_FILTER = new CallbackFilter() {
		public int accept(Method method) {
			if ( method.getParameterTypes().length == 0 && method.getName().equals("finalize") ){
				return 1;
			} else {
				return 0;
			}
		}
	};
	
	private Class[] interfaces;
	private boolean constructed = false;
	
	private CGLIBLazyInitializer(final Class persistentClass,
			final Class[] interfaces, final Serializable id, final Method getIdentifierMethod,
			final Method setIdentifierMethod, 
			final Session session) {
		super(persistentClass, id, getIdentifierMethod, setIdentifierMethod, session);
		this.interfaces = interfaces;
	}

	protected Object serializableProxy() {
		return new SerializableProxy(
				persistentClass,
				interfaces,
				getIdentifier(),
				getIdentifierMethod,
				setIdentifierMethod
			);
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if ( constructed ) {
			Object result = invoke( method, args, proxy );
			if ( result == INVOKE_IMPLEMENTATION ) {
				Object target = getImplementation();
				if(null == target) {
					// 可能已经被删除了，或数据库中不存在
					return null;
					// throw new IllegalStateException("The database record does not exist or it had been deleted.");
				}
				try {
					final Object returnValue;
					if ( Reflections.isPublic( persistentClass, method ) ) {
						if ( !method.getDeclaringClass().isInstance( target ) ) {
							throw new ClassCastException( target.getClass().getName() );
						}
						returnValue = method.invoke( target, args );
					} else {
						if ( !method.isAccessible() ) {
							method.setAccessible( true );
						}
						returnValue = method.invoke( target, args );
					}
					return returnValue == target ? proxy : returnValue;
				} catch ( InvocationTargetException ite ) {
					throw ite.getTargetException();
				}
			} else {
				return result;
			}
		} else {
			// while constructor is running
			if ( method.getName().equals( "getLazyInitializer" ) ) {
				return this;
			} else {
				throw new LazyInitializationException( "unexpected case hit, method=" + method.getName() );
			}
		}
	}
	
	static JormProxy getProxy(final Class persistentClass,
			final Class[] interfaces, final Method getIdentifierMethod,
			final Method setIdentifierMethod, final Serializable id, 
			final Session session) throws JormException {
		// note: interfaces is assumed to already contain HibernateProxy.class
		try {
			final CGLIBLazyInitializer instance = new CGLIBLazyInitializer(
					persistentClass,
					interfaces,
					id,
					getIdentifierMethod,
					setIdentifierMethod,
					session
				);

			final JormProxy proxy;
			Class factory = getProxyFactory(persistentClass,  interfaces);
			proxy = getProxyInstance(factory, instance);
			instance.constructed = true;
			return proxy;
		} catch (Throwable t) {
			LogFactory.getLog( BasicLazyInitializer.class )
				.error( "CGLIB Enhancement failed: " + persistentClass, t );
			throw new JormException( "CGLIB Enhancement failed: " + persistentClass, t );
		}
	}

	public static JormProxy getProxy(final Class factory, 
			final Class persistentClass, final Class[] interfaces,
			final Method getIdentifierMethod, final Method setIdentifierMethod,
			final Serializable id, final Session session) throws JormException {

		final CGLIBLazyInitializer instance = new CGLIBLazyInitializer(
				persistentClass,
				interfaces,
				id,
				getIdentifierMethod,
				setIdentifierMethod,
				session
			);

		final JormProxy proxy;
		try {
			proxy = getProxyInstance(factory, instance);
			/*if(null != proxy) {
				Reflections.invokeMethod(proxy, setIdentifierMethod, id);
			}*/
		} catch (Exception e) {
			throw new JormException( "CGLIB Enhancement failed: " + persistentClass.getName(), e );
		}
		instance.constructed = true;

		return proxy;
	}

    private static JormProxy getProxyInstance(Class factory, CGLIBLazyInitializer instance) throws InstantiationException, IllegalAccessException {
    	JormProxy proxy;
		try {
			Enhancer.registerCallbacks(factory, new Callback[]{ instance, null });
			proxy = (JormProxy) factory.newInstance();
		} finally {
			// make sure the callback gets cleared, otherwise the instance stays in a static thread local.
			Enhancer.registerCallbacks(factory, null);
		}
		return proxy;
	}

	public static Class getProxyFactory(Class persistentClass, Class[] interfaces)
			throws JormException {
		Enhancer e = new Enhancer();
		e.setSuperclass( interfaces.length == 1 ? persistentClass : null );
		e.setInterfaces(interfaces);
		e.setCallbackTypes(new Class[]{
			InvocationHandler.class,
			NoOp.class,
	  	});
  		e.setCallbackFilter(FINALIZE_FILTER);
  		e.setUseFactory(false);
		e.setInterceptDuringConstruction( false );
		return e.createClass();
	}

}
