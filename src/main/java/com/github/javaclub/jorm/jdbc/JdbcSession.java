/*
 * @(#)JdbcSession.java	2010-4-21
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.jdbc;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.javaclub.jorm.Environment;
import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.JormException;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.annotation.Entity;
import com.github.javaclub.jorm.annotation.PK;
import com.github.javaclub.jorm.annotation.constant.GenerationType;
import com.github.javaclub.jorm.common.Annotations;
import com.github.javaclub.jorm.common.CommonUtil;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.config.JdbcConfigXmlParser;
import com.github.javaclub.jorm.id.IdentifierGeneratorFactory;
import com.github.javaclub.jorm.jdbc.batch.JdbcBatcher;
import com.github.javaclub.jorm.jdbc.batch.JdbcBatcherImpl;
import com.github.javaclub.jorm.jdbc.callable.ProcedureCaller;
import com.github.javaclub.jorm.jdbc.connection.ConnectionManager;
import com.github.javaclub.jorm.jdbc.connection.DBConnection;
import com.github.javaclub.jorm.jdbc.sql.Dialect;
import com.github.javaclub.jorm.jdbc.sql.DialectFactory;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;
import com.github.javaclub.jorm.jdbc.sql.SqlPrepared;
import com.github.javaclub.jorm.jdbc.sql.util.SqlUtil;
import com.github.javaclub.jorm.jdbc.work.IsolatedWork;
import com.github.javaclub.jorm.jdbc.work.Isolater;
import com.github.javaclub.jorm.jdbc.work.internal.AssociatedSavingWork;
import com.github.javaclub.jorm.proxy.JormProxy;
import com.github.javaclub.jorm.proxy.ProxyFactory;


/**
 * A jdbc implmentation for Session.
 * 
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: JdbcSession.java 1291 2012-01-06 16:38:58Z gerald.chen.hz@gmail.com $
 */
@SuppressWarnings("unchecked")
public class JdbcSession implements Session {
	
	/** Logger for this class */
	protected static final Log LOG = LogFactory.getLog(JdbcSession.class);

	/** Every batch operation's default record size */
	public static final int DEFAULT_PER_BATCH_SIZE = 50;
	
	/** The default delay between every time check, set to 60 seconds. */
	public static final long TIME_CHECK_DELAY = 60000L;
	
	/** The auto-close session's lifetime, time out to close the session.  */
	public static final long SESSION_LIFE_TIME = 1000 * 60 * 60L;
	
	private final Object lock = new Object();
	private boolean isClosed;
	private boolean autoClose = false;
	
	private BeanPersister persister;
	private JdbcBatcher batcher;

	private Dialect dialect;
	private Connection conn;
	private Statement stmt;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	/** session starting timestamp */
	protected long startTimestamp;
	protected Thread monitor;
	
	public JdbcSession() {
		try {
			initialize(JdbcConfigXmlParser.getDefaultConnectionName());
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			throw new JormException(e);
		}
	}

