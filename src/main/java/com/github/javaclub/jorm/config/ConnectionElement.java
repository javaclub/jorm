/*
 * @(#)ConnectionElement.java	2010-4-20
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.config;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Properties;

import com.github.javaclub.jorm.common.xml.XmlParserException;
import com.github.javaclub.jorm.common.xml.XmlUtil;
import com.github.javaclub.jorm.common.xml.Xmlizable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The entity bean for connection element in jorm.cfg.xml.
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: ConnectionElement.java 558 2011-10-10 03:19:30Z gerald.chen.hz $
 */
public class ConnectionElement implements Xmlizable, Serializable {
	
	private static final long serialVersionUID = 664700876457855912L;
	private String name;
	private Properties props = new Properties();
	
	public ConnectionElement() {
		super();
	}

	public ConnectionElement(String name, Properties props) {
		super();
		this.name = name;
		this.props = props;
	}

	public void fromXml(Element element) throws XmlParserException {
		this.setName(element.getAttribute("name"));
		
		NodeList nodes = element.getElementsByTagName("property");
		Element elem = null;
		Properties properties = new Properties();
		for (int i = 0; i < nodes.getLength(); i++) {
			elem = (Element) nodes.item(i);
			properties.setProperty(elem.getAttribute("name"), elem.getTextContent());
		}
		this.setProps(properties);
	}
	
	@SuppressWarnings("unchecked")
	public void toXml(Element element) throws XmlParserException {
		// 设置connection的name属性
		element.setAttribute("name", name);
		
		// 设置所有的property
		Enumeration<String> enums = (Enumeration<String>) props.propertyNames();
		String key = null;
		String val = null;
		Element tmpElement = null;
		Document doc = XmlUtil.newDocument();
		while(enums.hasMoreElements()) {
			key = enums.nextElement();
			val = props.getProperty(key);
			tmpElement = doc.createElement("property");
			tmpElement.setAttribute("name", key);
			tmpElement.setTextContent(val);
			element.appendChild(tmpElement);
		}
		
		doc = null;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

}
