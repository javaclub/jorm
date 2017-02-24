/*
 * @(#)BasicFormatterImpl.java	2011-8-13
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.sql.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Performs formatting of basic SQL statements (DML + query).
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: BasicFormatterImpl.java 522 2011-10-08 10:29:16Z gerald.chen.hz $
 */
public class BasicFormatterImpl implements Formatter {

	private static final Set<String> BEGIN_CLAUSES = new HashSet<String>();
	private static final Set<String> END_CLAUSES = new HashSet<String>();
	private static final Set<String> LOGICAL = new HashSet<String>();
	private static final Set<String> QUANTIFIERS = new HashSet<String>();
	private static final Set<String> DML = new HashSet<String>();
	private static final Set<String> MISC = new HashSet<String>();
	
	public static final String WHITESPACE = " \n\r\f\t";

	static {
		BEGIN_CLAUSES.add( "left" );
		BEGIN_CLAUSES.add( "right" );
		BEGIN_CLAUSES.add( "inner" );
		BEGIN_CLAUSES.add( "outer" );
		BEGIN_CLAUSES.add( "group" );
		BEGIN_CLAUSES.add( "order" );

		END_CLAUSES.add( "where" );
		END_CLAUSES.add( "set" );
		END_CLAUSES.add( "having" );
		END_CLAUSES.add( "join" );
		END_CLAUSES.add( "from" );
		END_CLAUSES.add( "by" );
		END_CLAUSES.add( "join" );
		END_CLAUSES.add( "into" );
		END_CLAUSES.add( "union" );

		LOGICAL.add( "and" );
		LOGICAL.add( "or" );
		LOGICAL.add( "when" );
		LOGICAL.add( "else" );
		LOGICAL.add( "end" );

		QUANTIFIERS.add( "in" );
		QUANTIFIERS.add( "all" );
		QUANTIFIERS.add( "exists" );
		QUANTIFIERS.add( "some" );
		QUANTIFIERS.add( "any" );

		DML.add( "insert" );
		DML.add( "update" );
		DML.add( "delete" );

		MISC.add( "select" );
		MISC.add( "on" );
	}

	static final String indentString = "    ";
	static final String initial = "\n    ";

	public String format(String source) {
		return new FormatProcess( source ).perform();
	}

	private static class FormatProcess {
		boolean beginLine = true;
		boolean afterBeginBeforeEnd = false;
		boolean afterByOrSetOrFromOrSelect = false;
		@SuppressWarnings("unused")
		boolean afterValues = false;
		boolean afterOn = false;
		boolean afterBetween = false;
		boolean afterInsert = false;
		int inFunction = 0;
		int parensSinceSelect = 0;
		private LinkedList<Integer> parenCounts = new LinkedList<Integer>();
		private LinkedList<Boolean> afterByOrFromOrSelects = new LinkedList<Boolean>();

		int indent = 1;

		StringBuffer result = new StringBuffer();
		StringTokenizer tokens;
		String lastToken;
		String token;
		String lcToken;

		public FormatProcess(String sql) {
			tokens = new StringTokenizer(
					sql,
					"()+*/-=<>'`\"[]," + WHITESPACE,
					true
			);
		}

