/*
 * @(#)DBUtil.java	2009-7-22
 *
 * Copyright (c) 2009 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.javaclub.jorm.common.AssertUtil;
import com.github.javaclub.jorm.common.Strings;

/**
 * Jdbc utility class.
 *
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: DBUtil.java 547 2011-10-09 10:10:45Z gerald.chen.hz $
 */
public class DBUtil {
	
	/** Logger for this class */
	protected static final Log LOG = LogFactory.getLog(DBUtil.class);
	
	private DBUtil() { }
	
	public static Connection getConnection(String driver, String dburl, String username, String password) {
		Connection conn = null;
		if(loadDriver(driver)) {
			try {
				conn = DriverManager.getConnection(dburl, username, password);
			} catch (SQLException e) {
				throw new RuntimeException("failed to get the connection.", e);
			}
		} else {
			throw new RuntimeException("failed to load the jdbc driver.");
		}
		return conn;
	}
	
	public static int getDatabaseType(Connection conn) throws SQLException {
		AssertUtil.notNull(conn);
		DatabaseMetaData dbmd = conn.getMetaData();
		String dbName = dbmd.getDatabaseProductName();
        if(Strings.isEmpty(dbName)) {
        	return DatabaseType.UNKNOWN_TYPE;
        }
        dbName = dbName.toLowerCase();

        if (dbName.indexOf("mysql") > -1) {
            return DatabaseType.MYSQL;
        } else if (dbName.indexOf("sql server") > -1) {
            return DatabaseType.MSSQL;
        } else if (dbName.indexOf("oracle") > -1) {
            return DatabaseType.ORACLE;
        } else if (dbName.indexOf("postgresql") > -1) {
            return DatabaseType.PostgreSQL;
        } else if (dbName.indexOf("h2") > -1) {
            return DatabaseType.H2;
        } 
        // else if ("DB2".equalsIgnoreCase(dbName.substring(0, 3)))
        // return (TransactionManager.DB2);
        // else if ("HSQL".equalsIgnoreCase(dbName.substring(0, 4)))
        // return TransactionManager.HSQL;
        // else if ("PostgreSQL".equalsIgnoreCase(dbName))
        // return TransactionManager.POSTGRESQL;
        return DatabaseType.UNKNOWN_TYPE;
	}
	
	public static DatabaseInfo getDatabaseInfo(Connection conn) throws SQLException {
		DatabaseMetaData dbmd = conn.getMetaData();
		DatabaseInfo info = new DatabaseInfo(dbmd.getDatabaseProductName(), 
				dbmd.getDatabaseProductVersion(), dbmd.getURL(), 
				dbmd.getUserName(), dbmd.getDriverName(), dbmd.getDriverVersion());
		return info;
	}
	
