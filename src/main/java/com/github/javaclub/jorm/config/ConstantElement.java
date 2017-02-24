/*
 * @(#)ConstantElement.java	2011-8-16
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.config;

import java.io.Serializable;

import com.github.javaclub.jorm.common.xml.XmlParserException;
import com.github.javaclub.jorm.common.xml.XmlUtil;
import com.github.javaclub.jorm.common.xml.Xmlizable;
import org.w3c.dom.Element;

/**
 * ConstantElement for the constants defined in jorm.cfg.xml.
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: ConstantElement.java 2011-8-16 下午07:26:24 Exp $
 */
public class ConstantElement implements Serializable, Xmlizable {

	private static final long serialVersionUID = 2849193524911093206L;
	
	private String name;
	
	private String value;
	
	public ConstantElement() {
		super();
	}
	
	public ConstantElement(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public void fromXml(Element element) throws XmlParserException {
		if(null == element) {
			throw new XmlParserException("The xml element is null.");
		}
		String attrName = element.getAttribute("name");
		String attrValue = element.getAttribute("value");
		if(attrName == null || attrValue == null) {
			throw new XmlParserException("The xml element attribute value can't be null.");
		}
		this.setName(attrName.trim());
		this.setValue(attrValue.trim());
	}

	public void toXml(Element element) throws XmlParserException {
		if(null == element) {
			element = XmlUtil.createElement("constant");
		}
		element.setAttribute("name", this.getName());
		element.setAttribute("value", this.getValue());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public int intValue() {
		return Integer.parseInt(this.getValue());
	}
	
	public long longValue() {
		return Long.parseLong(this.getValue());
	}
	
	public float floatValue() {
		return Float.parseFloat(this.getValue());
	}
	
	public double doubleValue() {
		return Double.parseDouble(this.getValue());
	}
	
	public boolean booleanValue() {
		return Boolean.parseBoolean(this.getValue());
	}

}
