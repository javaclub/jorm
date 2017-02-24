/*
 * @(#)AnnotationModelHelper.java	2011-7-10
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.sql;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaclub.jorm.JormException;
import com.github.javaclub.jorm.annotation.Column;
import com.github.javaclub.jorm.annotation.Entity;
import com.github.javaclub.jorm.annotation.Id;
import com.github.javaclub.jorm.annotation.NoColumn;
import com.github.javaclub.jorm.annotation.PK;
import com.github.javaclub.jorm.annotation.constant.GenerationType;
import com.github.javaclub.jorm.common.Annotations;
import com.github.javaclub.jorm.common.CommonUtil;
import com.github.javaclub.jorm.common.Reflections;
import com.github.javaclub.jorm.common.Strings;

/**
 * AnnotationModelHelper
 * 
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: AnnotationModelHelper.java 396 2011-09-18 11:15:08Z gerald.chen.hz $
 */
public abstract class AnnotationModelHelper {

	public static String insert(Class<?> clazz) {
		List<String> columns = new ArrayList<String>();
		String tbname = getTableName(clazz);

		// 需要往数据库中插入值的属性
		Field[] toInsertFields = getNeedInsertFields(clazz);
		for (Field field : toInsertFields) {
			NoColumn noColumn = field.getAnnotation(NoColumn.class);
			if (null != noColumn) {
				continue;
			}
			Column column = field.getAnnotation(Column.class);
			if (null == column || Strings.isEmpty(column.value())) {
				columns.add(field.getName());
			} else {
				columns.add(column.value());
			}
		}

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ").append(tbname + "(");
		for (int i = 0; i < columns.size(); i++) {
			sql.append(columns.get(i) + ",");
		}
		sql.deleteCharAt(sql.lastIndexOf(",")).append(") VALUES(");

		for (int i = 0; i < columns.size(); i++) {
			sql.append("?,");
		}
		sql.deleteCharAt(sql.lastIndexOf(",")).append(")");
		return sql.toString();
	}

	public static <T> SqlParams<T> delete(T model) {
		String[] keycols = getPrimaryColumns(model.getClass());
		if(CommonUtil.isEmpty(keycols)) {
			return deleteByFields(model);
		}
		String sql = "DELETE FROM " + getTableName(model.getClass())
				+ " WHERE " + getKeyFieldValueCondition(model);
		Object[] params = getKeyFieldValues(model);
		return new SqlParams<T>(sql, params);
	}
	
	public static <T> SqlParams<T> deleteByFields(T model,
			String... excludedFields) {
		String sql = "DELETE FROM " + getTableName(model.getClass())
				+ " WHERE " + getFieldValueConditions(model, excludedFields);
		Object[] params = getFieldValues(model, excludedFields);
		return new SqlParams<T>(sql, params);
	}

	public static <T> SqlParams<T> delete(Class<T> clazz, String fieldNames,
			Object... params) {
		String sql = "DELETE FROM " + getTableName(clazz) + " WHERE "
				+ getFieldValueConditions(clazz, fieldNames);
		return new SqlParams<T>(sql, params);
	}
	
	public static <T> String selectAll(Class<T> clazz) {
		return "SELECT * FROM " + getTableName(clazz);
	}

	/**
	 * 获取预sql和占位符参数的组合
	 * 
	 * @param model 被注解过的对象
	 * @return 预sql组合
	 */
	public static <T> SqlParams<T> select(T model) {
		String[] keycols = getPrimaryColumns(model.getClass());
		if(CommonUtil.isEmpty(keycols)) {
			return selectByFields(model);
		}
		// selectByKeyField
		String sql = "SELECT * FROM " + getTableName(model.getClass())
				+ " WHERE " + getKeyFieldValueCondition(model);
		Object[] params = getKeyFieldValues(model);
		return new SqlParams<T>(sql, params);
	}

	public static <T> SqlParams<T> selectByFields(T model,
			String... excludedFields) {
		String sql = "SELECT * FROM " + getTableName(model.getClass())
				+ " WHERE " + getFieldValueConditions(model, excludedFields);
		Object[] params = getFieldValues(model, excludedFields);
		return new SqlParams<T>(sql, params);
	}

	public static <T> SqlParams<T> select(Class<T> clazz, String fieldNames,
			Object... params) {
		String sql = "SELECT * FROM " + getTableName(clazz) + " WHERE "
				+ getFieldValueConditions(clazz, fieldNames);
		return new SqlParams<T>(sql, params);
	}

	public static <T> SqlParams<T> update(T model, String updateFieldNames,
			Object... params) {
		String sql = "UPDATE " + getTableName(model.getClass()) + " SET "
				+ getUpdateColumnsSql(model.getClass(), updateFieldNames) + " WHERE " + getKeyFieldValueCondition(model);
		return new SqlParams<T>(sql, params);
	}
	
	public static String autoId(Class<?> clazz, Field keyField) {
		String keyCol = keyField.getName();
		Column colAnn = Annotations.getAnnotation(keyField, Column.class);
		if (colAnn != null && !Strings.isEmpty(colAnn.value())) {
			keyCol = colAnn.value();
		}
		String sql = "SELECT (MAX(" + keyCol + ") + 1) FROM "
				+ getTableName(clazz);
		return sql;
	}
	
	public static String mysqlIncrId(Class<?> clazz) {
		return "SHOW TABLE STATUS WHERE Name='" + getTableName(clazz) + "'";
	}
	
	public static Object[] getFieldValues(Object model, String... excludedFields) {
		List<Object> list = new ArrayList<Object>();
		Field[] allFields = Reflections.getFields(model.getClass());
		for(Field field : allFields) {
			if(Annotations.hasAnnotation(field, NoColumn.class)) {
				continue;
			}
			if(CommonUtil.contains(excludedFields, field.getName())) {
				continue;
			}
			list.add(Reflections.getFieldValue(model, field));
		}
		return list.toArray(new Object[0]);
	}
	
	public static Object[] getSpecifiedFieldValues(Object model, String[] specifiedFieldNames) {
		Object[] array = new Object[specifiedFieldNames.length];
		for(int i = 0; i < specifiedFieldNames.length; i++) {
			array[i] = Reflections.getFieldValue(model, specifiedFieldNames[i]);
		}
		return array;
	}

	/**
	 * 获取表名
	 * 
	 * @param clazz 被注解过的对象对应的类型
	 * @return 对象类型对应的数据表名
	 */
	public static String getTableName(Class<?> clazz) {
		/*Table table = Annotations.findAnnotation(clazz, Table.class);
		return table == null ? clazz.getSimpleName() : table.value();*/
		Entity entity = Annotations.findAnnotation(clazz, Entity.class);
		if(null == entity) {
			throw new JormException("The annotation @Entity is not found.");
		}
		return entity.table();
	}
	
	public static String getColumName(Field field) {
		Column column = Annotations.getAnnotation(field, Column.class);
		return column == null ? field.getName() : column.value();
	}
	
	/**
	 * 按列取得where条件的sql片段
	 *
	 * @param model 对象实体
	 * @param condFields 这些Field必须属于对象实体model
	 * @return sql片段并包含参数
	 */
	public static <T> SqlParams<T> renderWhereFragment(T model, List<Field> condFields) {
		Object[] params = new Object[condFields.size()];
		StringBuilder sbf = new StringBuilder();
		try {
			for (int i = 0; i < condFields.size(); i++) {
				if (i > 0) {
					sbf.append(" AND ");
				}
				sbf.append(getColumName(condFields.get(i)));
				sbf.append(" = ?");
				params[i] = Reflections.getFieldValue(model, condFields.get(i));
			}
			return new SqlParams<T>(sbf.toString(), params);
		} finally {
			sbf.setLength(0);
			sbf = null;
			params = null;
		}
	}
	
	public static <T> String renderWhereFragment(Class<T> clazz, String[] fieldNames) {
		StringBuilder sbf = new StringBuilder();
		Field field;
		try {
			for (int i = 0; i < fieldNames.length; i++) {
				if (i > 0) {
					sbf.append(" AND ");
				}
				field = Reflections.getField(clazz, fieldNames[i]);
				sbf.append(getColumName(field));
				sbf.append(" = ?");
			}
			return sbf.toString();
		} finally {
			field = null;
			sbf.setLength(0);
			sbf = null;
		}
	}
	
	public static String renderWhereFragment(List<Field> condFields) {
		StringBuilder sbf = new StringBuilder();
		try {
			for (int i = 0; i < condFields.size(); i++) {
				if (i > 0) {
					sbf.append(" AND ");
				}
				sbf.append(getColumName(condFields.get(i)));
				sbf.append("=?");
			}
			return sbf.toString();
		} finally {
			sbf.setLength(0);
			sbf = null;
		}
	}
	
	public static String renderWhereFragment(Field[] condFields) {
		StringBuilder sbf = new StringBuilder();
		try {
			for (int i = 0; i < condFields.length; i++) {
				if (i > 0) {
					sbf.append(" AND ");
				}
				sbf.append(getColumName(condFields[i]));
				sbf.append("=?");
			}
			return sbf.toString();
		} finally {
			sbf.setLength(0);
			sbf = null;
		}
	}

	/**
	 * 获取主键列名
	 * 
	 * @param clazz 被注解过的对象对应的类型
	 * @return 主键列名
	 */
	public static String[] getPrimaryColumns(Class<?> clazz) {
		Annotation ann = Annotations.findAnnotation(clazz, PK.class);
		if (ann == null)
			return null;
		String[] pkFieldNames = ((PK) ann).value();
		String[] pkColNames = new String[pkFieldNames.length];
		Field f = null;
		Column column = null;
		for (int i = 0; i < pkFieldNames.length; i++) {
			f = Reflections.getField(clazz, pkFieldNames[i]);
			pkColNames[i] = f.getName();
			column = f.getAnnotation(Column.class);
			if (null != column && !Strings.isEmpty(column.value())) {
				pkColNames[i] = column.value();
			}
		}
		return pkColNames;
	}

	/**
	 * 根据属性名找到对应的数据库表的列名
	 * 
	 * @param clazz       被注解过的对象对应的类型
	 * @param fieldNames  属性Field
	 * @return  各个属性Field对应的列名
	 */
	public static String[] getColumns(Class<?> clazz, String[] fieldNames) {
		String[] colnames = new String[fieldNames.length];
		Field field = null;
		for (int i = 0; i < fieldNames.length; i++) {
			field = Reflections.getField(clazz, fieldNames[i]);
			if (field == null) {
				continue;
			}
			colnames[i] = field.getName();
			Column column = Annotations.getAnnotation(field, Column.class);
			if (column != null) {
				colnames[i] = column.value();
			}
		}
		return colnames;
	}

	/**
	 * 取得需要往数据库中插入值的属性
	 * <p>
	 * 循环向上获得所有的属性,包括私有属性,不包括 属性[class].
	 *
	 * @param clazz 被注解过的对象对应的类型
	 * @return 属性Field
	 */
	public static Field[] getNeedInsertFields(Class<?> clazz) {
		Class<?> theClass = clazz;
		Map<String, Field> list = new HashMap<String, Field>();
		while (null != theClass && !(theClass == Object.class)) {
			Field[] fs = theClass.getDeclaredFields();
			for (int i = 0; i < fs.length; i++) {
				if (isIgnoredField(fs[i]))
					continue;
				if (list.containsKey(fs[i].getName()))
					continue;
				Id idAnn = Annotations.getAnnotation(fs[i], Id.class);
				if (idAnn != null && GenerationType.isAutoGenerated(idAnn.value())) {
					continue;
				}
				list.put(fs[i].getName(), fs[i]);
			}
			theClass = theClass.getSuperclass();
		}
		return list.values().toArray(new Field[list.size()]);
	}
	
	/**
	 * 获取对象主键属性对应的值
	 *
	 * @param model 目标对象
	 * @return 主键属性的值
	 */
	public static Object[] getKeyFieldValues(Object model) {
		Annotation ann = Annotations.findAnnotation(model.getClass(),
				PK.class);
		if (ann == null)
			return new Object[0];
		String[] pkFieldNames = ((PK) ann).value();
		Object[] params = new Object[pkFieldNames.length];
		for (int i = 0; i < pkFieldNames.length; i++) {
			params[i] = Reflections.getFieldValue(model, pkFieldNames[i]);
		}
		return params;
	}
	
	protected static String getUpdateColumnsSql(Class<?> clazz, String updateFieldNames) {
		StringBuilder sql = new StringBuilder();
		String[] fieldNames = updateFieldNames.split(",");
		Field field = null;
		Column column = null;
		for(int i = 0; i < fieldNames.length; i++) {
			field = Reflections.getField(clazz, fieldNames[i]);
			column = Annotations.getAnnotation(field, Column.class);
			if(column ==  null || Strings.isEmpty(column.value())) {
				sql.append(field.getName() + "=?,");
			} else {
				sql.append(column.value() + "=?,");
			}
		}
		return sql.deleteCharAt(sql.lastIndexOf(",")).toString();
	}

	/**
	 * 生成满足主键属性值对应的sql，例如：
	 * <li>"1=1"(没有主键时)
	 * <li>"id=? and name=?"
	 *
	 * @param model 被注解过的目标对象
	 * @return sql语句
	 */
	protected static String getKeyFieldValueCondition(Object model) {
		StringBuilder sbf = new StringBuilder();
		String[] pkColnames = getPrimaryColumns(model.getClass());
		sbf.append(pkColnames[0] + "=?");
		if (pkColnames.length > 1) {
			for (int i = 1; i < pkColnames.length; i++) {
				sbf.append(" AND ").append(pkColnames[i] + "=?");
			}
		}
		return sbf.toString();
	}

	/**
	 * 生成满足相关属性值对应的sql，例如：
	 *
	 * @param clazz 被注解过的对象对应的类型
	 * @param fieldNames 列，如"id,name"
	 * @return sql语句
	 */
	protected static String getFieldValueConditions(Class<?> clazz,
			String fieldNames) {
		if (Strings.isEmpty(fieldNames)) {
			return "1=1";
		}
		StringBuilder sbf = new StringBuilder();
		String[] colnames = getColumns(clazz, fieldNames.split(","));

		sbf.append(colnames[0] + "=?");
		if (colnames.length > 1) {
			for (int i = 1; i < colnames.length; i++) {
				sbf.append(" AND ").append(colnames[i] + "=?");
			}
		}
		return sbf.toString();
	}
	
	protected static String getFieldValueConditions(Object model,
			String... excludedFields) {
		StringBuilder sbf = new StringBuilder();
		Field[] allFields = Reflections.getFields(model.getClass());
		for(Field field : allFields) {
			if(Annotations.hasAnnotation(field, NoColumn.class)) {
				continue;
			}
			if(CommonUtil.contains(excludedFields, field.getName())) {
				continue;
			}
			sbf.append(" AND ").append(getColumName(field) + "=?");
		}
		if(sbf.length() > 0) {
			sbf = new StringBuilder(sbf.substring(5));
		}
		return sbf.toString();
	}

	private static boolean isIgnoredField(Field f) {
		if (Modifier.isStatic(f.getModifiers()))
			return true;
		if (Modifier.isFinal(f.getModifiers()))
			return true;
		if (f.getName().startsWith("this$"))
			return true;
		return false;
	}
	
	public static void main(String[] args) {
		StringBuilder sbf = new StringBuilder(" AND 54354364,");
		sbf.deleteCharAt(sbf.lastIndexOf(",")).append("KKKK");
		System.out.println(sbf.toString());
		
		System.out.println(SqlParams.class.getSimpleName());
	}

}
