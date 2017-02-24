/*
 * @(#)Dialect.java	2010-4-20
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.jdbc.sql;

import java.lang.reflect.Field;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.javaclub.jorm.Environment;
import com.github.javaclub.jorm.JormException;
import com.github.javaclub.jorm.config.JdbcConfigXmlParser;
import com.github.javaclub.jorm.jdbc.ClassMetadata;
import com.github.javaclub.jorm.jdbc.JdbcException;
import com.github.javaclub.jorm.jdbc.criterion.Order;
import com.github.javaclub.jorm.jdbc.sql.util.SqlUtil;

/**
 * Dialect
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: Dialect.java 539 2011-10-09 03:48:18Z gerald.chen.hz $
 */
public abstract class Dialect {
	
	protected static final Log LOG = LogFactory.getLog(Dialect.class);
	
	public static final int DEFAULT_BATCH_SIZE = 50;
	
	public static final String FROM = "FROM";
	public static final String SELECT = "SELECT";
	public static final String INSERT = "INSERT";
	public static final String DELETE = "DELETE";
	public static final String ORDER_BY = "ORDER BY";
	
	private Properties nativeProperties;
	
	protected Dialect() {
	}
	
	public static Dialect getDefaultDialect() throws JormException {
		Properties props = JdbcConfigXmlParser.getDefaultJdbcPropertity();
		if(props != null) {
			return getDialect(props.getProperty(Environment.DIALECT));
		}
		throw new JormException("The default jdbcPropertity is null.");
	}
	
	public static Dialect getDialect(String dialectName) throws JormException {
		return DialectFactory.create(dialectName);
	}
	
	/**
	 * Get a sql that calculate the record count.
	 * 
	 * @param sql the target sql
	 * @return a sql string
	 */
	public String recordCountSql(final String sql) {
		String upper = sql.toUpperCase();
		if(upper.indexOf("DISTINCT") > -1) {
			return "SELECT COUNT(*) AS num FROM (" + SqlUtil.convert(sql) + ")";
		}
		int pos = upper.indexOf("FROM ");
		return "SELECT COUNT(*) AS num " + sql.substring(pos);
	}
	
	public String maxIdSql(Class<?> clazz, Field keyField) {
		String keyCol = AnnotationModelHelper.getColumName(keyField);
		String sql = "SELECT MAX(" + keyCol + ") FROM "
				+ ClassMetadata.getClassMetadata(clazz).tableName;
		return sql;
	}
	
	/**
	 * Create a {@link com.github.javaclub.jorm.jdbc.sql.JoinFragment} strategy responsible
	 * for handling this dialect's variations in how joins are handled.
	 *
	 * @return This dialect's {@link com.github.javaclub.jorm.jdbc.sql.JoinFragment} strategy.
	 * @since 1.0.7
	 */
	public JoinFragment createOuterJoinFragment() {
		return new ANSIJoinFragment();
	}
	
	protected <T> String generateSql(SqlParams<T> sqlParams, boolean annotationClass) {
		StringBuilder sbf = new StringBuilder(sqlParams.getSql(false));
		Set<Order> orders = sqlParams.getOrders();
		if(!orders.isEmpty()) {
			if (null == sqlParams.getObjectClass()) {
				throw new IllegalStateException(
						"the objectClass is null, if Order is in SqlParams, the objectClass needs to be initialized.");
			}
			String baseSql = sqlParams.getSql(false);
			if(sqlParams.hasOrderBy()) {
				baseSql = sqlParams.getSqlNoOrderBy();
				sbf.setLength(0);
				sbf.append(baseSql);
			}
			sbf.append(" ORDER BY");
			int i = 0;
			for (Order order : orders) {
				if(i > 0) {
					sbf.append(",");
				}
				if(annotationClass) {
					sbf.append(" " + order.toColumnString(sqlParams.getObjectClass()));
				} else {
					sbf.append(" " + order.toString());
				}
				
				i++;
			}
		}
		try {
			return sbf.toString();
		} finally {
			sbf.setLength(0);
			sbf = null;
		}
	}
	
