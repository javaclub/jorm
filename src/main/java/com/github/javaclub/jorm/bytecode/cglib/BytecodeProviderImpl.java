/*
 * @(#)BytecodeProviderImpl.java	2011-9-1
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.bytecode.cglib;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.javaclub.jorm.bytecode.BytecodeProvider;
import com.github.javaclub.jorm.bytecode.ProxyFactoryFactory;

/**
 * BytecodeProviderImpl
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: BytecodeProviderImpl.java 2011-9-1 下午02:25:16 Exp $
 */
public class BytecodeProviderImpl implements BytecodeProvider {
	
	private static final Log LOG = LogFactory.getLog(BytecodeProviderImpl.class);
	
	public BytecodeProviderImpl() {
		if(LOG.isInfoEnabled()) {
			LOG.info( "Using the CGLIB BytecodeProvider implementation." );
		}
	}

	public ProxyFactoryFactory getProxyFactoryFactory() {
		return new ProxyFactoryFactoryImpl();
	}

}
