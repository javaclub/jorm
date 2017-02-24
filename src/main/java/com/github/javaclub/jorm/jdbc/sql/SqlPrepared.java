/*
 * @(#)SqlPrepared.java	2011-7-20
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.sql;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.common.CommonUtil;
import com.github.javaclub.jorm.common.Reflections;
import com.github.javaclub.jorm.jdbc.ClassMetadata;


/**
 * SqlPrepared
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: SqlPrepared.java 2011-7-20 下午04:44:29 Exp $
 */
@SuppressWarnings("unchecked")
public class SqlPrepared {
	
	private static final Log LOG = LogFactory.getLog(SqlPrepared.class);
	
	/**
	 * 获取清空表的sql语句
	 *
	 * @param clazz 实体类型
	 * @return 删除表的全部数据的sql
	 */
	public static String preparedDeleteAll(Class<?> clazz) {
		return "DELETE FROM " + ClassMetadata.getClassMetadata(clazz).tableName;
	}
	
	/**
	 * 获取查询所有记录的sql
	 *
	 * @param clazz 实体类型
	 * @return sql
	 */
	public static String preparedSelectAll(Class<?> clazz) {
		return "SELECT * FROM " + ClassMetadata.getClassMetadata(clazz).tableName;
	}
	
	public static <T> SqlParams<T> preparedSelect(Class<T> clazz, String[] fieldNames, Object... fieldValues) {
		if(CommonUtil.isEmpty(fieldNames) || CommonUtil.isEmpty(fieldValues)) {
			return new SqlParams<T>(preparedSelectAll(clazz), (Object[]) null)  ;
		}
		String tbname = ClassMetadata.getClassMetadata(clazz).tableName;
		final String sql = "SELECT * FROM " + tbname + " WHERE " + AnnotationModelHelper.renderWhereFragment(clazz, fieldNames);
		return new SqlParams<T>(sql, fieldValues);
	}
	
	/**
	 * 获取一个对象是否在数据库中存在的SqlParams
	 * <li>"SELECT * FROM t_table WHERE pk_id1=? AND pk_id2=?" => params:  new Object[2]
	 * <li>"SELECT * FROM t_table WHERE col1=? AND col2=? AND col3=? AND ... AND colN=?" => params:  new Object[N]
	 *
	 * @param model 对象
	 * @return SqlParams
	 */
	public static <T> SqlParams<T> preparedContains(T model) {
		Class<?> persitentClass = Jorm.getClass(model);
		if(Jorm.isProxy(model)) {
			model = (T) Jorm.getTarget(model);
		}
		List<Field> condFields = ClassMetadata.getClassMetadata(persitentClass).definedPkFields;
		if (condFields.isEmpty()) {
			condFields = ClassMetadata.getClassMetadata(persitentClass).allFields;
		}
		SqlParams<T> sqlParams = AnnotationModelHelper.renderWhereFragment(model, condFields);
		final String sql = "SELECT * FROM "
				+ ClassMetadata.getClassMetadata(persitentClass).tableName
				+ " WHERE " + sqlParams.getSql(false);
		return sqlParams.setSql(sql);
		
	}
	
	public static <T> SqlParams<T> preparedDelete(T model) {
		Class<?> persitentClass = Jorm.getClass(model);
		if(Jorm.isProxy(model)) {
			model = (T) Jorm.getTarget(model);
		}
		List<Field> condFields = ClassMetadata.getClassMetadata(persitentClass).definedPkFields;
		if (condFields.isEmpty()) {
			condFields = ClassMetadata.getClassMetadata(persitentClass).allFields;
		}
		SqlParams<T> sqlParams = AnnotationModelHelper.renderWhereFragment(model, condFields);
		final String sql = "DELETE FROM "
				+ ClassMetadata.getClassMetadata(persitentClass).tableName
				+ " WHERE "
				+ sqlParams.getSql(false);
		return sqlParams.setSql(sql);
		
	}
	
	public static <T> SqlParams<T> preparedUpdate(T model) {
		Class<?> persistentClass = Jorm.getClass(model);
		if(Jorm.isProxy(model)) {
			model = (T) Jorm.getTarget(model);
		}
		List<Field> pkFields = ClassMetadata.getClassMetadata(persistentClass).definedPkFields;
		List<Field> updateFields = ClassMetadata.getClassMetadata(persistentClass).updateFields;
		if (pkFields.isEmpty() || updateFields.isEmpty()) {
			LOG.warn("No @PK defined in Class[" + persistentClass.getName() + "], but update operation need the annotation @PK.");
			return null;// 没有PK注解，无法确定唯一记录
		}
		Object[] params = new Object[pkFields.size() + updateFields.size()];
		StringBuilder sbf = new StringBuilder();
		try {
			sbf.append("UPDATE ").append(ClassMetadata.getClassMetadata(persistentClass).tableName);
			sbf.append(" SET ");
			int i = 0;
			for (i = 0; i < updateFields.size(); i++) {
				if (i > 0) {
					sbf.append(", ");
				}
				sbf.append(AnnotationModelHelper.getColumName(updateFields.get(i)));
				sbf.append(" = ?");
				params[i] = Reflections.getFieldValue(model, updateFields.get(i));
			}
			sbf.append(" WHERE ");
			for (int j = 0; j < pkFields.size(); j++) {
				if (j > 0) {
					sbf.append(" AND ");
				}
				sbf.append(AnnotationModelHelper.getColumName(pkFields.get(j)));
				sbf.append(" = ?");
				params[i] = Reflections.getFieldValue(model, pkFields.get(j));
				i++;
			}
			return new SqlParams<T>(sbf.toString(), params);
		} finally {
			sbf.setLength(0);
			sbf = null;
		}
	}

	/**
	 * 获取插入实体对象预sql语句
	 *
	 * @param model 实体对象
	 * @return 插入实体的sql
	 */
	public static <T> String preparedInsert(Class<T> clazz) {
		List<Field> insertFields = ClassMetadata.getClassMetadata(clazz).insertFields;
		StringBuilder sbfPrev = new StringBuilder();
		StringBuilder sbfNext = new StringBuilder();
		try {
			for (int i = 0; i < insertFields.size(); i++) {
				if (i > 0) {
					sbfPrev.append(",");
					sbfNext.append(",");
				}
				sbfPrev.append(AnnotationModelHelper.getColumName(insertFields.get(i)));
				sbfNext.append("?");
			}
			return "INSERT INTO "
					+ ClassMetadata.getClassMetadata(clazz).tableName + "("
					+ sbfPrev.toString() + ") VALUES(" + sbfNext.toString()
					+ ")";
		} finally {
			sbfPrev.setLength(0);
			sbfNext.setLength(0);
			sbfPrev = null;
			sbfNext = null;
		}
	}
	
}
