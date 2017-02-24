package com.github.javaclub.jorm.common.javabean;

/**
 * Java data type handler
 */
public interface IDataTypeHandler {

	// 根据字母串，按照字段在Soul对象中定义的类型，转换成相应的数据类型对象。
	public Object getValue(String fieldValue) ;
	
}