	/**
     * Gets all the column names with data types of table with name table.
     *
     * @param table target table
     * @return columns map, key is column name, and value is mapped object
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
	public static Map getColumns(Connection conn, String table) throws SQLException {
        Map result = new TreeMap();
        DatabaseMetaData dmd = conn.getMetaData();

        ResultSet rs = dmd.getColumns(null, null, table.toUpperCase(), null);
        // NPE 4.12.2007

        if(LOG.isDebugEnabled()) {
        	LOG.debug("rs=" + rs);
        }
        if (rs != null && !rs.next()) {
        	if(DatabaseType.PostgreSQL == getDatabaseType(conn)) {
        		// 如果是PostgreSQL
        		Statement st = conn.createStatement();
        		ResultSet rss = st.executeQuery("SELECT attname,typname,adsrc FROM pg_attribute INNER JOIN pg_class  ON pg_attribute.attrelid = pg_class.oid INNER JOIN pg_type ON pg_attribute.atttypid = pg_type.oid LEFT OUTER JOIN pg_attrdef ON pg_attrdef.adrelid = pg_class.oid AND pg_attrdef.adnum = pg_attribute.attnum LEFT OUTER JOIN pg_description ON pg_description.objoid = pg_class.oid AND pg_description.objsubid = pg_attribute.attnum WHERE pg_attribute.attnum > 0 AND attisdropped <> 't' AND pg_class.relname = '" + table + "' ORDER BY pg_attribute.attnum");
        		if(rss != null && rss.next()) {
        			do {
                    	if(LOG.isDebugEnabled()) {
                    		LOG.debug("col=" + rs.getObject(1));
                    	}
                        String col = rss.getObject(1).toString().toLowerCase();
                        result.put(col, "Column");
                    } while (rss.next());

                    return result;
        		}
        	} else {
        		throw new SQLException("No columns");
        	}
        }
        
        do {
        	if(LOG.isDebugEnabled()) {
        		LOG.debug("col=" + rs.getObject(4));
        	}
            String col = rs.getObject(4).toString().toLowerCase();
            String type = getDataType(rs.getInt(5));
            result.put(col, type);
        } while (rs.next());

        return result;
    }

    @SuppressWarnings("unchecked")
	public static Map getPrimaryKeys(Connection conn, String table) {
        Map result = new TreeMap();
        try {
            DatabaseMetaData dmd = conn.getMetaData();

            ResultSet rs = dmd.getPrimaryKeys(null, null, table);
            while (rs.next()) {
                String col = rs.getString(4);
                result.put(col, "Primary Key");
            }
        } catch (SQLException e) {
        	if(LOG.isWarnEnabled()) {
        		LOG.warn(e.getMessage());
        	}
        }
        if(LOG.isDebugEnabled()) {
        	LOG.debug("Entity Name = " + table + " Primary Key(s) = " + result);
        }

        return result;
    }

    /**
     * Gets all tables in specified database
     *
     * @param conn Jdbc Connection
     * @param database database name
     * @return tables mapping
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
	public static Map getTables(Connection conn, String database) throws SQLException {

        Map result = new TreeMap();
        DatabaseMetaData dmd = conn.getMetaData();
        ResultSet rs = null;
        rs = dmd.getTables(database, null, null, new String[] {"TABLE"});

        // removed schema 3.22.05
        while (rs.next()) {
            // logger.info("getTables :: " + rs.getString(3));
            String tbl = rs.getString(3);
            // result.put(tbl.toLowerCase(), rs.getString(4));// table or view
            result.put(tbl, rs.getString(4));
        }

        // MS SQL Server system tables
        result.remove("syssegments");
        result.remove("sysconstraints");
        return result;
    }
    
    @SuppressWarnings("unchecked")
	public static <T> T queryUniqueObject(Connection conn, String sql, boolean closeConnection) {
    	java.sql.Statement stmt = null;
    	java.sql.ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return (T) rs.getObject(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

    
	public static int executeUpdate(Connection conn, String sql,
			Object[] params, boolean closeConnection) {
		int returnVal = 0;
		java.sql.PreparedStatement pstmt = null;
		boolean commit = false, rollback = false;
		try {
    		if(conn.getAutoCommit()) {
    			conn.setAutoCommit(false);
    			commit = true;
    		}
    		pstmt = conn.prepareStatement(sql);
    		JdbcUtil.setParameters(pstmt, params);
			returnVal = pstmt.executeUpdate();
		} catch (SQLException e) {
			rollback = true;
			if(commit) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		} finally {
			if (commit) {
				try {
					if (!rollback) {
						conn.commit();
					}
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			closeQuietly(pstmt);
			if (closeConnection) {
				closeQuietly(conn);
			}
		}
		return returnVal;
	}
    
    public static void executeSQL(Connection conn, String sql, boolean closeConnection) {
    	java.sql.Statement stmt = null;
    	boolean commit = false, rollback = false;
    	try {
    		if(conn.getAutoCommit()) {
    			conn.setAutoCommit(false);
    			commit = true;
    		}
    		stmt = conn.createStatement();
    		stmt.execute(sql);
		} catch (SQLException e) {
			rollback = true;
			if(commit) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			e.printStackTrace();
		} finally {
			if (commit) {
				try {
					if (!rollback) {
						conn.commit();
					}
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			closeQuietly(stmt);
			if (closeConnection) {
				closeQuietly(conn);
			}
		}
    }
    
    /**
     * Close a <code>Connection</code>, avoid closing if null.
     *
     * @param conn Connection to close.
     * @throws SQLException if a database access error occurs
     */
    public static void close(Connection conn) throws SQLException {
        if (conn != null) {
        	String present = conn.toString();
            conn.close();
			if (LOG.isInfoEnabled() && Strings.isNotEmpty(present)
					&& !("NULL".equals(present) || "null".equals(present))) {
				LOG.info("Connection [" + present + "] is closed.");
				present = null;
			}
        }
    }

    /**
     * Close a <code>ResultSet</code>, avoid closing if null.
     *
     * @param rs ResultSet to close.
     * @throws SQLException if a database access error occurs
     */
    public static void close(ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
    }

    /**
     * Close a <code>Statement</code>, avoid closing if null.
     *
     * @param stmt Statement to close.
     * @throws SQLException if a database access error occurs
     */
    public static void close(Statement stmt) throws SQLException {
        if (stmt != null) {
            stmt.close();
        }
    }

    /**
     * Close a <code>Connection</code>, avoid closing if null and hide
     * any SQLExceptions that occur.
     *
     * @param conn Connection to close.
     */
    public static void closeQuietly(Connection conn) {
        try {
            close(conn);
        } catch (SQLException e) {
            // quiet
        }
    }
    
    /**
     * Close a <code>ResultSet</code>, avoid closing if null and hide any
     * SQLExceptions that occur.
     *
     * @param rs ResultSet to close.
     */
    public static void closeQuietly(ResultSet rs) {
        try {
            close(rs);
        } catch (SQLException e) {
            // quiet
        }
    }

    /**
     * Close a <code>Statement</code>, avoid closing if null and hide
     * any SQLExceptions that occur.
     *
     * @param stmt Statement to close.
     */
    public static void closeQuietly(Statement stmt) {
        try {
            close(stmt);
        } catch (SQLException e) {
            // quiet
        }
    }

