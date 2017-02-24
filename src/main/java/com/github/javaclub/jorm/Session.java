/*
 * @(#)DBSession.java	2010-4-21
 *
 * Copyright (c) 2010 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import com.github.javaclub.jorm.annotation.PK;
import com.github.javaclub.jorm.jdbc.BeanPersister;
import com.github.javaclub.jorm.jdbc.JdbcException;
import com.github.javaclub.jorm.jdbc.batch.JdbcBatcher;
import com.github.javaclub.jorm.jdbc.callable.ProcedureCaller;
import com.github.javaclub.jorm.jdbc.sql.Dialect;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;

/**
 * A jdbc session deal with database.
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: Session.java 568 2011-10-10 07:44:32Z gerald.chen.hz $
 */
@SuppressWarnings("unchecked")
public interface Session {
	
	/**
	 * Get the underlying database dialect.
	 *
	 * @return the underlying database dialect.
	 */
	public Dialect getDialect();
	
	/**
	 * Get a jdbc connection from this Session.
	 *
	 * @return JDBC Connection
	 */
	public Connection getConnection();
	
	/**
	 * Require a new JDBC Connection.
	 *
	 * @param isnew is this required session is fully new ?
	 * @return JDBC Connection
	 * @throws JdbcException
	 */
	public Connection getConnection(boolean isnew) throws JdbcException;
	
	/**
	 * Start transaction.
	 *
	 * @throws JdbcException
	 */
	public void beginTransaction() throws JdbcException;
	
    /**
     * Commit the transaction.
     *
     * @throws JdbcException
     */
    public void commit() throws JdbcException;
    
    /**
     * If operation failed, do rollback.
     *
     * @throws JdbcException
     */
    public void rollback() throws JdbcException;
    
    /**
     * Ending transaction.
     *
     * @throws JdbcException
     */
    public void endTransaction() throws JdbcException;
    
    /**
     * Close the JdbcSession
     *
     * @throws JdbcException
     */
    public void close() throws JdbcException;
    
    /**
     * Present the session is auto-closed type or not.
     *
     * @return true if this session is auto-closed type, otherwise false
     * @since 1.0.7
     */
    public boolean isAutoClose();
    
    /**
     * This method is used to call a sql stored procedure.
     *
     * @param caller a abstract procedure caller param
     * @return the invoked result of a sql stored procedure
     * @throws JdbcException
     */
    public <T> T call(ProcedureCaller caller) throws JdbcException;
    
    /**
     * Executes a stored procedure that without a returning value.
     *
     * @param procedure the stored procedure
     * @param params input parameters
     * @throws JdbcException
     */
    public void call(final String procedure, final Object... params) throws JdbcException;
    
    /**
     * Release some jdbc resources, such as {@link Statement}, {@link ResultSet}.
     *
     * @throws JdbcException
     */
    public void flush() throws JdbcException;
    
    /**
     * Gets the default connection's database name.
     *
     * @return the default connection's database name
     * @throws JormException
     */
    public String getDefaultDatabase() throws JormException;
    
    /**
     * Gets the specified connection's database name.
     *
     * @param provider a specified connection provider name
     * @return database name
     * @throws JormException
     */
    public String getDatabase(String provider) throws JormException;
    
    /**
     * Creates a JDBC Batch operator.
     *
     * @return JDBC Batch operator
     * @throws JdbcException
     */
    public JdbcBatcher createBatcher() throws JdbcException;
    
    /**
     * Gets the holding entity persiter of session
     *
     * @return a entity persiter
     */
    public BeanPersister getPersister();
	
	/**
	 * If the JdbcSession is closed.
	 *
	 * @return true if JdbcSession is closed, othwise false.
	 */
	public boolean isClosed();
	
	/**
	 * Reads a list of all objects in the database mapped to the given object class.
	 *
	 * @param objectClass entity class type
	 * @return the collection result
	 * @throws JdbcException
	 */
	public <T> List<T> all(Class<T> objectClass) throws JdbcException;
	
	/**
	 * Persist the given transient instance, first assigning a generated identifier. (Or
	 * using the current value of the identifier property if the <tt>assigned</tt>
	 * generator is used.)
	 *
	 * @param object a transient instance of a persistent class
	 * @return the generated identifier
	 * @throws JdbcException
	 */
	public Serializable save(Object object) throws JdbcException;

