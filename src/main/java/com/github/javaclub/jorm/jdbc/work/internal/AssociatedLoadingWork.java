/*
 * @(#)AssociatedLoadingWork.java	2011-9-16
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.work.internal;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.annotation.ManyToMany;
import com.github.javaclub.jorm.annotation.ManyToOne;
import com.github.javaclub.jorm.annotation.OneToMany;
import com.github.javaclub.jorm.annotation.OneToOne;
import com.github.javaclub.jorm.common.Reflections;
import com.github.javaclub.jorm.jdbc.ClassMetadata;
import com.github.javaclub.jorm.jdbc.JdbcException;
import com.github.javaclub.jorm.jdbc.collection.PersistentList;
import com.github.javaclub.jorm.jdbc.collection.PersistentSet;
import com.github.javaclub.jorm.jdbc.sql.AnnotationModelHelper;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;
import com.github.javaclub.jorm.jdbc.sql.SqlPrepared;
import com.github.javaclub.jorm.jdbc.work.AbstractStepWork;

/**
 * AssociatedLoadingWork
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: AssociatedLoadingWork.java 2011-9-16 上午11:08:37 Exp $
 */
@SuppressWarnings("unchecked")
public class AssociatedLoadingWork extends AbstractStepWork {
	
	private Object target;
	private ClassMetadata metadata;

	public AssociatedLoadingWork() {
		super();
	}

	public AssociatedLoadingWork(Object target) {
		super();
		this.target = target;
		this.metadata = ClassMetadata.getClassMetadata(target.getClass());
	}

	public boolean beforeWork(Session session) throws JdbcException {
		if(metadata.OneToOneFields.size() > 0) {
			Field field = null;
			SqlParams sqlParams = null;
			for (int i = 0; i < metadata.OneToOneFields.size(); i++) {
				field = metadata.OneToOneFields.get(i);
				OneToOne oneToOne = field.getAnnotation(OneToOne.class);
				String[] ownerFieldNames = oneToOne.ownerField();
				String[] selFieldNames = oneToOne.selField();
				Object[] params = AnnotationModelHelper.getSpecifiedFieldValues(target, ownerFieldNames);
				// sqlParams = RelationHelper.querySql(field.getType(), params, selFieldNames);
				sqlParams = SqlPrepared.preparedSelect(field.getType(), selFieldNames, params);
				
				// 避免循环加载(针对有双向关联的)
				sqlParams.setLoadAssociated(false);
				Object obj = session.loadFirst(sqlParams.setObjectClass(field.getType()));
				Reflections.setFieldValue(target, field, obj);
				Reflections.setFieldValue(obj, metadata
						.matchedField(ClassMetadata.getClassMetadata(field
						.getType()).OneToOneFields, metadata.clazz), target);
			}
		}
		return true;
	}

	public boolean doWork(Session session) throws JdbcException {
		if(metadata.OneToManyFields.size() > 0) {
			Class<?> manyType = null;
			Object value = null;
			for (Field field : metadata.OneToManyFields) {
				manyType = field.getType();
				if(manyType == List.class) {
					value = new PersistentList(session, target, field.getAnnotation(OneToMany.class).type());
				} else if(manyType == Set.class) {
					value = new PersistentSet(session, target, field.getAnnotation(OneToMany.class).type());
				}
				Reflections.setFieldValue(target, field, value);
			}
		}
		if(metadata.ManyToOneFields.size() > 0) {
			Object value = null;
			Object[] params = null;
			SqlParams sqlParams = null;
			for (Field field : metadata.ManyToOneFields) {
				params = AnnotationModelHelper.getSpecifiedFieldValues(target, 
								field.getAnnotation(ManyToOne.class).ownerField());
				/*sqlParams = RelationHelper.querySql(field.getType(), 
								params, field.getAnnotation(ManyToOne.class).selField());*/
				sqlParams = SqlPrepared.preparedSelect(field.getType(), 
								field.getAnnotation(ManyToOne.class).selField(), params);
				sqlParams.setLoadAssociated(false);// 避免循环加载
				value = session.loadFirst(sqlParams.setObjectClass(field.getType()));
				Reflections.setFieldValue(target, field, value);
			}
			sqlParams = null;
			params = null;
			value = null;
		}
		if(metadata.ManyToManyFields.size() > 0) {
			Class<?> manyType = null;
			Object value = null;
			for (Field field : metadata.ManyToManyFields) {
				manyType = field.getType();
				if(manyType == List.class) {
					value = new PersistentList(session, target, field.getAnnotation(ManyToMany.class).type());
				} else if(manyType == Set.class) {
					value = new PersistentSet(session, target, field.getAnnotation(ManyToMany.class).type());
				}
				Reflections.setFieldValue(target, field, value);
			}
		}
		return true;
	}

	public boolean finalWork(Session session) throws JdbcException {
		try {
			return true;
		} finally {
			metadata = null;
		}
	}

}