		public String perform() {

			result.append( initial );

			while ( tokens.hasMoreTokens() ) {
				token = tokens.nextToken();
				lcToken = token.toLowerCase();

				if ( "'".equals( token ) ) {
					String t;
					do {
						t = tokens.nextToken();
						token += t;
					}
					while ( !"'".equals( t ) && tokens.hasMoreTokens() ); // cannot handle single quotes
				}
				else if ( "\"".equals( token ) ) {
					String t;
					do {
						t = tokens.nextToken();
						token += t;
					}
					while ( !"\"".equals( t ) );
				}

				if ( afterByOrSetOrFromOrSelect && ",".equals( token ) ) {
					commaAfterByOrFromOrSelect();
				}
				else if ( afterOn && ",".equals( token ) ) {
					commaAfterOn();
				}

				else if ( "(".equals( token ) ) {
					openParen();
				}
				else if ( ")".equals( token ) ) {
					closeParen();
				}

				else if ( BEGIN_CLAUSES.contains( lcToken ) ) {
					beginNewClause();
				}

				else if ( END_CLAUSES.contains( lcToken ) ) {
					endNewClause();
				}

				else if ( "select".equals( lcToken ) ) {
					select();
				}

				else if ( DML.contains( lcToken ) ) {
					updateOrInsertOrDelete();
				}

				else if ( "values".equals( lcToken ) ) {
					values();
				}

				else if ( "on".equals( lcToken ) ) {
					on();
				}

				else if ( afterBetween && lcToken.equals( "and" ) ) {
					misc();
					afterBetween = false;
				}

				else if ( LOGICAL.contains( lcToken ) ) {
					logical();
				}

				else if ( isWhitespace( token ) ) {
					white();
				}

				else {
					misc();
				}

				if ( !isWhitespace( token ) ) {
					lastToken = lcToken;
				}

			}
			return result.toString();
		}

		private void commaAfterOn() {
			out();
			indent--;
			newline();
			afterOn = false;
			afterByOrSetOrFromOrSelect = true;
		}

		private void commaAfterByOrFromOrSelect() {
			out();
			newline();
		}

		private void logical() {
			if ( "end".equals( lcToken ) ) {
				indent--;
			}
			newline();
			out();
			beginLine = false;
		}

		private void on() {
			indent++;
			afterOn = true;
			newline();
			out();
			beginLine = false;
		}

		private void misc() {
			out();
			if ( "between".equals( lcToken ) ) {
				afterBetween = true;
			}
			if ( afterInsert ) {
				newline();
				afterInsert = false;
			}
			else {
				beginLine = false;
				if ( "case".equals( lcToken ) ) {
					indent++;
				}
			}
		}

		private void white() {
			if ( !beginLine ) {
				result.append( " " );
			}
		}

		private void updateOrInsertOrDelete() {
			out();
			indent++;
			beginLine = false;
			if ( "update".equals( lcToken ) ) {
				newline();
			}
			if ( "insert".equals( lcToken ) ) {
				afterInsert = true;
			}
		}

		private void select() {
			out();
			indent++;
			newline();
			parenCounts.addLast( new Integer( parensSinceSelect ) );
			afterByOrFromOrSelects.addLast( Boolean.valueOf( afterByOrSetOrFromOrSelect ) );
			parensSinceSelect = 0;
			afterByOrSetOrFromOrSelect = true;
		}

		private void out() {
			result.append( token );
		}

		private void endNewClause() {
			if ( !afterBeginBeforeEnd ) {
				indent--;
				if ( afterOn ) {
					indent--;
					afterOn = false;
				}
				newline();
			}
			out();
			if ( !"union".equals( lcToken ) ) {
				indent++;
			}
			newline();
			afterBeginBeforeEnd = false;
			afterByOrSetOrFromOrSelect = "by".equals( lcToken )
					|| "set".equals( lcToken )
					|| "from".equals( lcToken );
		}

		private void beginNewClause() {
			if ( !afterBeginBeforeEnd ) {
				if ( afterOn ) {
					indent--;
					afterOn = false;
				}
				indent--;
				newline();
			}
			out();
			beginLine = false;
			afterBeginBeforeEnd = true;
		}

		private void values() {
			indent--;
			newline();
			out();
			indent++;
			newline();
			afterValues = true;
		}

		private void closeParen() {
			parensSinceSelect--;
			if ( parensSinceSelect < 0 ) {
				indent--;
				parensSinceSelect = ( ( Integer ) parenCounts.removeLast() ).intValue();
				afterByOrSetOrFromOrSelect = ( ( Boolean ) afterByOrFromOrSelects.removeLast() ).booleanValue();
			}
			if ( inFunction > 0 ) {
				inFunction--;
				out();
			}
			else {
				if ( !afterByOrSetOrFromOrSelect ) {
					indent--;
					newline();
				}
				out();
			}
			beginLine = false;
		}

