/*
 * @(#)SqlParamsTest.java	2011-8-1
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.sql;

import com.github.javaclub.jorm.jdbc.sql.SqlParams;
import org.junit.Test;

/**
 * SqlParamsTest
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: SqlParamsTest.java 2011-8-1 下午05:39:08 Exp $
 */
@SuppressWarnings("unchecked")
public class SqlParamsTest {

	@Test
	public void testGetCount() {
		String sql = " SELECT FIRST_NAME, LAST_NAME, COUNT(*) FROM AUTHOR JOIN BOOK ON AUTHOR.ID = BOOK.AUTHOR_ID WHERE LANGUAGE = 'DE' AND PUBLISHED > '2008-01-01' GROUP BY FIRST_NAME, LAST_NAME HAVING COUNT(*) > 5 ORDER BY LAST_NAME ASC NULLS FIRST LIMIT 2 OFFSET 1";
		SqlParams params = new SqlParams(sql);
		System.out.println(params.getCountSql());
	}
}
