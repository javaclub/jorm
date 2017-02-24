package com.github.javaclub.jorm.bytecode.javassist;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.javaclub.jorm.bytecode.BytecodeProvider;
import com.github.javaclub.jorm.bytecode.ProxyFactoryFactory;

/**
 * Bytecode provider implementation for Javassist.
 *
 * @author Steve Ebersole
 */
public class BytecodeProviderImpl implements BytecodeProvider {

	private static final Log LOG = LogFactory.getLog( BytecodeProviderImpl.class );

	public BytecodeProviderImpl() {
		if(LOG.isInfoEnabled()) {
			LOG.info( "Using the javassist BytecodeProvider implementation." );
		}
	}

	public ProxyFactoryFactory getProxyFactoryFactory() {
		return new ProxyFactoryFactoryImpl();
	}


}
