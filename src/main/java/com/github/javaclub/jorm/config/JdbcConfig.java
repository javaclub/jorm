/*
 * @(#)JdbcConfig.java	2010-4-20
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.common.xml.XmlParserException;
import com.github.javaclub.jorm.common.xml.XmlUtil;
import com.github.javaclub.jorm.common.xml.Xmlizable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A entity bean for jorm.cfg.xml.
 * 
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: JdbcConfig.java 1302 2012-01-07 14:42:38Z gerald.chen.hz $
 */
public class JdbcConfig implements Xmlizable, Serializable {

	private static final long serialVersionUID = -5427263378441228963L;

	private boolean showSql = false;

	private String defaultConnectionName;
	
	private Map<String, ConstantElement> constantElements = new HashMap<String, ConstantElement>();
	
	private Map<String, ConnectionElement> connectionElements = new HashMap<String, ConnectionElement>();
	
	public JdbcConfig() {
		super();
	}

	public void fromXml(Element element) throws XmlParserException {
		NodeList nodes = element.getElementsByTagName("constant");
		ConstantElement ce = null;
		Element elem = null;
		for (int i = 0; i < nodes.getLength(); i++) {
			elem = (Element) nodes.item(i);
			ce = new ConstantElement();
			ce.fromXml(elem);
			if (Strings.equals("show_sql", ce.getName()) && Strings.equalsIgnoreCase("true", ce.getValue())) {
				this.setShowSql(true);
			}
			this.constantElements.put(ce.getName(), ce);
		}

		Element connections = (Element) element.getElementsByTagName("connections").item(0);
		this.setDefaultConnectionName(connections.getAttribute("default"));

		nodes = element.getElementsByTagName("connection");
		ConnectionElement connectionElement = null;
		for (int i = 0; i < nodes.getLength(); i++) {
			elem = (Element) nodes.item(i);
			connectionElement = new ConnectionElement();
			connectionElement.fromXml(elem);
			this.connectionElements.put(connectionElement.getName(), connectionElement);
		}
	}

	public void toXml(Element element) throws XmlParserException {
		// constant元素处理
		Set<Entry<String, ConstantElement>> constantEntries = this.constantElements.entrySet();
		Element constantElem = null;
		ConstantElement constantObj = null;
		Document doc = XmlUtil.newDocument();
		for (Entry<String, ConstantElement> entry : constantEntries) {
			constantObj = entry.getValue();
			constantElem = doc.createElement("constant");
			constantObj.toXml(constantElem);
			element.appendChild(constantElem);
		}

		// default connection
		Element connections = doc.createElement("connections");
		connections.setAttribute("default", this.defaultConnectionName);
		
		// connection元素处理
		Set<Entry<String, ConnectionElement>> connectionEntries = this.connectionElements.entrySet();
		Element connectionElem = null;
		ConnectionElement connectionObj = null;
		for (Entry<String, ConnectionElement> entry : connectionEntries) {
			connectionObj = entry.getValue();
			connectionElem = doc.createElement("connection");
			connectionObj.toXml(connectionElem);
			connections.appendChild(connectionElem);
		}
		element.appendChild(connections);
		doc = null;
	}

	public boolean isShowSql() {
		return showSql;
	}
	
	private void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}

	public String getDefaultConnectionName() {
		return defaultConnectionName;
	}

	public void setDefaultConnectionName(String defaultConnectionName) {
		this.defaultConnectionName = defaultConnectionName;
	}
	
	public ConstantElement getConstantElement(String namedKey) {
		return this.constantElements.get(namedKey);
	}
	
	public ConnectionElement getConnectionElement(String namedKey) {
		return this.connectionElements.get(namedKey);
	}

}
