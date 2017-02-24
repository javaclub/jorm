/*
 * @(#)IXMLSupporter.java	2010-1-24
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.common.xml;

import org.w3c.dom.Element;

/**
 * desc
 * 
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: Xmlizable.java 66 2011-06-27 03:21:59Z gerald.chen.hz@gmail.com $
 */
public interface Xmlizable {

	/**
	 * write the object properties in the specified XML element
	 * 
	 * @param element XML element
	 * @throws XmlParserException if encounter errors
	 */
	public void toXml(Element element) throws XmlParserException;

	/**
	 * import object properties from a XML element
	 * 
	 * @param element XML element to import
	 * @throws XmlParserException if encounter errors
	 */
	public void fromXml(Element element) throws XmlParserException;
	
}
