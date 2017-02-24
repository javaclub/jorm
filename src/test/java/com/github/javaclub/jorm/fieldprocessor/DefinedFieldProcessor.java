/*
 * @(#)DefinedFieldProcessor.java	2011-9-1
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.fieldprocessor;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.common.CommonUtil;
import com.github.javaclub.jorm.common.Reflections;
import com.github.javaclub.jorm.jdbc.process.FieldProcessor;

/**
 * DefinedFieldProcessor
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: DefinedFieldProcessor.java 287 2011-09-01 15:23:35Z gerald.chen.hz@gmail.com $
 */
public class DefinedFieldProcessor implements FieldProcessor {

	public Object insert(Session session, Object entity, Field field) {
		String[] crs = (String[]) Reflections.getFieldValue(entity, field);
		if(!CommonUtil.isEmpty(crs)) {
			StringBuilder sbf = new StringBuilder();
			for (int i = 0; i < crs.length; i++) {
				if(i > 0) {
					sbf.append(",");
				}
				sbf.append(crs[i]);
			}
			return sbf.toString();
		}
		return "";
	}

	public void load(Session session, Object entity, Field field, ResultSet rs,
			int idx) throws SQLException {
		String str = rs.getString(idx);
		String[] crs = str.split(",");
		Reflections.setFieldValue(entity, field, crs);
	}

}