    /**
     * Close a <code>Connection</code>, <code>Statement</code> and 
     * <code>ResultSet</code>.  Avoid closing if null and hide any 
     * SQLExceptions that occur.
     *
     * @param conn Connection to close.
     * @param stmt Statement to close.
     * @param rs ResultSet to close.
     */
    public static void closeQuietly(Connection conn, Statement stmt, ResultSet rs) {

        try {
            closeQuietly(rs);
        } finally {
            try {
                closeQuietly(stmt);
            } finally {
                closeQuietly(conn);
            }
        }

    }
    
	public static void closeQuietly(Connection conn, Statement stmt,
			PreparedStatement pstmt, ResultSet rs) {
		try {
			closeQuietly(rs);
		} finally {
			try {
				closeQuietly(stmt);
				closeQuietly(pstmt);
			} finally {
				closeQuietly(conn);
			}
		}

	}

    /**
     * Commits a <code>Connection</code> then closes it, avoid closing if null.
     *
     * @param conn Connection to close.
     * @throws SQLException if a database access error occurs
     */
    public static void commitAndClose(Connection conn) throws SQLException {
        if (conn != null) {
            try {
                conn.commit();
            } finally {
                conn.close();
            }
        }
    }

    /**
     * Commits a <code>Connection</code> then closes it, avoid closing if null 
     * and hide any SQLExceptions that occur.
     *
     * @param conn Connection to close.
     */
    public static void commitAndCloseQuietly(Connection conn) {
        try {
            commitAndClose(conn);
        } catch (SQLException e) {
            // quiet
        }
    }
    
    /**
     * Rollback any changes made on the given connection.
     * @param conn Connection to rollback.  A null value is legal.
     * @throws SQLException if a database access error occurs
     */
    public static void rollback(Connection conn) throws SQLException {
        if (conn != null) {
            conn.rollback();
        }
    }
    
    public static void rollbackQuietly(Connection conn) {
        if (conn != null) {
            try {
				conn.rollback();
			} catch (SQLException e) {
				// ignore
			}
        }
    }
    
    /**
     * Performs a rollback on the <code>Connection</code> then closes it, 
     * avoid closing if null.
     *
     * @param conn Connection to rollback.  A null value is legal.
     * @throws SQLException if a database access error occurs
     * @since DbUtils 1.1
     */
    public static void rollbackAndClose(Connection conn) throws SQLException {
        if (conn != null) {
            try {
                conn.rollback();
            } finally {
                conn.close();
            }
        }
    }

    /**
     * Performs a rollback on the <code>Connection</code> then closes it, 
     * avoid closing if null and hide any SQLExceptions that occur.
     *
     * @param conn Connection to rollback.  A null value is legal.
     * @since DbUtils 1.1
     */
    public static void rollbackAndCloseQuietly(Connection conn) {
        try {
            rollbackAndClose(conn);
        } catch (SQLException e) {
            // quiet
        }
    }

    /**
     * Loads and registers a database driver class.
     * If this succeeds, it returns true, else it returns false.
     *
     * @param driverClassName of driver to load
     * @return boolean <code>true</code> if the driver was found, otherwise <code>false</code>
     */
    public static boolean loadDriver(String driverClassName) {
        try {
            Class.forName(driverClassName).newInstance();
            return true;

        } catch (ClassNotFoundException e) {
            return false;

        } catch (IllegalAccessException e) {
            // Constructor is private, OK for DriverManager contract
            return true;

        } catch (InstantiationException e) {
            return false;

        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * Print the stack trace for a SQLException to STDERR.
     *
     * @param e SQLException to print stack trace of
     */
    public static void printStackTrace(SQLException e) {
        printStackTrace(e, new PrintWriter(System.err));
    }

    /**
     * Print the stack trace for a SQLException to a 
     * specified PrintWriter. 
     *
     * @param e SQLException to print stack trace of
     * @param pw PrintWriter to print to
     */
    public static void printStackTrace(SQLException e, PrintWriter pw) {

        SQLException next = e;
        while (next != null) {
            next.printStackTrace(pw);
            next = next.getNextException();
            if (next != null) {
                pw.println("Next SQLException:");
            }
        }
    }

    /**
     * Print warnings on a Connection to STDERR.
     *
     * @param conn Connection to print warnings from
     */
    public static void printWarnings(Connection conn) {
        printWarnings(conn, new PrintWriter(System.err));
    }

    /**
     * Print warnings on a Connection to a specified PrintWriter. 
     *
     * @param conn Connection to print warnings from
     * @param pw PrintWriter to print to
     */
    public static void printWarnings(Connection conn, PrintWriter pw) {
        if (conn != null) {
            try {
                printStackTrace(conn.getWarnings(), pw);
            } catch (SQLException e) {
                printStackTrace(e, pw);
            }
        }
    }
    
    private static String getDataType(int type) {
        Integer jType = new Integer(type);
        String result = (String) DataTypeMappings.TYPE_MAPPINGS.get(jType);
        if (result == null) {
            result = "Object";
        }

        return result;
    }

}
