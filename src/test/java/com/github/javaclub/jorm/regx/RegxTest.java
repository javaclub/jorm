/*
 * @(#)RegxTest.java	2011-8-4
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.regx;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaclub.jorm.common.Strings;
import org.junit.Test;

/**
 * RegxTest
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: RegxTest.java 208 2011-08-05 08:46:10Z gerald.chen.hz $
 */
public class RegxTest {

	@Test
	public void testSQL() {
		String text = "SELECT a.phone, a.count FROM (SELECT phone, COUNT(phone) as count FROM call_miss_phone GROUP BY phone) a where a.count > 0 order by a.phone desc";
		String regex = "(select)(.+)(from)";
		
		/*
		String s0 = "select * from a";
		String s1 = "select * from a order by id desc";
		String s2 = "select * from (select * from a where id > 100)";
		String s3 = "select * from (select * from a where id > 100) order by col desc";
		String s4 = "select * from (select * from a where id > 100 order by id desc) order by col desc";
		String s5 = "select * from (select * from a where id > 100 order by id desc)";
		*/
		
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

		Matcher matcher = pattern.matcher(text);

		while (matcher.find()) {
			System.out.println(matcher.group());
		}
	}
	
	@Test
	public void testHasLimit() {
		List<String> list = Arrays.asList(new String[]{
				"select * from t",
				"select * from t limit 0, 3",
				"select * from t order by t.id desc limit 0, 3",
				"select * from (select * from a where id > 100) order by col desc",
				"select * from (select * from a where id > 100 limit 2, 80) order by col desc",
				"select * from (select * from a where id > 100) order by col desc limit 0, 3"
		});
		
		for (String sql : list) {
			String regex = "(select\\s+)(.+\\s+)(from\\s+)(.+\\s+)([^\\(]+)(\\s+limit\\s+)([^\\)]+)";
			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(sql);
			if(matcher.find()) {
				String matched = matcher.group();
				if(!matched.endsWith(")") && matched.equalsIgnoreCase(sql)) {
					System.out.println(sql + "\n" + true);
				}
			} else {
				String lower = Strings.lowerCase(sql);
				if (Strings.count(lower, "select") == 1
						&& Strings.count(lower, "limit") == 1) {
					System.out.println(sql + "\n" + true);
				}
			}
		}
		
	}
	
	/*
	
	sql = "select * from a";
	sql = "select * from a order by id desc";
	sql = "select * from (select * from a where id > 100)";
	sql = "select * from (select * from a where id > 100) order by col desc";
	sql = "select * from (select * from a where id > 100 order by id desc) order by col desc";
	sql = "select * from (select * from a where id > 100 order by id desc)";
	sql = "select * from (select * from a where id > 100 order by id desc) where kk=6";
	sql = "select * from (select * from a where id > 100 order by id desc) where kk=6 order by kk asc";
	sql = "select * from (select * from a where id > 100 order by id desc) where kk in(select id from tt) order by kk asc";
	sql = "select * from (select * from a where id > 100 order by id desc) where kk in(select id from tt)";
	sql = "select * from tt where kk=6 order by id desc";
	
	*/
	
	public static boolean hasOrderBy(final String sql) {
		String regex = "(select\\s+)(.+\\s+)(from\\s+)(.+\\s+)([^\\(]+)(\\s+order\\s+by\\s+)([^\\)]+)";
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
	
	public static void main(String[] args) {
		List<String> list = Arrays.asList(new String[] {
				"select * from a",
				"select * from a order by id desc",
				"select * from (select * from a where id > 100)",
				"select * from (select * from a where id > 100) order by col desc",
				"select * from (select * from a where id > 100 order by id desc) order by col desc",
				"select * from (select * from a where id > 100 order by id desc)",
				"select * from (select * from a where id > 100 order by id desc) where kk=6",
				"select * from (select * from a where id > 100 order by id desc) where kk=6 order by kk asc",
				"select * from (select * from a where id > 100 order by id desc) where kk in(select id from tt) order by kk asc",
				"select * from (select * from a where id > 100 order by id desc) where kk in(select id from tt)",
				"select * from tt where kk=6 order by id desc"	
		});
		
		for (String string : list) {
			if(hasOrderBy(string)) {
				System.out.println(string + "\n" + true);
			} else {
				System.out.println(string + "\n" + false);
			}
		}
		
	}
	
}
