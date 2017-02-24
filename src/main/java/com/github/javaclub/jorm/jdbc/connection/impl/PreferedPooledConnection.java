/*
 * @(#)PooledConnection.java	2010-4-21
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.jdbc.connection.impl;


/**
 * This call exists only for backward compatiblity.
 * It just extends {@link C3P0PooledConnection}
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: PreferedPooledConnection.java 442 2011-09-22 11:02:08Z gerald.chen.hz $
 * @since 1.0.6
 */
public class PreferedPooledConnection extends C3P0PooledConnection {}
