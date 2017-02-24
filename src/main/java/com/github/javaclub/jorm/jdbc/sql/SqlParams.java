/*
 * @(#)SqlParams.java	May 7, 2009
 *
 * Copyright (c) 2009 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.jdbc.sql;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.JormException;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.config.JdbcConfigXmlParser;
import com.github.javaclub.jorm.jdbc.criterion.Order;
import com.github.javaclub.jorm.jdbc.sql.util.SqlUtil;

/**
 * SQL语句的封装类，包含可含有占位符的SQL语句与占位符值
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: SqlParams.java 529 2011-10-08 16:10:35Z gerald.chen.hz@gmail.com $
 */
public class SqlParams<T> implements Serializable {

	private static final long serialVersionUID = -1247394451022586209L;
	
	protected static final Log LOG = LogFactory.getLog(SqlParams.class);

	/** 
	 * sql语句,可能会包含有占位符,如下
	 * <li> "SELECT * FROM t_table WHERE id=? AND name=?"
	 * <li> "name=? AND age=? AND sex=?"
	 * 
	 */
	private String sql;
	
	/** 查询时需要的查询对象类型 */
	private Class<T> objectClass;
	
	/** 如果sql语句中含有占位符，那么paraValues的值就是顺序对应的占位符值 */
	private Object[] params;
	
	/** 是否是SELECT语句 */
	private boolean select = false;
	
	/** Define JDBC ResultSet's RowNumber starting from zero */
	private int firstResult = 0;
	
	/** 抓取数据记录的条数、当sql语句是查询语句时有效  */
	private int maxResults = -1;
	
	/** 排序 */
	private Set<Order> orders = new HashSet<Order>();
	
	/** 是否加载关联对象 default value => <code>true</code> */
	private boolean loadAssociated = true;
	
	public SqlParams() {
	}
	
	public SqlParams(String sql) {
		this(sql, null);
	}

	public SqlParams(String sql, Object[] params) {
		this.sql = initialized(sql);
		this.params = params;
		
	}
	
	protected String initialized(String sql) {
		sql = sql.trim();
		if(sql.startsWith("(") && sql.endsWith(")")) {
			sql = sql.substring(1, sql.length()-1);
		}
		sql = sql.replaceAll("\\r", " ");
		sql = sql.replaceAll("\\n", " ");
		sql = sql.replaceAll("\\t", " ");
		while (sql.indexOf("  ") > 0) {
			sql = sql.replaceAll("  ", " ");
		}
		if(sql.toUpperCase().startsWith("SELECT")) {
			this.select = true;
		}
		return sql;
	}

	public SqlParams<T> addOrder(Order order) {
		if(!orders.contains(order)) {
			orders.add(order);
		}
		return this;
	}
	
	public boolean hasOrderBy() {
		final String regex = "(select\\s+)(.+\\s+)(from\\s+)(.+\\s+)([^\\(]+)(\\s+order\\s+by\\s+)([^\\)]+)";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(sql);
		if(matcher.find()) {
			String matched = matcher.group();
			if(!matched.endsWith(")") && matched.equalsIgnoreCase(sql)) {
				return true;
			}
		}

		String lower = Strings.lowerCase(sql);
		if (Strings.count(lower, "select") == 1
				&& Strings.count(lower, "from") == 1
				&& Strings.count(lower, "order by") == 1) {
			return true;
		}
		return false;
	}
	
	public final String getSqlNoOrderBy() {
		if(this.hasOrderBy()) {
			String sbf = Strings.upperCase(sql);
			int lastOrderByPos = sbf.lastIndexOf(" ORDER BY");
			return sql.substring(0, lastOrderByPos);
		}
		return sql;
	}
	
	public String getCountSql() {
		String noOrderSql = getSqlNoOrderBy();
		String upper = Strings.upperCase(noOrderSql);
		if(upper.indexOf("DISTINCT") > -1) {
			return "SELECT COUNT(*) AS num FROM (" + SqlUtil.convert(sql) + ")";
		}
		int pos = upper.indexOf("FROM ");
		return "SELECT COUNT(*) AS num " + sql.substring(pos);
	}
	
	public SqlParams<T> setSql(String sql) {
		this.sql = initialized(sql);
		return this;
	}
	
	public String getSql() {
		return this.getSql(true);
	}
	
