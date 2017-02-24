/*
 * @(#)Client.java	2011-8-31
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.basic;

import com.github.javaclub.jorm.jdbc.sql.SqlParams;
import com.github.javaclub.jorm.testentity.User;

/**
 * Client
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Client.java 2011-8-31 下午02:14:58 Exp $
 */
public class Client {

	public static void main(String[] args) {
		ConcretTop top = new ConcretTop();
		SqlParams<User> params = new SqlParams<User>("SELECT * FROM t_user");
		params.setObjectClass(User.class);
		params.setFirstResult(1);
		params.setMaxResults(100);
		System.out.println(top.loading(params));

		User u = new User();
		System.out.println(u);
		changeUser(u);
		System.out.println(u);
	}
	
	public static void changeUser(User user) {
		User u = new User(1, "gerald", "男", 26, "techer");
		user = u;
	}

}