	/**
	 * Update the persistent instance with the identifier of the given detached
	 * instance. 
	 *
	 * @param object a detached instance containing updated state
	 * @throws JdbcException
	 */
	public <T> void update(T object) throws JdbcException;
	
	/**
	 * Either {@link #save(Object)} or {@link #update(Object)} the given
	 * instance, depending upon resolution of the unsaved-value checks (see the
	 * manual for discussion of unsaved-value checking).
	 * <p/>
	 *
	 * @see Session#save(java.lang.Object)
	 * @see Session#update(Object object)
	 * @param object a transient or detached instance containing new or updated state
	 * @throws JdbcException
	 */
	public void saveOrUpdate(Object object) throws JdbcException;
	
	/**
	 * Clean all the data records of a table.
	 *
	 * @param objectClass the mapping entity of the target table
	 * @return influenced record count
	 * @throws JdbcException
	 */
	public <T> int clean(Class<T> objectClass) throws JdbcException;
	
	/**
	 * Clean all the data records of a table.
	 *
	 * @param tbname table name
	 * @return influenced record count
	 * @throws JdbcException
	 */
	public int clean(String tbname) throws JdbcException;
	
	/**
	 * Tests if the specfied object exsits in database.
	 *
	 * @param object the object to test
	 * @return true if the object exsits in database, false otherwise.
	 * @throws JdbcException
	 */
	public <T> boolean has(T object) throws JdbcException;
	
	/**
	 * Remove a persistent instance from the database. The argument may be
	 * an instance associated with the receiving <tt>Session</tt> or a transient
	 * instance with an identifier associated with existing persistent state.
	 *
	 * @param object the instance to be removed
	 * @throws JdbcException
	 */
	public <T> void delete(T object) throws JdbcException;
	
	/**
	 * Execute delete operation with sql only.
	 *
	 * @param sql sql statement
	 * @return influenced record count
	 * @throws JdbcException
	 */
	public int delete(String sql) throws JdbcException;
	
	/**
	 * Delete all objects returned by the query. Return the number of objects deleted.
	 *
	 * @param params {@link SqlParams}
	 * @return influenced record count
	 * @throws JdbcException
	 */
	public int delete(SqlParams params) throws JdbcException;
	
	/**
	 * Delete objects by conditions.
	 *
	 * @param objectClass the class to delete.
	 * @param whereFragment conditions
	 * @return the successfully deleting results count.
	 * @throws JdbcException
	 */
	public <T> int delete(Class<T> objectClass, String whereFragment) throws JdbcException;
	
	/**
	 * Query object by primary key.
	 *
	 * @param objectClass the class to lookup
	 * @param id primary key
	 * @return query result
	 * @throws JdbcException
	 */
	public <T> T read(Class<T> objectClass, Serializable id) throws JdbcException;
	
	/**
	 * Lookup a single object by multiple primary keys.
	 * <p>
	 * The target object entity class type must define annotation {@link PK}, and 
	 * the multiple primary key values' order should the same with PK items' order.
	 * </p>
	 *
	 * @param objectClass entity class type
	 * @param pks  multiple primary key values
	 * @return  query result
	 * @throws JdbcException
	 */
	public <T> T read(Class<T> objectClass, Object... pks) throws JdbcException;
	
	/**
	 * Loads the frist record object.
	 *
	 * @param sqlParams query parameters
	 * @return domain object
	 * @throws JdbcException
	 */
	public <T> T loadFirst(SqlParams<T> sqlParams) throws JdbcException;
	
	/**
	 * Loading the first Java entity that satisfy query conditions
	 *
	 * @param objectClass the entity class type
	 * @param sql query sql statement
	 * @param params bind parameters
	 * @return the first Java entity
	 * @throws JdbcException
	 */
	public <T> T loadFirst(Class<T> objectClass, String sql, Object... params) throws JdbcException;
	
	/**
	 * Load entities that satisfy specified conditions.
	 *
	 * @param params SqlParams
	 * @return collection result
	 * @throws JdbcException
	 */
	public <T> List<T> loadBeans(SqlParams<T> params) throws JdbcException;

	/**
	 * Find Objects by condition.
	 *
	 * @param objectClass the entity class type
	 * @param params conditions
	 * @return query objects
	 * @throws JdbcException
	 */
	public <T> List<T> list(SqlParams<T> params) throws JdbcException;
	
