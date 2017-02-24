/*
 * @(#)IncreasableIdentifiers.java	2011-8-6
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.id;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.annotation.Id;

/**
 * IncreasableIdentifiers, for IncrementGenerator.
 * 
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: IncreasableIdentifiers.java 483 2011-09-26 14:04:43Z gerald.chen.hz $
 */
@SuppressWarnings("unchecked")
public class IncreasableIdentifiers {

	private static Log LOG = LogFactory.getLog(AutoIdGenerator.class);

	private static ConcurrentMap<Class, AtomicLong> identifiersHolder = new ConcurrentHashMap<Class, AtomicLong>();

	public synchronized static Serializable getNextIdValue(Session session, Class objectClass, Field idField) {
		int incrementBy = 1;
		Id idAnnotation = idField.getAnnotation(Id.class);
		if (null != idAnnotation && idAnnotation.incrementBy() > 1) {
			incrementBy = idAnnotation.incrementBy();
		}
		AtomicLong longValue = identifiersHolder.get(objectClass);
		if (null == longValue) {
			// 取得数据库中的最大Id值
			Long id = getDatabaseMaxIdValue(session, objectClass, idField);
			longValue = new AtomicLong(id == 0 ? 1 : (id + incrementBy));
			if (LOG.isInfoEnabled()) {
				LOG.info("Class => [" + objectClass.getName() + "] => first free id: "
						+ (id + incrementBy));
			}
			identifiersHolder.put(objectClass, longValue);
		} else {
			longValue.addAndGet(incrementBy);
		}
		return IdentifierGeneratorFactory.createIdentifier(longValue.get(), idField.getType());
	}

	protected static Long getDatabaseMaxIdValue(Session session, Class objectClass,
			Field idField) {
		String sql = session.getDialect().maxIdSql(objectClass, idField);
		Serializable id = session.unique(sql);
		return id == null ? 0L : Long.valueOf(id.toString());
	}
	
	public static void main(String[] args) {
		boolean flag = true;
		int i = 9;
		
		System.out.println(((flag ? (i - 1) : 0) + 1) + "");
	}

}