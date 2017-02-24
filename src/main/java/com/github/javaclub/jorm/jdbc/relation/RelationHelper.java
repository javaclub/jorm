/*
 * @(#)RelationHelper.java	2011-7-12
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.relation;

import java.lang.reflect.Field;
import java.util.List;

import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.JormException;
import com.github.javaclub.jorm.annotation.Column;
import com.github.javaclub.jorm.annotation.Relation;
import com.github.javaclub.jorm.common.Annotations;
import com.github.javaclub.jorm.common.Reflections;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.jdbc.sql.AnnotationModelHelper;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;

/**
 * RelationHelper
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: RelationHelper.java 575 2011-10-10 09:44:13Z gerald.chen.hz $
 * @deprecated
 */
public class RelationHelper {

	@SuppressWarnings("unchecked")
	public static <T> T one(Object model) {
		Class<?> thisClass = model.getClass();
		Relation relationAnn = Annotations.findAnnotation(thisClass, Relation.class);
		if(relationAnn == null) {
			throw new JormException("The object model must have the annotation [Relation]");
		}
		String[] thisFieldNames = relationAnn.thisField();
		String[] thatFieldNames = relationAnn.thatField();
		Object[] params = AnnotationModelHelper.getSpecifiedFieldValues(model, thisFieldNames);
		SqlParams sqlParams = querySql(relationAnn.thatClass(), params, thatFieldNames);
		
		return (T) Jorm.getSession().loadFirst(sqlParams.setObjectClass(relationAnn.thatClass()));
	}
	
	/**
	 * 取分页列表数据
	 *
	 * @param model
	 * @param start 从1开始
	 * @param limit 取多少条
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> many(Object model, int start, int limit) {
		Class<?> thisClass = model.getClass();
		Relation relationAnn = Annotations.findAnnotation(thisClass, Relation.class);
		if(relationAnn == null) {
			throw new JormException("The object model must have the annotation @Relation");
		}
		Class<?> thatClass = relationAnn.thatClass();
		String[] thisFieldNames = relationAnn.thisField();
		String[] thatFieldNames = relationAnn.thatField();
		Object[] params = AnnotationModelHelper.getSpecifiedFieldValues(model, thisFieldNames);
		SqlParams sqlParams = querySql(thatClass, params, thatFieldNames);
		sqlParams.setObjectClass(thatClass);
		sqlParams.setFirstResult(start).setMaxResults(limit);
		
		return Jorm.getSession().list(sqlParams);
	}

	public static <T> SqlParams<T> querySql(Class<T> clazz, Object[] params,
			String[] fieldNames) {
		String sql = "SELECT * FROM "
				+ AnnotationModelHelper.getTableName(clazz) + " WHERE "
				+ getCondition(clazz, fieldNames);
		return new SqlParams<T>(sql, params);
	}
	
	public static String getCondition(Class<?> clazz, String[] fieldNames) {
		StringBuilder sbf = new StringBuilder();
		String[] pkColnames = getColumns(clazz, fieldNames);
		sbf.append(pkColnames[0] + " = ?");
		if (pkColnames.length > 1) {
			for (int i = 1; i < pkColnames.length; i++) {
				sbf.append(" AND ").append(pkColnames[i] + " = ?");
			}
		}
		return sbf.toString();
	}

	public static String[] getColumns(Class<?> clazz, String[] fieldNames) {
		String[] cols = new String[fieldNames.length];
		Field field = null;
		for(int i = 0; i < fieldNames.length; i++) {
			cols[i] = fieldNames[i];
			field = Reflections.getField(clazz, fieldNames[i]);
			if(field != null) {
				Column column = Annotations.getAnnotation(field, Column.class);
				if(column != null && Strings.isNotEmpty(column.value())) {
					cols[i] = column.value();
				}
			}
		}
		return cols;
	}
	
}