	/**
	 * Find entities that satisfy specified conditions.
	 *
	 * @param objectClass the entity class type
	 * @param sql query sql statement
	 * @param params bind parameters
	 * @return collection result
	 * @throws JdbcException
	 */
	public <T> List<T> list(Class<T> objectClass, String sql, Object... params) throws JdbcException;
	
	// --- JDBC Operation ---
	/**
	 * Executes the given INSERT, UPDATE, or DELETE SQL statement without any replacement parameters.
	 * 
	 * @param sql The SQL statement to execute.
	 * @return the count of the influenced rows
	 */
	public int executeUpdate(String sql) throws JdbcException;

	/**
	 * Executes sql with some bind parameters.
	 * 
	 * @param sql the sql statement with some '?'
	 * @param params binding parameters
	 * @return the count of the influenced rows
	 */
	public int executeUpdate(String sql, Object[] params) throws JdbcException;

	/**
	 * Executes sql with some bind parameters.
	 * 
	 * @param sqlParams bind SqlParams
	 * @return the count of the influenced rows
	 */
	public int executeUpdate(SqlParams sqlParams) throws JdbcException;

	/**
	 * Executes batch operation with a few of sql statement.
	 * 
	 * @param sqls sql statement array
	 * @return the count of the influenced rows, int[i] presents the influenced record count of sqls[i].
	 */
	public int[] executeBatch(String[] sqls) throws JdbcException;
	
	/**
	 * 批量更新操作,同条SQL语句,单一不同参数,失败则全部回滚
	 * 
	 * @param sql SQL语句
	 * @param params 不同的参数值
	 * @param perBatchSize 批量大小,0直接返回空列表
	 * @return 返回影响记录数List<int[]>,List[i]表示第i批影响的记录数int[]
	 */
	public List<int[]> executeBatch(String sql, Object[] params, int perBatchSize) throws JdbcException;

	/**
	 * 批量更新操作,同条SQL语句,多个不同参数,失败则全部回滚
	 * 
	 * @param sql SQL语句
	 * @param params 占位符参数
	 * @param perBatchSize 批量大小,0直接返回空列表
	 * @return 返回影响记录数List<int[]>,List[i]表示第i批影响的记录数int[]
	 */
	public List<int[]> executeBatch(String sql, Object[][] params, int perBatchSize) throws JdbcException;

	/**
	 * Batch insert operation.
	 * 
	 * @param sql a prepared sql statement with some '?'
	 * @param datalist the binding parameters list
	 * @return <code>true</code> if the operation is success, otherwise <code>false</code>.
	 */
	public boolean batchInsert(String sql, List<Object[]> datalist) throws JdbcException;
	
	/**
	 * Batch insert operation.
	 *
	 * @param sql a prepared sql statement with some '?'
	 * @param datalist the binding parameters list
	 * @param perBatchSize Every batch operation's default record size
	 * @return <code>true</code> if the operation is success, otherwise <code>false</code>.
	 */
	public boolean batchInsert(String sql, List<Object[]> datalist, int perBatchSize) throws JdbcException;
	
	/**
	 * Delete rows from table by the specified conditions.
	 *
	 * @param tbname table name
	 * @param conditions sql statement conditions
	 * @return the count of influenced rows
	 */
	public int delete(String tbname, Map<String, Object> conditions) throws JdbcException;

	/**
	 * 动态更新某些列的值
	 * 
	 * @param tableName 表名
	 * @param targetColumnNamesAndValues 要更新的列名和对应的值
	 * @param paramColumnAndValues 条件（包括列名和对应值）
	 * @return the count of influenced rows
	 */
	public int dynamicUpdate(String tableName, Map<String, Object> targetColumnNamesAndValues,
			Map<String, Object> paramColumnAndValues) throws JdbcException;

	/**
	 * 往表里插入一条新记录，只给特定的列赋值
	 * 
	 * @param tableName 数据表名
	 * @param columnNames 列名
	 * @param columnValues 对应的列值
	 * @return the count of influenced rows
	 */
	public int dynamicInsert(String tableName, String[] columnNames, Object[] columnValues) throws JdbcException;
	
