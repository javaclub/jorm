package com.github.javaclub.jorm.proxy.pojo.javassist;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Set;

import com.github.javaclub.jorm.JormException;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.proxy.JormProxy;
import com.github.javaclub.jorm.proxy.ProxyFactory;

/**
 * A {@link ProxyFactory} implementation for producing Javassist-based proxies.
 *
 * @author Muga Nishizawa
 */
@SuppressWarnings("unchecked")
public class JavassistProxyFactory implements ProxyFactory, Serializable {

	private static final long serialVersionUID = 9091420611226578537L;
	
	protected static final Class[] NO_CLASSES = new Class[0];
	private Class persistentClass;
	private Class[] interfaces;
	private Method getIdentifierMethod;
	private Method setIdentifierMethod;
	private Class factory;

	public void postInstantiate(
			final Class persistentClass,
	        final Set interfaces,
			final Method getIdentifierMethod,
	        final Method setIdentifierMethod) throws JormException {
		this.persistentClass = persistentClass;
		this.interfaces = (Class[]) interfaces.toArray(NO_CLASSES);
		this.getIdentifierMethod = getIdentifierMethod;
		this.setIdentifierMethod = setIdentifierMethod;
		factory = JavassistLazyInitializer.getProxyFactory( persistentClass, this.interfaces );
	}

	public JormProxy getProxy(
			Serializable id,
	        Session session) throws JormException {
		return JavassistLazyInitializer.getProxy(
				factory,
				persistentClass,
		        interfaces,
		        getIdentifierMethod,
				setIdentifierMethod,
		        id,
		        session);
	}

}