	public <T> SqlParams<T> listSqlString(SqlParams<T> sqlParams) {
		if (null == sqlParams.getObjectClass()) {
			throw new IllegalStateException(
					"the objectClass is null, if Order is in SqlParams, the objectClass needs to be initialized.");
		}
		SqlParams<T> r = sqlParams.copy();
		String t_sql = this.generateSql(sqlParams, true);
		if(sqlParams.getMaxResults() > 0) {
			t_sql = pageable(t_sql, 0, sqlParams.getMaxResults());
		}
		r.setSql(t_sql);
		
		return r;
	}
	
	/**
	 * Does this dialect support sequences?
	 *
	 * @return True if sequences supported; false otherwise.
	 */
	public boolean supportsSequences() {
		return false;
	}
	
	/**
	 * Does this dialect support some form of limiting query results
	 * via a SQL clause?
	 *
	 * @return True if this dialect supports some form of LIMIT.
	 */
	public boolean supportsLimit() {
		return false;
	}
	
	/**
	 * Does the database support DDL statement's transaction rollback.
	 *
	 * @return
	 */
	public boolean supportDdlRollback() {
		return false;
	}
	
	/**
	 * Tests if the native database support the specified feture, the <tt>fetureKey</tt> 
	 * must be {@link Environment.SUPPORT_XXX}.
	 *
	 * @param fetureKey the specified feture key
	 * @return true if support the specified feture, false otherwise
	 */
	public boolean supportSpecifiedFeture(String fetureKey) {
		Properties p = getNativeProperties();
		if(null == p.get(fetureKey)) {
			return false;
		}
		return Boolean.parseBoolean((String) p.get(fetureKey));
	}
	
	/**
	 * Generate the appropriate select statement to to retreive the next value
	 * of a sequence.
	 * <p/>
	 * This should be a "stand alone" select statement.
	 *
	 * @param sequenceName the name of the sequence
	 * @return String The "nextval" select string.
	 * @throws JdbcException If sequences are not supported.
	 */
	public String sequenceNextValSql(String sequenceName) throws JdbcException {
		throw new JdbcException( "Dialect does not support sequences" );
	}
	
	/**
	 * Get the command used to select a GUID from the underlying database.
	 * <p/>
	 * Optional operation.
	 *
	 * @return The appropriate command string.
	 */
	public String getSelectGuidSql() {
		throw new UnsupportedOperationException( "Dialect does not support GUIDs" );
	}
	
	public String ddlIdentityColumn() {
		throw new UnsupportedOperationException( "Dialect does not support generate a identity column." );
	}
	
	public String ddlFieldColumn(Class<?> entityType) {
		throw new UnsupportedOperationException( "Dialect does not support generate a identity column." );
	}
	
	
	/**
	 * Get the command used to select the next generated increment id value.
	 *
	 * @param clazz
	 * @return command sql string.
	 */
	public String nextIdentitySql(String dbname, Class<?> clazz) {
		throw new UnsupportedOperationException( "Dialect does not support next generated increment sql." );
	}
	
	public String identityIncrementStep(String dbname, Class<?> clazz) {
		throw new UnsupportedOperationException( "Dialect does not support increment identity." );
	}
	
	public <T> SqlParams<T> loadEntityParams(SqlParams<T> sqlParams, boolean annotationClass) {
		throw new UnsupportedOperationException( "The method => loadBeansSqlString(SqlParams) had not been implemented yet." );
	}
	
	public Properties getNativeProperties() {
		return this.nativeProperties;
	}

	public void setNativeProperties(Properties nativeProperties) {
		this.nativeProperties = nativeProperties;
	}
	
	/**
	 * 生成分页sql语句
	 *
	 * @param sql   原sql语句(不带分页参数)
	 * @param start 从0开始
	 * @param limit 取多少条记录
	 * @return 分页sql语句
	 */
	public abstract String pageable(final String sql, int start, int limit);
	public abstract <T> SqlParams<T> pageable(SqlParams<T> sqlParams);
	
}
