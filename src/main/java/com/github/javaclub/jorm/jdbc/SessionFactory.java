/*
 * @(#)SessionFactory.java	2010-4-21
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.jdbc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.config.JdbcConfigXmlParser;


/**
 * A SessionFactory for JdbcSession.
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: SessionFactory.java 547 2011-10-09 10:10:45Z gerald.chen.hz $
 */
public class SessionFactory {

	private static final Log LOG = LogFactory.getLog(SessionFactory.class);
	private static final ThreadLocal<Session> threadLocal = new ThreadLocal<Session>();
	private static ConcurrentMap<String, Session> cachedSession = new ConcurrentHashMap<String, Session>();
	
	/**
	 * 取得默认数据库连接的JdbcSession
	 *
	 * @return {@link JdbcSession}
	 */
	public static Session currentSession() {
		Session session = threadLocal.get();
		if (null == session || session.isClosed()) {
			session = JdbcSession.getSession();
			threadLocal.set(session);
		}
        return session;
	}
	
	/**
	 * Get a new JdbcSession
	 *
	 * @return A new JdbcSession
	 */
	public static Session newSession() {
		Session session = newSession(JdbcConfigXmlParser.getDefaultConnectionName());
        return session;
	}

	public static Session newSession(String providerName) {
		Session session = JdbcSession.getSession(providerName);
		if (null != session) {
			cachedSession.put(providerName + ":" + session.toString(), session);
		}
		return session;
	}
	
	public static Session getSession(String providerName) {
		Session session = cachedSession.get(providerName);
		if(null != session && (!session.isClosed())) {
			return session;
		}
		session = JdbcSession.getSession(providerName);
		cachedSession.put(providerName, session);

        return session;
	}
	
	public static void destory() {
		if(LOG.isInfoEnabled()) {
			LOG.info("Destroying sessions started...");
		}
		Session session = null;
		try {
			for (Map.Entry<String, Session> entry : cachedSession.entrySet()) {
				session = entry.getValue();
				if(null != session && !session.isClosed()) {
					session.close();
					session = null;
				}
			}
		} finally {
			cachedSession.clear();
		}
		
		session = threadLocal.get();
		if(null != session && !session.isClosed()) {
			session.close();
			session = null;
		}
		if(LOG.isInfoEnabled()) {
			LOG.info("Destroying sessions ended...");
		}
	}
}
