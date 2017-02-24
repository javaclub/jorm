/*
 * @(#)ProcedureTest.java	2011-8-25
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.mysql;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.jdbc.callable.ProcedureCaller;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;
import com.github.javaclub.jorm.testentity.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * ProcedureTest
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: ProcedureTest.java 2011-8-25 下午06:18:17 Exp $
 */
public class ProcedureTest {

	static Session session;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		session = Jorm.getSession();
	}
	
	@AfterClass
	public static void destroyAfterClass() {
		Jorm.free();
	}
	
	@Test
	public void testQuery() {
		String p = "{call p_t_user_query()}";
		
		List<User> list = session.list(new SqlParams<User>(p).setObjectClass(User.class));
		System.out.println(list);
	}
	
	
	@Test
	public void testQuery_1() {
		List<User> users = session.call(new ProcedureCaller() {
			public CallableStatement prepare() throws SQLException {
				String sql = "{call p_t_user_query()}";
				CallableStatement cs = getSession().getConnection().prepareCall(sql);
				return cs;
			}
			
			public List<User> callback(CallableStatement cs) throws SQLException {
				ResultSet rs = cs.executeQuery();
				return getSession().getPersister().toBeanList(rs, User.class, true);
			}
		});
		
		for (int i = 0; i < users.size(); i++) {
			System.out.println(users.get(i));
		}
	}
	
	@Test
	public void test_insert_0() {
		String p = "{call p_t_incre_insert()}";
		session.call(p);
	}
	
	@Test
	public void test_hello_pro() {
		final String pro = "{? = call hello_proc(?)}";
		String r = session.call(new ProcedureCaller() {
			
			public CallableStatement prepare() throws SQLException {
				CallableStatement cs = this.getSession().getConnection().prepareCall(pro);
				cs.setString(2, "World");
				cs.registerOutParameter(1, Types.CHAR);
				return cs;
			}
			
			public String callback(CallableStatement cs) throws SQLException {
				cs.execute();
				return cs.getString(1);
			}
		});
		
		System.out.println(r);
	}
}
