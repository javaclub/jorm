/*
 * @(#)JdbcConfigXmlParser.java	2010-4-20
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.config;

import java.io.File;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.javaclub.jorm.common.CommonUtil;
import com.github.javaclub.jorm.common.PropUtil;
import com.github.javaclub.jorm.common.xml.XmlParserException;
import com.github.javaclub.jorm.common.xml.XmlUtil;
import com.github.javaclub.jorm.jdbc.JdbcSession;

/**
 * The parser for jdbc.cfg.xml.
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: JdbcConfigXmlParser.java 558 2011-10-10 03:19:30Z gerald.chen.hz $
 */
public class JdbcConfigXmlParser {
	
	/** Logger for this class */
	protected static final Log LOG = LogFactory.getLog(JdbcConfigXmlParser.class);
	
	public static final String JDBC_CFG_PATH_DEFAULT = "jorm.cfg.xml";
	
	private static final String JDBC_CONFIG_PATH_KEY = "jdbc.config.path";
	
	private static org.w3c.dom.Element root;
	private static JdbcConfig config;
	
	static {
		if(LOG.isInfoEnabled()) {
			LOG.info("Memory before initialized: free/total="
					+ (Runtime.getRuntime().freeMemory() / 1024f) + "KB/"
					+ (Runtime.getRuntime().totalMemory() / 1024f) + "KB \t Memory occupancy rate="
					+ (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
					/ (float) Runtime.getRuntime().totalMemory());
		}
		try {
			String jdbcConfigPath = getJdbcConfigPath();
			if(LOG.isDebugEnabled()) {
				LOG.debug("Initializing jdbc config file -> [" + jdbcConfigPath + "]");
			}
			File resource = CommonUtil.getClasspathFile(jdbcConfigPath);
			root = XmlUtil.rootElement(resource);
			config = new JdbcConfig();
			config.fromXml(root);
			if(LOG.isDebugEnabled()) {
				LOG.debug("JdbcConfigXmlParser engine finished.");
			}
		} catch (Exception e) {
			LOG.error(JdbcConfigXmlParser.class.getName() + " failed to parse the jdbc.cfg.xml", e);
			throw new XmlParserException(JdbcConfigXmlParser.class.getName() + " failed to parse the jdbc.cfg.xml", e);
		}
	}
	
	public static String getJdbcConfigPath() {
		String path = JDBC_CFG_PATH_DEFAULT;
		try {
			path = PropUtil.getConfiguration().getValue(JDBC_CONFIG_PATH_KEY);
		} catch (Exception e) {
			path = JDBC_CFG_PATH_DEFAULT;
			if(null == CommonUtil.getClasspathFile(path)) {
				throw new IllegalStateException("The file => " + path + " is not found in classpath.");
			}
		}
		return path;
	}
	
	public static String getDefaultConnectionName() {
		return config.getDefaultConnectionName();
	}
	
	public static Properties getDefaultJdbcPropertity() {
		return getJdbcPropertity(getDefaultConnectionName());
	}
	
	public static Properties getJdbcPropertity(String name) {
		if(null == config.getConnectionElement(name)) {
			throw new RuntimeException("the connection name[" + name + "] dose not exsits.");
		}
		return config.getConnectionElement(name).getProps();
	}
	
	public static ConstantElement constant(String namedValue) {
		return config.getConstantElement(namedValue);
	}
	
	public static boolean isShowSql() {
		return config.isShowSql();
	}
	
	public static boolean isFormatSql() {
		if(null != constant("format_sql")) {
			return constant("format_sql").booleanValue();
		}
		return false;
	}
	
	public static long getCheckDelay() {
		if(null != constant("session.monitor.check_delay")) {
			return constant("session.monitor.check_delay").longValue();
		}
		return JdbcSession.TIME_CHECK_DELAY;
	}
	
	public static long getSessionLifetime() {
		if(null != constant("session.lifetime.auto_close")) {
			return constant("session.lifetime.auto_close").longValue();
		}
		return JdbcSession.SESSION_LIFE_TIME;
	}

	public static void main(String[] args) {

	}

}
