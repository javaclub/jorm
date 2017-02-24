/*
 * @(#)ClientTest.java	2011-9-19
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.jdbc.DBUtil;
import org.junit.Test;

/**
 * ClientTest
 * 
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: ClientTest.java 2011-9-19 下午02:43:26 Exp $
 */
public class ClientTest {

	static String driver = "com.mysql.jdbc.Driver";
	static String jdbcurl = "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&amp;characterEncoding=UTF-8";
	static String username = "root";
	static String password = "root";

	public static void main(String[] args) throws Throwable {
		Connection conn = DBUtil.getConnection(driver, jdbcurl, username, password);
		clean(conn);

		String sql = "INSERT INTO t_id_increment_auto_generated(name) VALUES ('dog'),('cat'),('penguin'),('lax'),('whale'),('ostrich');";
		PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		//ps.setString(1, "kkk");
		ps.executeUpdate();//方法一：执行一条语句
		
		Object obj = Jorm.getSession().unique("SELECT ( IDENT_CURRENT('t_id_increment_auto_generated') + IDENT_INCR('t_id_increment_auto_generated') ) AS id");
		System.out.println(obj);

		/*Statement ps = conn.createStatement();
		int total = 10;
		for (int i = 0; i < total; i++) {
			ps.addBatch("insert into t_id_increment_mysql_auto(name) values ('" + Strings.fixed(6) + "')");
			Object obj = Jorm.getSession().queryUniqueObject("SELECT last_insert_id()");
			System.out.println(obj);
		}
		ps.executeBatch();

		ResultSet rs = ps.getGeneratedKeys();
		while(rs.next()) {
			System.out.println(rs.getInt(1));
		}*/


	}

	public static void clean(Connection conn) throws SQLException {
		Statement statement = conn.createStatement();
		statement.execute("DELETE FROM t_id_increment_auto_generated");
	}
	
	static String getOrderByPart(String sql) {  
        String loweredString = sql.toLowerCase();  
        int orderByIndex = loweredString.indexOf("order by");  
        if (orderByIndex != -1) {  
            // if we find a new "order by" then we need to ignore  
            // the previous one since it was probably used for a subquery  
            return sql.substring(orderByIndex);  
        } else {  
            return "";  
        }  
    }  
	
	@Test
	public void test() {
		int _start = 1;
		int _limit = 6;
		String _sql = "SELECT * FROM t_user ORDER BY ID DESC";
		StringBuffer pagingBuilder = new StringBuffer();    
        String orderby = getOrderByPart(_sql);    
        String distinctStr = "";    

        String loweredString = _sql.toLowerCase();    
        String sqlPartString = _sql.trim();    
        if (loweredString.trim().startsWith("select")) {    
            int index = 6;    
            if (loweredString.startsWith("select distinct")) {    
                distinctStr = "DISTINCT ";    
                index = 15;    
            }    
            sqlPartString = sqlPartString.substring(index);    
        }    
        pagingBuilder.append(sqlPartString);    

        // if no ORDER BY is specified use fake ORDER BY field to avoid errors    
        if (orderby == null || orderby.length() == 0) {    
            orderby = "ORDER BY CURRENT_TIMESTAMP";    
        }    

        StringBuffer result = new StringBuffer();    
        result.append("SELECT * FROM (")     
        .append("SELECT ")    
        .append(distinctStr)    
        .append(" TOP 100 PERCENT ROW_NUMBER() OVER (") //使用TOP 100 PERCENT可以提高性能    
        .append(orderby)    
        .append(") AS __hibernate_row_nr__, ")    
        .append(pagingBuilder)    
        .append(") as ucstarTempTable WHERE __hibernate_row_nr__ >")  
        .append(_start)    
        .append(" AND __hibernate_row_nr__ <=")    
        .append(_start + _limit)    
        .append(" ORDER BY __hibernate_row_nr__");    

        System.out.println(result.toString());  
	}

}
