package com.github.javaclub.jorm.proxy.pojo.javassist;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import org.apache.commons.logging.LogFactory;
import com.github.javaclub.jorm.JormException;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.common.Reflections;
import com.github.javaclub.jorm.proxy.JormProxy;
import com.github.javaclub.jorm.proxy.pojo.BasicLazyInitializer;

/**
 * A Javassist-based lazy initializer proxy.
 *
 * @author Muga Nishizawa
 */
@SuppressWarnings("unchecked")
public class JavassistLazyInitializer extends BasicLazyInitializer implements MethodHandler {

	private static final MethodFilter FINALIZE_FILTER = new MethodFilter() {
		public boolean isHandled(Method m) {
			// skip finalize methods
			return !( m.getParameterTypes().length == 0 && m.getName().equals( "finalize" ) );
		}
	};

	private Class[] interfaces;
	private boolean constructed = false;

	private JavassistLazyInitializer(
	        final Class persistentClass,
	        final Class[] interfaces,
	        final Serializable id,
	        final Method getIdentifierMethod,
	        final Method setIdentifierMethod,
	        final Session session) {
		super( persistentClass, id, getIdentifierMethod, setIdentifierMethod, session );
		this.interfaces = interfaces;
	}

	public static JormProxy getProxy(
	        final Class persistentClass,
	        final Class[] interfaces,
	        final Method getIdentifierMethod,
	        final Method setIdentifierMethod,
	        final Serializable id,
	        final Session session) throws JormException {
		// note: interface is assumed to already contain HibernateProxy.class
		try {
			final JavassistLazyInitializer instance = new JavassistLazyInitializer(
			        persistentClass,
			        interfaces,
			        id,
			        getIdentifierMethod,
			        setIdentifierMethod,
			        session
			);
			ProxyFactory factory = new ProxyFactory();
			factory.setSuperclass( interfaces.length == 1 ? persistentClass : null );
			factory.setInterfaces( interfaces );
			factory.setFilter( FINALIZE_FILTER );
			Class cl = factory.createClass();
			final JormProxy proxy = ( JormProxy ) cl.newInstance();
			( ( ProxyObject ) proxy ).setHandler( instance );
			instance.constructed = true;
			return proxy;
		}
		catch ( Throwable t ) {
			LogFactory.getLog( BasicLazyInitializer.class ).error(
					"Javassist Enhancement failed: " + persistentClass, t);
			throw new JormException(
					"Javassist Enhancement failed: "
					+ persistentClass, t);
		}
	}

	public static JormProxy getProxy(
			final Class factory,
	        final Class persistentClass,
	        final Class[] interfaces,
	        final Method getIdentifierMethod,
	        final Method setIdentifierMethod,
	        final Serializable id,
	        final Session session) throws JormException {

		final JavassistLazyInitializer instance = new JavassistLazyInitializer(
		        persistentClass,
		        interfaces, id,
		        getIdentifierMethod,
		        setIdentifierMethod,
		        session
		);

		final JormProxy proxy;
		try {
			proxy = ( JormProxy ) factory.newInstance();
			if(null != proxy) {
				Reflections.invokeMethod(proxy, setIdentifierMethod, id);
			}
		}
		catch ( Exception e ) {
			throw new JormException(
					"Javassist Enhancement failed: "
					+ persistentClass.getName(), e
			);
		}
		( ( ProxyObject ) proxy ).setHandler( instance );
		instance.constructed = true;
		return proxy;
	}

	public static Class getProxyFactory(
			Class persistentClass,
	        Class[] interfaces) throws JormException {
		// note: interfaces is assumed to already contain HibernateProxy.class

		try {
			ProxyFactory factory = new ProxyFactory();
			factory.setSuperclass( interfaces.length == 1 ? persistentClass : null );
			factory.setInterfaces( interfaces );
			factory.setFilter( FINALIZE_FILTER );
			return factory.createClass();
		}
		catch ( Throwable t ) {
			LogFactory.getLog( BasicLazyInitializer.class ).error(
					"Javassist Enhancement failed: "
					+ persistentClass.getName(), t
			);
			throw new JormException(
					"Javassist Enhancement failed: "
					+ persistentClass.getName(), t
			);
		}
	}

	public Object invoke(
			final Object proxy,
			final Method thisMethod,
			final Method proceed,
			final Object[] args) throws Throwable {
		if ( this.constructed ) {
			Object result;
			try {
				result = this.invoke( thisMethod, args, proxy );
			}
			catch ( Throwable t ) {
				throw new Exception( t.getCause() );
			}
			if ( result == INVOKE_IMPLEMENTATION ) {
				Object target = getImplementation();
				if(null == target) {
					// 可能已经被删除了，或数据库中不存在
					return null;
					// throw new IllegalStateException("The database record does not exist or it had been deleted.");
				}
				final Object returnValue;
				try {
					if ( Reflections.isPublic( persistentClass, thisMethod ) ) {
						if ( !thisMethod.getDeclaringClass().isInstance( target ) ) {
							throw new ClassCastException( target.getClass().getName() );
						}
						returnValue = thisMethod.invoke( target, args );
					}
					else {
						if ( !thisMethod.isAccessible() ) {
							thisMethod.setAccessible( true );
						}
						returnValue = thisMethod.invoke( target, args );
					}
					return returnValue == target ? proxy : returnValue;
				}
				catch ( InvocationTargetException ite ) {
					throw ite.getTargetException();
				}
			}
			else {
				return result;
			}
		}
		else {
			// while constructor is running
			if ( thisMethod.getName().equals( "getLazyInitializer" ) ) {
				return this;
			}
			else {
				return proceed.invoke( proxy, args );
			}
		}
	}

	protected Object serializableProxy() {
		return new SerializableProxy(
		        persistentClass,
		        interfaces,
		        getIdentifier(),
		        getIdentifierMethod,
		        setIdentifierMethod);
	}
}