	/**
	 * Get total records count by the specified jdbc sql.
	 *
	 * @param sql jdbc query sql statement with some '?'
	 * @param params bind parameters
	 * @return records count that satisfy condition
	 * @throws JdbcException
	 */
	public long count(String sql, Object... params) throws JdbcException;

	/**
	 * Get total records count by the specified wraped jdbc sql.
	 * 
	 * @param sqlParams the wraped jdbc sql
	 * @return total records count 
	 */
	public long count(SqlParams sqlParams) throws JdbcException;
	
	/**
	 * Gets the total count of the mapped entity
	 *
	 * @param objectClass entity class type
	 * @return the total count of the mapped entity
	 * @throws JdbcException
	 */
	public long count(Class<?> objectClass) throws JdbcException;
	
	/**
	 * Test a table whether contains the specified record or not.
	 *
	 * @param tableName table name
	 * @param conditions query condition with the form of column and value
	 * @return <code>true</code> if the specified table has the record that satisfy conditions, 
	 *         otherwise <code>false</code>.
	 */
	public boolean has(String tableName, Map<String, Object> conditions) throws JdbcException;
	
	/**
	 * Test a table whether contains the specified record or not.
	 *
	 * @param tableName table name
	 * @param conditions query condition
	 * @param inConditions query condition of IN(...)
	 * @return <code>true</code> if the specified table has the record that satisfy conditions, 
	 *         otherwise <code>false</code>.
	 */
	public boolean has(String tableName, Map<String, Object> conditions, Map<String, Object[]> inConditions) throws JdbcException;
	
	/**
	 * Test current database whether contains the specified table or not.
	 *
	 * @param tbname table name
	 * @return <code>true</code> if current database contains the specified table, otherwise <code>false</code>.
	 */
	public boolean existsTable(String tbname) throws JdbcException;
	
	/**
	 * Test the database whether contains the specified table or not.
	 * <p>
	 * for PostgreSQL, if the database is not the current connection's database, this method will not take effect.
	 *
	 * @param database database name
	 * @param tbname table name
	 * @return  <code>true</code> if database contains the specified table with name tbname, otherwise <code>false</code>.
	 */
	public boolean existsTable(String database, String tbname) throws JdbcException;
	
	/**
	 * Used to load a single column or entity, such as following sqls: 
	 * <li> select name from User where ID = 1 
	 * <li> select t.* from t_user t limit 1
	 * 
	 * @param sql query sql statement
	 */
	public <T> T unique(String sql) throws JdbcException;

	/**
	 * Used to load a single column or entity, such as following sqls: 
	 * <li> select name from User where ID = ? 
	 * <li> select t.* from t_user t where t.name = ? limit 1
	 * 
	 * @param sqlParams sql params
	 */
	public <T> T unique(SqlParams sqlParams) throws JdbcException;
	
	public Object column(String sql, String colAlias, Object... params) throws JdbcException;

	public Object column(String colAlias, SqlParams sqlParams) throws JdbcException;
	
	/**
	 * Gets the first row result in the form of {@link Map}.
	 * 
	 * @param sql sql statement with some '?'
	 * @return the first row result in the form of {@link Map}, key is column name, value is column value.
	 */
	public Map<String, Object> queryMap(String sql, Object... params) throws JdbcException;

	/**
	 * @see #queryMap(String, Object...)
	 */
	public Map<String, Object> queryMap(SqlParams sqlParams) throws JdbcException;
	
	/**
	 * Gets the records result int the form of map list by specified query condition.
	 * 
	 * @param sql sql statement with some '?'
	 * @return the record result int the form of map list
	 */
	public List<Map<String, Object>> queryMapList(String sql, Object... params) throws JdbcException;

	/**
	 * @see #queryMapList(String, Object...)
	 */
	public List<Map<String, Object>> queryMapList(SqlParams sqlParams) throws JdbcException;
	
	/**
	 * Presents first two colums of query results as {@link Map}.
	 *
	 * @param sql sql statement with some '?'
	 * @param params binding parameters
	 * @return first two colums of query results, with the form of {@link Map}, first column values as the key, second as the value
	 * @throws JdbcException
	 */
	public <K, V> Map<K, V> queryColumnsAsMap(String sql, Object... params) throws JdbcException;

	/**
	 * @see #queryColumnsAsMap(String, Object...)
	 */
	public <K, V> Map<K, V> queryColumnsAsMap(SqlParams sqlParams) throws JdbcException;
	
}