		private void openParen() {
			if ( isFunctionName( lastToken ) || inFunction > 0 ) {
				inFunction++;
			}
			beginLine = false;
			if ( inFunction > 0 ) {
				out();
			}
			else {
				out();
				if ( !afterByOrSetOrFromOrSelect ) {
					indent++;
					newline();
					beginLine = true;
				}
			}
			parensSinceSelect++;
		}

		private static boolean isFunctionName(String tok) {
			final char begin = tok.charAt( 0 );
			final boolean isIdentifier = Character.isJavaIdentifierStart( begin ) || '"' == begin;
			return isIdentifier &&
					!LOGICAL.contains( tok ) &&
					!END_CLAUSES.contains( tok ) &&
					!QUANTIFIERS.contains( tok ) &&
					!DML.contains( tok ) &&
					!MISC.contains( tok );
		}

		private static boolean isWhitespace(String token) {
			return WHITESPACE.indexOf( token ) >= 0;
		}

		private void newline() {
			result.append( "\n" );
			for ( int i = 0; i < indent; i++ ) {
				result.append( indentString );
			}
			beginLine = true;
		}
	}
	
	public static void main(String[] args) {
		String[] ARRAY = {};
		List<String> ls = new ArrayList<String>();
		ls.add("select * from dual");
		ls.add("SELECT * frOm dual");
		ls.add("Select C1,c2 From tb");
		ls.add("select c1,c2 from tb");
		ls.add("select count(*) from t1");
		ls.add("select c1,c2,c3 from t1 where condi1=1 ");
		ls.add("Select c1,c2,c3 From t1 Where condi1=1 ");
		ls.add("select c1,c2,c3 from t1,t2 where condi3=3 or condi4=5 order   by o1,o2");
		ls.add("Select c1,c2,c3 from t1,t2 Where condi3=3 or condi4=5 Order   by o1,o2");
		ls.add("select c1,c2,c3 from t1,t2,t3 where condi1=5 and condi6=6 or condi7=7 group  by g1,g2");
		ls.add("Select c1,c2,c3 From t1,t2,t3 Where condi1=5 and condi6=6 or condi7=7 Group  by g1,g2");
		ls.add("Select c1,c2,c3 From t1,t2,t3 Where condi1=5 and condi6=6 or condi7=7 Group  by g1,g2,g3 order  by g2,g3");
		ls.add("select c1,c2,c3 from t1,t2 where (condi3=3 and cc=2) or condi4=5 or (c1=9 and c3=10) order by o1,o2");
		ls.add("select * from role_info as r,user_role_relation as ur,user_info as u where u.user_id = ur.user_id and role_info = r.role_id and u.user_name = 'zeng'");
		ls.add("select name,count(*) from A group by name having count(*) > 1");
		ls.add("select name,sex,count(*) from A group by name,sex having count(*) > 1");
		ls.add("select a.id,a.name from t_user a");
		ls.add("SELECT FIRST_NAME, LAST_NAME, COUNT(*) FROM AUTHOR JOIN BOOK ON AUTHOR.ID = BOOK.AUTHOR_ID WHERE LANGUAGE = 'DE' AND PUBLISHED > '2008-01-01' GROUP BY FIRST_NAME, LAST_NAME HAVING COUNT(*) > 5 ORDER BY LAST_NAME ASC NULLS FIRST LIMIT 2 OFFSET 1 FOR UPDATE OF FIRST_NAME, LAST_NAME");
		ls.add("SELECT a.phone, a.count FROM (SELECT phone, COUNT(phone) as count FROM call_miss_phone GROUP BY phone) a where a.count > 0 order by a.phone desc");
		ls.add("SELECT a.phone, a.count FROM (SELECT phone, COUNT(phone) as count FROM call_miss_phone order BY oo desc) a where a.count > 0 order by a.phone desc");
		ARRAY = ls.toArray(new String[0]);
		
		Formatter formatter = new BasicFormatterImpl();
		for (int i = 0; i < ARRAY.length; i++) {
			System.out.println("JDBC: " + formatter.format(ARRAY[i]));
			System.out.println("=================================================");
		}
	}

}
