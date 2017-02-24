/*
 * @(#)SystemProperties.java  2009-2-19
 *
 * Copyright (c) 2009 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A utility class for processing Properties
 * 
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 */
public class PropUtil {

	private static final Log log = LogFactory.getLog(PropUtil.class);

	/** if value of a key is null,than use this value instead */
	public static final String UNKNOWN_VALUE = "unknown";

	/** Holds configuration objects */
	private static Map<String, Configuration> map = new HashMap<String, Configuration>();

	/** The filename of default configuration file */
	private static final String DEFAULT_CONFIG_FILE = "config";
	
	private PropUtil() {
	}

	/**
	 * Gets system property value by key,return value <code>valIfNull</code> if
	 * null.
	 * 
	 * @param key the key
	 * @return
	 */
	public static String getSystemProperty(String key, String valIfNull) {
		String val = getSystemProperty(key);
		return val == null ? valIfNull : val;
	}

	/**
	 * <p>
	 * Gets a System property, defaulting to <code>null</code> if the property
	 * cannot be read.
	 * </p>
	 * 
	 * <p>
	 * If a <code>SecurityException</code> is caught, the return value is
	 * <code>null</code> and a message is written to <code>System.err</code>.
	 * </p>
	 * 
	 * @param property the system property name
	 * @return the system property value or <code>null</code> if a security
	 *         problem occurs
	 */
	public static String getSystemProperty(String property) {
		try {
			return System.getProperty(property);
		} catch (SecurityException ex) {
			// we are not allowed to look at this property
			System.err.println("Caught a SecurityException reading the system property '"
							+ property
							+ "'; the SystemUtils property value will default to null.");
			return null;
		}
	}

	/**
	 * Loads Properties from a file.
	 * 
	 * @param filepath The target file's path
	 * @return java.util.Properties
	 */
	public static Properties load(String filepath) {
		if (null == filepath || 0 == filepath.length()) {
			throw new IllegalArgumentException("the path must not be empty.");
		}
		File file = new File(filepath);
		return load(file);
	}
	
	public static Properties load(File file) {
		AssertUtil.notNull(file, "File [" + file + "] is null, but it is expected not null.");
		if (!(file.exists() && file.isFile())) {
			throw new IllegalArgumentException("the path [" + file.getAbsolutePath()
					+ "] is not a correct file path.");
		}

		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(file);
			prop.load(input);
		} catch (IOException e) {
			if (log.isWarnEnabled()) {
				log.warn("load file [" + file.getAbsolutePath() + "] failed.");
			}
		} finally {
			CommonUtil.close(input);
		}
		return prop;
	}
	
	/**
     * Loads properties from a classpath resource
     * 
     * @param props
     * @param resource e.g. => conf/props/test.properties
     * @param throwExceptionIfNotFound
     * @return loaded properties
     */
	public static Properties loadFromClassPath(String resource,
			boolean throwExceptionIfNotFound) {
		URL url = PropUtil.class.getClassLoader().getResource(resource);
		if (url == null) {
			if (throwExceptionIfNotFound) {
				throw new IllegalStateException(
						"Could not find classpath properties resource: "
								+ resource);
			} else {
				return new Properties();
			}
		}
		try {
			Properties props = new Properties();
			InputStream is = url.openStream();
			try {
				props.load(url.openStream());
			} finally {
				is.close();
			}
			return props;
		} catch (IOException e) {
			throw new RuntimeException(
					"Could not read properties at classpath resource: "
							+ resource, e);
		}
	}
	
	public static Properties setValue(String filepath, String key, String value) throws IOException {
		File file = new File(filepath);
		if(!file.exists()) {
			throw new RuntimeException("The properties file [" + filepath + "] doesn't exist.");
		}
		FileOutputStream out = null;
		try {
			Properties p = new Properties();
			p.load(new FileInputStream(file));
			p.setProperty(key, value);
			// save to file
			out = new FileOutputStream(file);
			p.store(out, "Modified on " + new Date());
			
			return p;
		} finally {
			CommonUtil.close(out);
		}
	}
	
	/**
	 * Merges multiple {@link Properties} instances into one
	 * 
	 * @param mode merge mode
	 * @param sources properties to merge
	 * @return new instance of {@link Properties} containing merged values
	 */
	public static Properties merge(MergeMode mode, Properties... sources) {
		Properties props = new Properties();

		for (int i = 0; i < sources.length; i++) {
			final Properties source = sources[i];
			for (Entry<Object, Object> prop : source.entrySet()) {
				final boolean exists = props.containsKey(prop.getKey());
				boolean set = false;
				switch (mode) {
					case MERGE:
						set = true;
						break;
					case OVERRIDE_ONLY:
						set = exists;
						break;
				}
				if (set || i == 0) {
					props.put(prop.getKey(), prop.getValue());
				}
			}
		}
		return props;
	}

	/**
	 * Loads the default properties file.
	 * 
	 * @return
	 */
	public synchronized static Configuration getConfiguration() {
		return getConfiguration(DEFAULT_CONFIG_FILE);
	}

	/**
	 * Loads a specified properties file.
	 * <p>
	 * For example,if properties file 'database.properties' is in the package
	 * "com.jsoft.rs", you can do as following:
	 * 
	 * <pre>
	 * String val = PropUtil.getConfiguration(&quot;com/jsoft/rs/database&quot;).getValue(
	 * 		&quot;database.username&quot;);
	 * </pre>
	 * 
	 * @param config The bundle properties file's path
	 * @return Configuration object
	 */
	public synchronized static Configuration getConfiguration(String config) {
		Configuration configuration = map.get(config);
		if (null == configuration) {
			configuration = new Configuration(config);
			map.put(config, configuration);
		}
		return configuration;
	}
	
	/**
     * Merge mode
     */
    public static enum MergeMode {
        OVERRIDE_ONLY,
        MERGE
    }

	public static class Configuration {
		private ResourceBundle resourceBundle;

		public Configuration(String config) {
			resourceBundle = ResourceBundle.getBundle(config);
		}

		public String getValue(String key) {
			return (resourceBundle.getString(key));
		}
		
		public byte byteValue(String key) {
			return Byte.parseByte(getValue(key));
		}
		
		public short shortValue(String key) {
			return Short.parseShort(getValue(key));
		}
		
		public int intValue(String key) {
			return Integer.parseInt(getValue(key));
		}
		
		public long longValue(String key) {
			return Long.parseLong(getValue(key));
		}
		
		public float floatValue(String key) {
			return Float.parseFloat(getValue(key));
		}
		
		public double doubleValue(String key) {
			return Double.parseDouble(getValue(key));
		}
		
		public boolean boolValue(String key) {
			return "true".equals(getValue(key));
		}
	}

	public static void main(String[] args) throws IOException {
		Properties p = System.getProperties();
		p.setProperty("压抑", "哈哈");
		p.list(System.out);

		FileOutputStream out = new FileOutputStream(new File(
				"C:/Documents and Settings/jadmin/桌面/utf8.txt"));
		p.store(out, null);
		
		System.out.println(System.getProperty("java.io.tmpdir"));
	}
}
