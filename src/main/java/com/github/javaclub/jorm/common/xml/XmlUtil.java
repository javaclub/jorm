/*
 * @(#)XmlUtil.java	2010-1-24
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.common.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A utility class for Xml procesing.
 * 
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: XmlUtil.java 66 2011-06-27 03:21:59Z gerald.chen.hz@gmail.com $
 */
public class XmlUtil {

	protected static final Log logger = LogFactory.getLog(XmlUtil.class);

	/**
	 * constructor: forbid to call
	 */
	protected XmlUtil() {
		super();
	}

	public final static org.w3c.dom.Element rootElement(File file) throws XmlParserException {
		org.w3c.dom.Document doc = getDocument(file);
		return doc.getDocumentElement();
	}

	/**
     * 通过xml文件路径获取一个Document文档对象
     * 
     * @param xmlPath XML文件的路径
     * @return doc 该XML文件的Document对象
     * @throws XmlParserException
     */
    public static org.w3c.dom.Document getDocument(String xmlPath) throws XmlParserException {
        return getDocument(new File(xmlPath));
    }
    
    /**
     * 读取file对象并分析，返回一个Document对象
     * 
     * @param file 需要分析的文件
     * @return doc Document对象
     * @throws XmlParserException
     */
    public static org.w3c.dom.Document getDocument(File file) throws XmlParserException {
        DocumentBuilderFactory factory = null; DocumentBuilder builder = null;
        try {
        	factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			return builder.parse(file);
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
			throw new XmlParserException("ParserConfigurationException occured.", pce);
		} catch (SAXException saxe) {
			saxe.printStackTrace();
			throw new XmlParserException("SAXException occured.", saxe);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new XmlParserException("IOException occured.", ioe);
		}
    }
    
    /**
     * 在内存中创建一个Document文档对象
     * 
     * @return doc
     * @throws XmlParserException
     */
    public static org.w3c.dom.Document newDocument() throws XmlParserException {
        DocumentBuilderFactory factory = null; DocumentBuilder builder = null;
        try {
        	factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			return builder.newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new XmlParserException("ParserConfigurationException occured.", e);
		} finally {
			builder = null;
			factory = null;
		}
    }
    
    public static org.w3c.dom.Element createElement(String tagName) throws XmlParserException {
    	org.w3c.dom.Document doc = newDocument();
    	try {
    		return doc.createElement(tagName);
		} finally {
			doc = null;
		}
    }
    
    /**
     * 该方法用于将doc文件保存至xmlPath, 将内存中的整个Document保存至路径
     * @param doc 已经读入内存的document文档对象
     * @param xmlPath 保存路径
     * @throws XmlParserException
     */
    public static void saveDocument(Document doc, String xmlPath) throws XmlParserException {
        saveDocument(doc, new File(xmlPath));
    }
    
    /**
     * 该方法用于将doc文件保存至file, 将内存中的整个Document保存至file所在的路径
     * 
     * @param doc 已经读入内存的document文档对象
     * @param file 保存的文件对象
     * @throws javax.xml.transform.TransformerConfigurationException
     * @throws javax.xml.transform.TransformerException
     */
    public static void saveDocument(Document doc, File file) throws XmlParserException {
        TransformerFactory tFactory = null;
        Transformer transformer = null;
        try {
        	tFactory = TransformerFactory.newInstance();
			transformer = tFactory.newTransformer();  
			transformer.transform(new DOMSource(doc), new StreamResult(file));
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			throw new XmlParserException("TransformerConfigurationException occured.", e);
		} catch (TransformerException e) {
			e.printStackTrace();
			throw new XmlParserException("TransformerException occured.", e);
		} finally {
			tFactory = null;
			transformer = null;
		}
    }
    
    public static String selectText(String xml, String xpath) throws XmlParserException {
		XPathFactory factory = XPathFactory.newInstance();
		javax.xml.xpath.XPath xp = factory.newXPath();
		try {
			Node node = (Node) xp.evaluate(xpath, getDocument(xml), XPathConstants.NODE);
			return node.getTextContent();
		} catch (ClassCastException e) {
			throw new IllegalArgumentException(
					"Found result is not text, ensure that you have the right expression which evaluates to text.");
		} catch (Exception e) {
			throw new XmlParserException(e);
		} finally {
			xp = null;
			factory = null;
		}
	}

	public static void main(String[] args) {
		File file = new File("E:/workspace/java/opensource/javaclub/gerald-jorm/src/test/resources/conf/jdbc.cfg.xml");
		org.w3c.dom.Element element = rootElement(file);
		NodeList nodes = element.getElementsByTagName("connections");
		System.out.println(((org.w3c.dom.Element) nodes.item(0)).getAttribute("default"));
	}

}