	private void initialize(String providerName) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Initializing the DBConnection ...");
		}
		DBConnection dbConnImpl = DBConnection.getImplementation(providerName);
		if(dbConnImpl == null) {
			throw new JormException("There is no database implmentation of [" + providerName + "]");
		}
		Properties jdbcPrpos = dbConnImpl.getJdbcProperties();
		if (null == jdbcPrpos) {
			throw new JormException(
					"Failed to initialized the database dialect.");
		}
		this.conn = dbConnImpl.getConnection();
		this.isClosed = false;
		if (LOG.isDebugEnabled()) {
			LOG.debug("Jorm framework show_sql -> " + JdbcConfigXmlParser.isShowSql());
		}
		String dialect = jdbcPrpos.getProperty(Environment.DIALECT);
		if (Strings.isEmpty(dialect)) {
			throw new JormException("The property of ["
					+ Environment.DIALECT + "] is not setted in jdbc.cfg.xml file.");
		}
		initDialect(dialect);
		this.persister = new BeanPersister(this);
		this.startTimestamp = System.currentTimeMillis();
		this.startMonitor();
	}

	protected Thread getMonitor() {
		return this.monitor;
	}

	private void initDialect(String dialect) throws JdbcException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Initializing the database dialect => " + dialect);
		}
		this.dialect = DialectFactory.create(dialect);
		this.initializedDBProperties();
	}

	private JdbcSession(String providerName, boolean autoClose) {
		try {
			this.autoClose = autoClose;
			initialize(providerName);
		} catch (Exception e) {
			LOG.error("Jorm JdbcSession initialize error " + e.getMessage(), e);
			throw new JormException(e);
		}
	}

	public static Session getSession() {
		return getSession(JdbcConfigXmlParser.getDefaultConnectionName());
	}

	public static Session getSession(String providerName) {
		return getSession(providerName, false);
	}
	
	public static Session getSession(String providerName, boolean autoClose) {
		return new JdbcSession(providerName, autoClose);
	}

	public <T> List<T> all(Class<T> objectClass) throws JdbcException {
		String tbname = ClassMetadata.getClassMetadata(objectClass).tableName;
		if (Strings.isEmpty(tbname)) {
			throw new JdbcException("The type [" + objectClass
					+ "] doesn't have the annotaion Table.");
		}
		return list(objectClass, SqlPrepared.preparedSelectAll(objectClass));
	}
	
	public Serializable save(Object object) throws JdbcException {
		Serializable pkIdValue;
		ClassMetadata metadata = this.validate(object);
		if(!metadata.hasAssociated()) {
			synchronized(this.lock) {
				pkIdValue = persister.getIdentifierValue(object);
				this.executeUpdate(persister.insert(object));
			}
		} else {
			if(null == metadata.identifierField) {
				throw new JdbcException("The annotation @Id can't be found.");
			}
			AssociatedSavingWork work = new AssociatedSavingWork(object, metadata);
			Isolater.doStepWork(work, this);
			pkIdValue = work.getIdentifier();
		}
		try {
			return pkIdValue;
		} finally {
			pkIdValue = null;
			metadata = null;
		}
	}

	protected ClassMetadata validate(Object object) throws JdbcException {
		if(null == object) {
			throw new JdbcException("The persitent entity should not be null.");
		}
		if(null == Annotations.findAnnotation(object.getClass(), Entity.class)) {
			throw new JdbcException("The annotation @Entity is not found on the target class => " +
					object.getClass().getName());
		}
		ClassMetadata metadata = ClassMetadata.getClassMetadata(object.getClass());
		if((!Strings.equals(GenerationType.ASSIGNED, metadata.identityStrategy())) 
			&& IdentifierGeneratorFactory.isFieldInitialized(metadata.identifierField, object)) {
			throw new JdbcException("This entity => " + object + " had been persisted maybe.");
		}
		return metadata;
	}
	
	public <T> void update(T object) throws JdbcException {
		SqlParams<T> sqlParams = SqlPrepared.preparedUpdate(object);
		if(sqlParams != null) {
			this.executeUpdate(sqlParams);
		}
	}
	
	public void saveOrUpdate(Object object) throws JdbcException {
		boolean contains = has(object);
		if (contains) {
			update(object);
		} else {
			save(object);
		}
	}
	
	public boolean isAutoClose() {
		return this.autoClose;
	}

	public <T> int clean(Class<T> objectClass) throws JdbcException {
		return this.executeUpdate(SqlPrepared.preparedDeleteAll(objectClass));
	}
	
	public int clean(String tbname) throws JdbcException {
		return this.executeUpdate("DELETE FROM " + tbname);
	}

	public <T> boolean has(T object) throws JdbcException {
		SqlParams<T> sqlParams = SqlPrepared.preparedContains(object);
		return (this.count(sqlParams) > 0);
	}

	public <T> void delete(T object) throws JdbcException {
		SqlParams<T> sqlParams = SqlPrepared.preparedDelete(object);
		this.executeUpdate(sqlParams);
		if(Jorm.isProxy(object)) {
			((JormProxy) object).getLazyInitializer().setImplementation(null);
		}
	}
	
	public int delete(String sql) throws JdbcException {
		return this.executeUpdate(new SqlParams(sql));
	}
	
	public int delete(SqlParams params) throws JdbcException {
		return this.executeUpdate(params);
	}

	public <T> int delete(Class<T> objectClass, String whereFragment)
			throws JdbcException {
		String tbname = ClassMetadata.getClassMetadata(objectClass).tableName;;
		final String sql = "DELETE FROM " + tbname + " WHERE " + whereFragment;
		return this.executeUpdate(sql);
	}

	public <T> T read(Class<T> objectClass, Serializable id)
			throws JdbcException {
		ClassMetadata metadata = ClassMetadata.getClassMetadata(objectClass);
    	if(metadata.isEntityLazy()) {
    		ProxyFactory proxyFactory = Environment.getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
    		HashSet interfaces = new HashSet();
    		interfaces.add(JormProxy.class);
    		proxyFactory.postInstantiate(objectClass, interfaces, metadata.getIdentifierMethod, metadata.setIdentifierMethod);
    		return (T) proxyFactory.getProxy(id, this);
    	} else {
    		return this.read(objectClass, new Object[] { id });
    	}
		
	}

	public <T> T read(Class<T> objectClass, Object... keyValues) throws JdbcException {
		String tbname = ClassMetadata.getClassMetadata(objectClass).tableName;
		if (CommonUtil.isEmpty(keyValues)) {
			return loadFirst(objectClass, "SELECT * FROM " + tbname);
		}
		PK pk = Annotations.findAnnotation(objectClass, PK.class);
		if(pk == null) {
			throw new JdbcException("The annotation @PK isn't defined in class [" + objectClass.getName() + "]");
		}
		String[] pkFieldNames = pk.value();
		if (CommonUtil.length(keyValues) != CommonUtil.length(pkFieldNames)
				|| CommonUtil.isEmpty(pkFieldNames)) {
			throw new JdbcException(
					"the primary key's count are not the with the value's count.");
		}
		SqlParams<T> sqlParams = SqlPrepared.preparedSelect(objectClass, pkFieldNames, keyValues);
		return this.loadFirst(sqlParams.setObjectClass(objectClass));
	}
	
	public <T> T loadFirst(SqlParams<T> sqlParams)
			throws JdbcException {
		final SqlParams<T> newParams = dialect.loadEntityParams(sqlParams, true);
		if (LOG.isDebugEnabled()) {
			LOG.debug(newParams.getSql(false));
		}
		try {
			pstmt = this.getPreparedStatement(conn, pstmt, newParams);
			rs = this.advance(pstmt.executeQuery(), newParams);
			if (rs.next()) {
				return persister.toBean(rs, newParams.getObjectClass(), newParams.isLoadAssociated());
			}
		} catch (SQLException e) {
			rethrow(e, newParams);
		}
		return null;
	}
	
	public <T> T loadFirst(Class<T> objectClass, String sql, Object... params)
			throws JdbcException {
		if (CommonUtil.isEmpty(params)) {
			return this.loadFirst(new SqlParams<T>(sql).setObjectClass(objectClass));
		}
		SqlParams<T> sqlParams = new SqlParams<T>(sql, params).setObjectClass(objectClass);

		return this.loadFirst(sqlParams);
	}

	public <T> List<T> loadBeans(SqlParams<T> sqlParams) throws JdbcException {
		SqlParams<T> newParams = dialect.loadEntityParams(sqlParams, false);
		if (LOG.isDebugEnabled()) {
			LOG.debug(newParams.getSql(false));
		}
		try {
			pstmt = this.getPreparedStatement(conn, pstmt, newParams);
			rs = this.advance(pstmt.executeQuery(), newParams);
			return persister.toBeanList(rs, newParams.getObjectClass(), sqlParams.isLoadAssociated());
		} catch (SQLException e) {
			rethrow(e, sqlParams);
		}
		return null;
	}

	public <T> List<T> list(SqlParams<T> params)
			throws JdbcException {
		final SqlParams<T> newParams = dialect.loadEntityParams(params, true);
		if (LOG.isDebugEnabled()) {
			LOG.debug(newParams.getSql(false));
		}
		try {
			pstmt = this.getPreparedStatement(conn, pstmt, newParams);
			rs = this.advance(pstmt.executeQuery(), newParams);
			return persister.toBeanList(rs, newParams.getObjectClass(), params.isLoadAssociated());
		} catch (SQLException e) {
			rethrow(e, newParams);
		}
		return null;
	}

	public <T> List<T> list(Class<T> objectClass, String sql, Object... params)
			throws JdbcException {
		if (CommonUtil.isEmpty(params)) {
			return this.list(new SqlParams<T>(sql).setObjectClass(objectClass));
		}
		SqlParams<T> sqlParams = new SqlParams<T>(sql, params).setObjectClass(objectClass);
		return this.list(sqlParams);
	}

	public boolean batchInsert(String sql, List<Object[]> datalist)
			throws JdbcException {
		return batchInsert(sql, datalist, JdbcSession.DEFAULT_PER_BATCH_SIZE);
	}

	public boolean batchInsert(String sql, List<Object[]> datalist,
			int perBatchSize) throws JdbcException {
		boolean result = false;
		Object[][] os = new Object[datalist.size()][];
		for (int i = 0; i <= os.length - 1; i++) {
			os[i] = datalist.get(i);
		}
		List<int[]> l = executeBatch(sql, os, perBatchSize);
		if (l.isEmpty())
			return result;
		for (Object oo : l) {
			int[] ia = (int[]) oo;
			for (int j = 0; j < ia.length; j++) {
				if (ia[j] < 1)
					return result;
			}
		}
		return !result;
	}

	public int delete(String tableName, Map<String, Object> conditions)
			throws JdbcException {
		int result = 0;
		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM ").append(tableName);
		if (conditions == null || conditions.isEmpty()) {
			result = executeUpdate(sql.toString());
		} else {
			Object[] condition = new Object[conditions.size()];
			sql.append(" WHERE ");
			int i = 0;
			for (Entry<String, Object> e : conditions.entrySet()) {
				sql.append(e.getKey()).append("=? AND ");
				condition[i++] = e.getValue();
			}
			sql.delete(sql.length() - 5, sql.length());
			result = executeUpdate(sql.toString(), condition);
		}
		return result;
	}

	public int[] executeBatch(String[] sqls) throws JdbcException {
		int[] results = null;
		boolean commit = false, rollback = false;
		if(getAutoCommit()) {
			setAutoCommit(false);
			commit = true;
		}
		try {
			stmt = conn.createStatement();
			for (int i = 0; i < sqls.length; i++) {
				if (JdbcConfigXmlParser.isShowSql()) {
					Jorm.format(sqls[i]);
				}
				stmt.addBatch(sqls[i]);
			}
			results = stmt.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
			rollback = true;
			if(commit) {
				rollback();
			}
			rethrow(e, "");
		} finally {
			if (commit) {
				if (!rollback) {
					this.commit();
				}
				setAutoCommit(true);
			}
		}
		return results;
	}

	public List<int[]> executeBatch(String sql, Object[] params,
			int perBatchSize) throws JdbcException {
		List<int[]> results = new ArrayList<int[]>();
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}
		if (perBatchSize == 0) {
			return results;
		}
		boolean commit = false, rollback = false;
		if(getAutoCommit()) {
			setAutoCommit(false);
			commit = true;
		}
		try {
			pstmt = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				pstmt.setObject(1, params[i]);
				pstmt.addBatch();
				if (i % perBatchSize == 0) {
					results.add(pstmt.executeBatch());
					pstmt.clearBatch();
				}
			}
			results.add(pstmt.executeBatch());
		} catch (Exception e) {
			e.printStackTrace();
			rollback = true;
			if(commit) {
				rollback();
			}
			throw new JdbcException("Errors occured while batching execute sql: " + sql, e);
		} finally {
			if (commit) {
				if (!rollback) {
					this.commit();
				}
				setAutoCommit(true);
			}
		}
		return results;
	}

	public List<int[]> executeBatch(String sql, Object[][] params,
			int perBatchSize) throws JdbcException {
		List<int[]> results = new ArrayList<int[]>();
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}
		if (perBatchSize == 0) {
			return results;
		}
		boolean commit = false, rollback = false;
		if(getAutoCommit()) {
			setAutoCommit(false);
			commit = true;
		}
		try {
			pstmt = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				for (int j = 0; j < params[i].length; j++) {
					pstmt.setObject(j + 1, params[i][j]);
				}
				pstmt.addBatch();
				if (i % perBatchSize == 0) {
					results.add(pstmt.executeBatch());
					if(JdbcConfigXmlParser.isShowSql()) {
						Jorm.format(sql);
					}
					pstmt.clearBatch();
				}
			}
			results.add(pstmt.executeBatch());
		} catch (Exception e) {
			e.printStackTrace();
			rollback = true;
			if(commit) {
				rollback();
			}
			throw new JdbcException("Errors occured while execute sql.", e);
		} finally {
			if (commit) {
				if (!rollback) {
					this.commit();
				}
				setAutoCommit(true);
			}
		}

		return results;
	}

	public int executeUpdate(String sql) throws JdbcException {
		return this.executeUpdate(sql, (Object[]) null);
	}

	public int executeUpdate(String sql, Object[] params) throws JdbcException {
		return this.executeUpdate(new SqlParams(sql, params));
	}

	public int executeUpdate(final SqlParams sqlParams) throws JdbcException {
		String sql = sqlParams.getSql(false);
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}
		class Work implements IsolatedWork {
			int rows;
			public void doWork(Connection connection) throws JdbcException {
				PreparedStatement statement = null;
				try {
					statement = getPreparedStatement(connection, statement, sqlParams);
					rows = statement.executeUpdate();
				} catch (SQLException sqle) {
					sqle.printStackTrace();
					rethrow(sqle, sqlParams);
				} finally {
					DBUtil.closeQuietly(statement);
				}
				
			}
			
			public Preference getPreference() {
				return Preference.CONNECTION;
			}

			public void doWork(Session session) throws JdbcException {
				// do nothing
			}
		}
		Work work = new Work();
		if(SqlUtil.isDdl(sql) && (!getDialect().supportDdlRollback())) {
			Isolater.doNonTransactedWork(work, this);
		} else {
			Isolater.doIsolatedWork(work, this);
		}
		return work.rows;
	}

	public int dynamicInsert(String tableName, String[] columnNames,
			Object[] columnValues) throws JdbcException {
		int result = 0;
		if (tableName == null || tableName.trim().equals("")
				|| columnNames == null || columnNames.length == 0
				|| columnValues == null
				|| columnValues.length != columnNames.length) {
			return result;
		}
		StringBuffer sql = new StringBuffer("INSERT INTO ");
		sql.append(tableName);
		sql.append("(");
		for (int i = 0; i < columnNames.length; i++) {
			sql.append(columnNames[i]);
			sql.append(",");
		}
		int index = sql.length();
		sql.replace(index - 1, index, ")");
		sql.append(" ");
		sql.append("VALUES(");
		for (int j = 0; j < columnValues.length; j++) {
			sql.append("?,");
		}
		int index2 = sql.length();
		sql.replace(index2 - 1, index2, ")");

		result = executeUpdate(sql.toString(), columnValues);
		return result;
	}

	public int dynamicUpdate(String tableName,
			Map<String, Object> targetColumnNamesAndValues,
			Map<String, Object> paramColumnAndValues) throws JdbcException {
		int result = 0;
		if (tableName == null || tableName.trim().equals("")
				|| targetColumnNamesAndValues.size() == 0) {
			return result;
		}
		StringBuffer sql = new StringBuffer("UPDATE ");
		List<List> array = CommonUtil.mapToList(targetColumnNamesAndValues);
		List names = (List) array.get(0);
		List values = (List) array.get(1);
		sql.append(tableName);
		sql.append(" SET ");
		for (Object name : names) {
			sql.append((String) name);
			sql.append("=?,");
		}
		int index = sql.length();
		sql.replace(index - 1, index, " ");

		if (paramColumnAndValues != null && paramColumnAndValues.size() != 0) {
			sql.append("WHERE ");
			List<List> params = CommonUtil.mapToList(paramColumnAndValues);

			List paramNames = (List) params.get(0);
			for (Object paramName : paramNames) {
				sql.append((String) paramName);
				sql.append("=? AND ");
			}
			int paramIndex = sql.length();
			sql.replace(paramIndex - 5, paramIndex, "");
			values.addAll((List) params.get(1));

		}
		result = executeUpdate(sql.toString(), values.toArray());
		return result;
	}

	public Dialect getDialect() {
		return dialect;
	}

	/**
	 * Throws a new exception with a more informative error message.
	 * 
	 * @param cause The original exception that will be chained to the new
	 *            exception when it's rethrown.
	 * @param sqlParams The query that was executing when the exception happened.
	 *            valid value to pass in.
	 * 
	 */
	protected void rethrow(SQLException cause, SqlParams sqlParams) {
		rethrow(cause, sqlParams.toString());
	}

	/**
	 * Throws a new exception with a more informative error message.
	 * 
	 * @param cause 
	 *              The original exception that will be chained to the new 
	 *              exception when it's rethrown.
	 * @param sql query sql statement. valid value to pass in.
	 * 
	 */
	protected void rethrow(SQLException cause, String sql) {
		StringBuffer msg = new StringBuffer(cause.getMessage()).append(sql);
		SQLException e = new SQLException(msg.toString(), cause.getSQLState(),
				cause.getErrorCode());
		e.setNextException(cause);
		LOG.error("Errors occured while executing sql: " + sql, cause);
		throw new JdbcException(msg.toString(), e);
	}

	/**
	 * 根据提供的SqlParams得到jdbc预编译语句对象(PreparedStatement)
	 * 
	 * @param conn 普通的jdbc连接
	 * @param pstatement jdbc预编译语句
	 * @param sqlParams 对含有占位符的sql的封装
	 * @return jdbc预编译语句
	 * @throws SQLException
	 * 
	 */
	public PreparedStatement getPreparedStatement(Connection conn,
			PreparedStatement pstatement, SqlParams sqlParams) throws JdbcException {
		try {
			pstatement = conn.prepareStatement(sqlParams.getSql(), 
					ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_READ_ONLY);
			if(!getDialect().supportsLimit() && sqlParams.getMaxResults() > 0) {
				pstatement.setMaxRows(sqlParams.getMaxResults() + sqlParams.getFirstResult());
				if (LOG.isInfoEnabled()) {
					LOG.info("Use JDBC API to limit the number of rows, the max rows => "
									+ sqlParams.getMaxResults()
									+ sqlParams.getFirstResult());
				}
			}
			JdbcUtil.setParameters(pstatement, sqlParams.getParams());
		} catch (Exception e) {
			e.printStackTrace();
			throw new JdbcException("Error occured while preparing jdbc PreparedStatement.", e);
		}
		return pstatement;
	}
	
	protected <T> ResultSet advance(final ResultSet rs, SqlParams<T> sqlParams)
			throws SQLException {
		if(!getDialect().supportsLimit() && sqlParams.getMaxResults() > 0) {
			final int firstRow = sqlParams.getFirstResult();
			if (firstRow != 0) {
				try {
					// we can go straight to the first required row
					rs.absolute(firstRow);
					if (LOG.isInfoEnabled()) {
						LOG.info("Use JDBC API to go straight to the number of row => " + firstRow);
					}
				} catch (SQLException e) {
					// if not support, we need to step through the rows one row at a time (slow)
					if (LOG.isInfoEnabled()) {
						LOG.info("JDBC API not support absolute, we need to step through the specified row.");
					}
					for (int m = 0; m < firstRow; m++) rs.next();
				}
			}
		}
		return rs;
	}

	public boolean has(String tableName, Map<String, Object> conditions)
			throws JdbcException {
		if (conditions == null) {
			return false;
		}
		long result = 0;
		Object[] params = new Object[conditions.size()];
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT count(*) FROM ").append(tableName);
		int i = 0;
		StringBuffer end = new StringBuffer();
		end.append(" WHERE ");
		for (Entry<String, Object> e : conditions.entrySet()) {
			end.append(e.getKey()).append("=? AND ");
			params[i++] = e.getValue();
		}
		end.delete(end.length() - 5, end.length());

		if (conditions.isEmpty()) {
			result = count(sql.toString());
		} else {
			sql.append(end);
			result = count(new SqlParams(sql.toString(), params));
		}

		return (result > 0);
	}

	public boolean has(String tableName, Map<String, Object> conditions,
			Map<String, Object[]> inConditions) throws JdbcException {
		if (conditions == null || inConditions == null) {
			return false;
		}
		List<Object> params = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		long result = 0;
		sql.append("SELECT COUNT(*) FROM ").append(tableName);
		if (!conditions.isEmpty() || !inConditions.isEmpty()) {
			StringBuffer end = new StringBuffer();
			end.append(" WHERE ");

			for (Entry<String, Object> e : conditions.entrySet()) {
				end.append(e.getKey()).append("=? AND ");
				params.add(e.getValue());
			}

			for (Entry<String, Object[]> e : inConditions.entrySet()) {
				Object[] t = e.getValue();
				if (t == null || t.length == 0) {
					continue;
				}
				end.append(e.getKey()).append(" IN(");
				for (Object o : t) {
					end.append("?,");
					params.add(o);
				}
				end.deleteCharAt(end.length() - 1).append(") AND ");
			}
			end.delete(end.length() - 5, end.length());
			sql.append(end);
			result = count(new SqlParams(sql.toString(), params.toArray()));

		} else {
			result = count(sql.toString());
		}

		return (result > 0);
	}

	public boolean existsTable(String tbname) throws JdbcException {
		Properties jdbcProps = JdbcConfigXmlParser.getDefaultJdbcPropertity();
		return existsTable(jdbcProps.getProperty(Environment.DATABASE), tbname);
	}

	public boolean existsTable(String database, String tbname)
			throws JdbcException {
		if (database == null || tbname == null) {
			return false;
		}
		try {
			DatabaseMetaData dmd = conn.getMetaData();
			rs = dmd.getTables(database, null, null, new String[] { "TABLE" });
			while (rs.next()) {
				String tbl = rs.getString(3);
				if (tbl.equalsIgnoreCase(tbname)) {
					return true;
				}
			}
		} catch (SQLException e) {
			throw new JdbcException(e);
		}
		return false;
	}

	public long count(String sql, Object... params) throws JdbcException {
		if(CommonUtil.length(params) == 0) {
			return this.count(new SqlParams(sql));
		}
		return this.count(new SqlParams(sql, params));
	}

	public long count(SqlParams sqlParams) throws JdbcException {
		sqlParams.setSql(dialect.recordCountSql(sqlParams.getSql(false)));
		Object obj = unique(sqlParams);
		return obj == null ? 0 : Long.parseLong(obj.toString());
	}

	public long count(Class<?> objectClass) throws JdbcException {
		String tb = ClassMetadata.getClassMetadata(objectClass).tableName;
		String sql = "SELECT COUNT(*) FROM " + tb;
		Object obj = unique(sql);
		return obj == null ? 0 : Long.parseLong(obj.toString());
	}

	public <T> T unique(String sql) throws JdbcException {
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}
		if(JdbcConfigXmlParser.isShowSql()) {
			Jorm.format(sql);
		}
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return (T) rs.getObject(1);
			}
		} catch (SQLException e) {
			rethrow(e, sql);
		}
		return null;
	}

	public <T> T unique(SqlParams sqlParams) throws JdbcException {
		final SqlParams<T> newParams = dialect.loadEntityParams(sqlParams, false);
		if (LOG.isDebugEnabled()) {
			LOG.debug(newParams.getSql(false));
		}
		if (CommonUtil.isEmpty(newParams.getParams())) {
			return (T) unique(newParams.getSql(false));
		}
		try {
			pstmt = this.getPreparedStatement(conn, pstmt, newParams);
			rs = this.advance(pstmt.executeQuery(), newParams);
			if (rs.next()) {
				return (T) rs.getObject(1);
			}
		} catch (SQLException e) {
			rethrow(e, newParams);
		}
		return null;
	}
	
	public Object column(String sql, String colAlias, Object... params)
			throws JdbcException {
		if(!CommonUtil.isEmpty(params)) {
			return column(colAlias, new SqlParams(sql, params));
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return persister.getColumnValue(rs, colAlias);
			}
		} catch (SQLException e) {
			rethrow(e, sql);
		}
		return null;
	}

	public Object column(String colAlias, SqlParams sqlParams)
			throws JdbcException {
		final SqlParams<?> newParams = dialect.loadEntityParams(sqlParams, false);
		if (LOG.isDebugEnabled()) {
			LOG.debug(newParams.getSql(false));
		}
		try {
			pstmt = this.getPreparedStatement(conn, pstmt, newParams);
			rs = this.advance(pstmt.executeQuery(), newParams);
			if (rs.next()) {
				return persister.getColumnValue(rs, colAlias);
			}
		} catch (SQLException e) {
			rethrow(e, newParams);
		}
		return null;
	}

	public Map<String, Object> queryMap(String sql, Object... params)
			throws JdbcException {
		if (!CommonUtil.isEmpty(params)) {
			return queryMap(new SqlParams(sql, params));
		}
		// else
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			// 将结果放入Map中
			if (rs.next()) {
				return persister.toMap(rs);
			}
		} catch (SQLException e) {
			rethrow(e, sql);
		}
		return null;
	}

	public Map<String, Object> queryMap(SqlParams sqlParams)
			throws JdbcException {
		final SqlParams<?> newParams = dialect.loadEntityParams(sqlParams, false);
		if (LOG.isDebugEnabled()) {
			LOG.debug(newParams.getSql(false));
		}
		try {
			pstmt = this.getPreparedStatement(conn, pstmt, newParams);
			rs = this.advance(pstmt.executeQuery(), newParams);
			if (rs.next()) {
				return persister.toMap(rs);
			}
		} catch (SQLException e) {
			rethrow(e, newParams);
		}
		return null;
	}

	public List<Map<String, Object>> queryMapList(String sql, Object... params)
			throws JdbcException {
		if (null != params && params.length > 0) {
			SqlParams sqlParams = new SqlParams(sql, params);
			return queryMapList(sqlParams);
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			// 取出记录
			while (rs.next()) {
				list.add(persister.toMap(rs));
			}
		} catch (SQLException e) {
			rethrow(e, sql);
		}
		return list;
	}

	public List<Map<String, Object>> queryMapList(SqlParams sqlParams)
			throws JdbcException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		final SqlParams<?> newParams = dialect.loadEntityParams(sqlParams, false);
		if (LOG.isDebugEnabled()) {
			LOG.debug(newParams.getSql(false));
		}
		try {
			pstmt = this.getPreparedStatement(conn, pstmt, newParams);
			rs = this.advance(pstmt.executeQuery(), newParams);
			while (rs.next()) {
				list.add(persister.toMap(rs));
			}
		} catch (SQLException e) {
			rethrow(e, newParams);
		}
		return list;
	}

	public <K, V> Map<K, V> queryColumnsAsMap(String sql, Object... params)
			throws JdbcException {
		if (null != params && params.length > 0) {
			SqlParams sqlParams = new SqlParams(sql, params);
			return queryColumnsAsMap(sqlParams);
		}

		Map<K, V> map = new HashMap<K, V>();
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			if (metaData.getColumnCount() < 2) {
				return map;
			}
			// 以第一列为key,第二列为value
			while (rs.next()) {
				map.put((K) rs.getObject(1), (V) rs.getObject(2));
			}
		} catch (SQLException e) {
			rethrow(e, sql);
		}
		return map;
	}

	public <K, V> Map<K, V> queryColumnsAsMap(SqlParams sqlParams)
			throws JdbcException {
		Map<K, V> map = new HashMap<K, V>();
		final SqlParams<?> newParams = dialect.loadEntityParams(sqlParams, false);
		if (LOG.isDebugEnabled()) {
			LOG.debug(newParams.getSql(false));
		}
		try {
			pstmt = this.getPreparedStatement(conn, pstmt, newParams);
			rs = this.advance(pstmt.executeQuery(), newParams);
			ResultSetMetaData metaData = rs.getMetaData();
			if (metaData.getColumnCount() < 2) {
				return map;
			}
			// 以第一列为key,第二列为value
			while (rs.next()) {
				map.put((K) rs.getObject(1), (V) rs.getObject(2));
			}
		} catch (SQLException e) {
			rethrow(e, newParams);
		}
		return map;
	}
	
	// ============= inner =============
	
	protected void setAutoCommit(boolean autoCommit) throws JdbcException {
		if (isClosed) {
			throw new JdbcException("The JdbcSession had been closed already.");
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Setting auto commit to " + autoCommit);
		}
		try {
			this.getConnection().setAutoCommit(autoCommit);
		} catch (Exception e) {
			throw new JdbcException(e);
		}
	}
	
	protected boolean getAutoCommit() throws JdbcException {
		if (isClosed) {
			throw new JdbcException("The JdbcSession had been closed already.");
		}
		try {
			return this.getConnection().getAutoCommit();
		} catch (Exception e) {
			throw new JdbcException(e);
		}
	}
	
	public void beginTransaction() throws JdbcException {
		if(LOG.isInfoEnabled()) {
			LOG.info("Starting transaction");
		}
        this.setAutoCommit(false);
		
	}

	public void commit() throws JdbcException {
		if (isClosed) {
			throw new JdbcException("The JdbcSession had been closed already.");
		}
		if(LOG.isInfoEnabled()) {
			LOG.info("Commiting transaction");
		}
		try {
			this.getConnection().commit();
		} catch (Exception e) {
			throw new JdbcException(e);
		}

	}

	public void endTransaction() throws JdbcException {
		if(LOG.isInfoEnabled()) {
			LOG.info("End transaction");
		}
		this.setAutoCommit(true);

	}

	public void rollback() throws JdbcException {
		if (isClosed) {
			throw new JdbcException("The JdbcSession had been closed already.");
		}
		if (LOG.isInfoEnabled()) {
			LOG.info("Rolling back transaction of Session => " + this);
		}
		try {
			this.getConnection().rollback();
		} catch (Exception e) {
			throw new JdbcException(e);
		}
		
	}

	public Connection getConnection() {
		return this.conn;
	}
	
	public Connection getConnection(boolean isnew) throws JdbcException {
		if(isnew) {
			return ConnectionManager.getConnection(JdbcConfigXmlParser.getDefaultConnectionName());
		}
		return this.conn;
	}

	public String getDatabase(String provider) throws JormException {
		Properties jdbcPropertity = JdbcConfigXmlParser.getJdbcPropertity(provider);
		if(jdbcPropertity != null) {
			return jdbcPropertity.getProperty(Environment.DATABASE);
		}
		throw new JormException("Failed to get the database name, provider => " + provider);
	}

	public String getDefaultDatabase() throws JormException {
		return getDatabase(JdbcConfigXmlParser.getDefaultConnectionName());
	}
	
	public JdbcBatcher createBatcher() throws JdbcException {
		if(!getDialect().supportSpecifiedFeture(Environment.SUPPORT_BATCH_UPDATE)) {
			throw new JdbcException("Batch operation is not supported.");
		}
		if(null == this.batcher) {
			this.batcher = new JdbcBatcherImpl(this);
			if(LOG.isInfoEnabled()) {
				LOG.info("A JDBC Batcher is created.");
			}
		}
		return this.batcher;
	}

	public BeanPersister getPersister() {
		return this.persister;
	}
	
	public void flush() throws JdbcException {
		DBUtil.closeQuietly(null, stmt, pstmt, rs);
	}

	public void close() throws JdbcException {
		synchronized (this.lock) {
			if(null != this.batcher) {
				if(!batcher.hadSubmit()) {
					batcher.execute();
				}
				this.batcher = null;
			}
			if(null != this.getMonitor()) {
				try {
					this.getMonitor().interrupt();
				} catch (Exception e) {
				} finally {
					this.monitor = null;
				}
			}
			DBUtil.closeQuietly(conn, stmt, pstmt, rs);
			this.persister = null;
			this.dialect = null;
			this.isClosed = true;
			if(LOG.isInfoEnabled()) {
				LOG.info("Session [" + this + "] is closed.");
			}
		}
	}

	public boolean isClosed() {
		return isClosed;
	}
	
	public void call(final String procedure, final Object... params) throws JdbcException {
		this.call(new ProcedureCaller() {
			public CallableStatement prepare() throws SQLException {
				CallableStatement cs = getSession().getConnection().prepareCall(procedure);
				if(JdbcConfigXmlParser.isShowSql()) {
					Jorm.format(procedure);
				}
				if(!CommonUtil.isEmpty(params)) {
					JdbcUtil.setParameters(cs, params);
				}
				return cs;
			}
			
			public Boolean callback(CallableStatement cs) throws SQLException {
				return Boolean.valueOf(cs.execute());
			}
		});
	}
	
	public <T> T call(ProcedureCaller caller) throws JdbcException {
		try {
			caller.setSession(this);
			return (T) caller.execute();
		} catch (Throwable t) {
			t.printStackTrace();
			if (t instanceof JdbcException) {
				throw (JdbcException) t;
			} else if (t instanceof SQLException) {
				throw new JdbcException("Error performing Procedure call.",
						(SQLException) t);
			} else {
				throw new JdbcException("Error performing Procedure call.", t);
			}
		}
	}
	
	protected void startMonitor() {
		if(!this.autoClose) {
			return;
		}
		
		Runnable runnable = new Runnable() {
			protected long delay = JdbcConfigXmlParser.getCheckDelay();
			boolean warnedAlready = false;
			boolean interrupted = false;
			
			public void run() {
				while (!interrupted) {
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						// no interruption expected
					}
					checkAndConfigure();
				}
			}
			
			protected void checkAndConfigure() {
				if(System.currentTimeMillis() < startTimestamp) {
					LOG.warn("Bad state, now timestamp was less than session startTimestamp ["+ startTimestamp + "].");
					interrupted = true; // there is no point in continuing
					return;
				}

				if(LOG.isInfoEnabled()) {
					LOG.info("Checking session => " + seesionToString() + " lifetime.");
				}
				if (System.currentTimeMillis() > startTimestamp) {
					if (System.currentTimeMillis() > (startTimestamp + JdbcConfigXmlParser.getSessionLifetime())) { 
						if(!isClosed()) {
							close();
							interrupted = true;
						}
						warnedAlready = false;
					}
				} else {
					if (!warnedAlready) {
						LOG.warn("Bad state, now timestamp was less than session startTimestamp?");
						warnedAlready = true;
					}
				}
			}
		};
		this.monitor = new Thread(runnable, "Jorm-session-monitor-" + System.currentTimeMillis());
		this.monitor.setDaemon(true);
		this.monitor.start();
		LOG.info("Session monitor started.");
	}
	
	void initializedDBProperties() throws JdbcException {
		Properties nativeProperties = new Properties();
		try {
			DatabaseMetaData dbMetaData = conn.getMetaData();
			nativeProperties.setProperty(Environment.DB_NAME, dbMetaData.getDatabaseProductName());
			nativeProperties.setProperty(Environment.PRODUCT_VERSION, dbMetaData.getDatabaseProductVersion());
			nativeProperties.setProperty(Environment.DB_VERSION, dbMetaData.getDatabaseMajorVersion() + "." + dbMetaData.getDatabaseMinorVersion());
			nativeProperties.setProperty(Environment.DB_ISOLATION_LEVEL, dbMetaData.getDefaultTransactionIsolation() + "");
			if (dbMetaData.supportsTransactions()) {
				nativeProperties.setProperty(Environment.SUPPORT_TRANSACTIONS, "true");
			}
			if (dbMetaData.supportsBatchUpdates()) {
				nativeProperties.setProperty(Environment.SUPPORT_BATCH_UPDATE, "true");
			}
			if(dbMetaData.supportsSavepoints()) {
				nativeProperties.setProperty(Environment.SUPPORT_SAVEPOINTS, "true");
			}
			this.dialect.setNativeProperties(nativeProperties);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new JdbcException(t.getMessage());
		}
	}
	
	protected String seesionToString() {
		return this.toString();
	}
}