	/**
	 * Get the sql string
	 *
	 * @param log if logging sql to the console
	 * @return sql sql string
	 */
	public String getSql(boolean log) {
		if(Strings.isNotEmpty(this.sql)) {
			if(log && JdbcConfigXmlParser.isShowSql()) {
				Jorm.format(this.sql);
			}
			return this.sql;
		}
		// this.sql is null or empty
		if(null != getObjectClass()) {
			return SqlPrepared.preparedSelectAll(getObjectClass());
		}
		throw new JormException("The ObjectClass is not initialized.");
	}
	
	public Object[] getParams() {
		return params;
	}

	public SqlParams<T> setParams(Object[] params) {
		this.params = params;
		return this;
	}
	
	public SqlParams<T> setOrders(Set<Order> orders) {
		this.orders = orders;
		return this;
	}

	public Set<Order> getOrders() {
		return orders;
	}
	
	public Class<T> getObjectClass() {
		return objectClass;
	}

	public SqlParams<T> setObjectClass(Class<T> objectClass) {
		this.objectClass = objectClass;
		return this;
	}
	
	public int getFirstResult() {
		return firstResult;
	}

	public SqlParams<T> setFirstResult(int firstResult) {
		this.firstResult = firstResult;
		return this;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public SqlParams<T> setMaxResults(int maxResults) {
		this.maxResults = maxResults;
		return this;
	}

	public boolean isSelect() {
		return this.select;
	}

	public boolean isLoadAssociated() {
		return loadAssociated;
	}

	public SqlParams<T> setLoadAssociated(boolean loadAssociated) {
		this.loadAssociated = loadAssociated;
		return this;
	}

	public SqlParams<T> copy() {
		String t_sql = this.sql;
		if(Strings.isEmpty(t_sql)) {
			if(null == getObjectClass()) {
				throw new JormException("The ObjectClass is not initialized.");
			}
			t_sql = SqlPrepared.preparedSelectAll(getObjectClass());
		}
		SqlParams<T> params = new SqlParams<T>(t_sql, this.getParams());
		params.setObjectClass(this.getObjectClass());
		params.setOrders(this.getOrders());
		params.setFirstResult(this.getFirstResult());
		params.setMaxResults(this.getMaxResults());
		params.setLoadAssociated(this.isLoadAssociated());
		
		return params;
	}
	
	/**
	 * 清理回收内存
	 *
	 */
	public void clear() {
		this.getOrders().clear();
		this.setSql(null);
		this.setParams(null);
		this.setOrders(null);
		this.setObjectClass(null);
	}
	
	public String toString() {
		StringBuffer msg = new StringBuffer("");
		msg.append(" Query: ");
		msg.append(sql);
		msg.append(" Parameters: ");
		if (params == null) {
			msg.append("[]");
		} else {
			msg.append(Arrays.asList(params));
		}
		return msg.toString();
	}
	
	public static void main(String[] args) {
		/*
		String sqlx = "select id c_id, name, sex, age c_age, career c_job from t_user order by c_id desc";
		sqlx = "SELECT FIRST_NAME, LAST_NAME, COUNT(*) FROM AUTHOR JOIN BOOK ON AUTHOR.ID = BOOK.AUTHOR_ID WHERE LANGUAGE = 'DE' AND PUBLISHED > '2008-01-01' GROUP BY FIRST_NAME, LAST_NAME HAVING COUNT(*) > 5 ORDER BY LAST_NAME ASC NULLS FIRST LIMIT 2 OFFSET 1 FOR UPDATE OF FIRST_NAME, LAST_NAME";
		SqlParams sqlParams = new SqlParams(sqlx);
		String cc = sqlParams.getCountSql();
		String noorder = sqlParams.getSqlNoOrderBy();
		System.out.println("count => " + cc);
		System.out.println("noorder => " + noorder);
		System.out.println("noorder => " + new SqlParams("select * from (select * from t where t.id in(1,11,11) order by t.name desc) tb").getSqlNoOrderBy());
		
		System.out.println("hasOrderBy => " + new SqlParams(sqlx).hasOrderBy());
		System.out.println("hasOrderBy => " + new SqlParams("select * from (select * from t where t.id in(1,11,11) order by t.name desc) tb").hasOrderBy());
		*/
		
		StringBuilder sb = new StringBuilder("Hello,World!");
		System.out.println(sb.toString());
		if(1 == 1) {
			sb.setLength(0);
			System.out.println(sb.toString());
			sb.append("KKKKK");
			System.out.println(sb.toString());
		}
		
		String kk = "select 		form" +
				"\n  		kkk";
		System.out.println(kk.replaceAll("\\n", ""));
	}

}
