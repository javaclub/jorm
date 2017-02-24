/*
 * @(#)Isolater.java	2011-9-8
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.work;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.jdbc.DBUtil;
import com.github.javaclub.jorm.jdbc.JdbcException;
import com.github.javaclub.jorm.jdbc.work.IsolatedWork.Preference;

/**
 * Isolater
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Isolater.java 383 2011-09-16 05:30:31Z gerald.chen.hz $
 */
public class Isolater {

	private static final Log LOG = LogFactory.getLog(Isolater.class);
	
	public static void doIsolatedWork(IsolatedWork work, Session session) throws JdbcException {
		new JdbcDelegate(session).delegateWork(work, true);
	}
	
	public static void doWorkInNewTransaction(IsolatedWork work, Session session) throws JdbcException {

		Connection connection = null;
		boolean commit = false, rollback = false;
		try {
			connection = session.getConnection(true);
			if (connection.getAutoCommit()) {
				commit = true;
				connection.setAutoCommit(false);
			}
			
			if(work.getPreference() == Preference.CONNECTION) {
				work.doWork(connection);
				work.doWork(session);
			} else {
				work.doWork(session);
				work.doWork(connection);
			}
		} catch (Throwable t) {
			t.printStackTrace();
			rollback = true;
			try {
				if (connection != null && !connection.isClosed()) {
					if (LOG.isInfoEnabled()) {
						LOG.info("Rolling back transaction of Session => " + session);
					}
					if(commit) {
						connection.rollback();
					}
				}
			} catch (Throwable ignore) {
				LOG.error("unable to release connection on exception ["
						+ ignore + "]");
			}

			if (t instanceof JdbcException) {
				throw (JdbcException) t;
			} else if (t instanceof SQLException) {
				throw new JdbcException("error performing isolated work",
						(SQLException) t);
			} else {
				throw new JdbcException("error performing isolated work", t);
			}
		} finally {
			if (connection != null) {
				if (commit) {
					if(!rollback) {
						try {
							connection.commit();
							connection.setAutoCommit(true);
						} catch (Throwable ignore) {
							LOG.error("was unable to reset connection back to auto-commit");
						}
					}
				}
			}
		}
	
	}
	
	/**
	 * Ensures that all processing actually performed by the given work will
	 * occur outside of a transaction, using a new JDBC Connection required from <tt>JdbcSession</tt>.
	 *
	 * @param work The work to be performed.
	 * @param session The JdbcSession from which this request is originating.
	 * @throws JdbcException
	 */
	public static void doNonTransactedWork(IsolatedWork work, Session session) throws JdbcException {
		new JdbcDelegate(session).delegateWork(work, false);
	}
	
	public static void doStepWork(StepWork work, Session session) throws JdbcException {
		new StepDelegateImpl(session).delegateWork(work, true);
	}
	
	public static void doNonTransactedStepWork(StepWork work, Session session)
			throws JdbcException {
		try {
			
			work.execute(session);

		} catch (Throwable t) {
			t.printStackTrace();
			if (t instanceof JdbcException) {
				throw (JdbcException) t;
			} else if (t instanceof SQLException) {
				throw new JdbcException("Error performing isolated work",
						(SQLException) t);
			} else {
				throw new JdbcException("Error performing isolated work", t);
			}
		} finally {
			
		}

	}
	
	private static interface Delegate {
		public void delegateWork(IsolatedWork work, boolean transacted) throws JdbcException;
	}
	
	private static interface StepDelegate {
		public void delegateWork(StepWork work, boolean transacted) throws JdbcException;
	}
	
	public static class StepDelegateImpl implements StepDelegate {
		
		private final Session session;
		
		public StepDelegateImpl(Session session) {
			this.session = session;
		}

		public void delegateWork(StepWork work, boolean transacted)
				throws JdbcException {
			Connection connection = null;
			boolean commit = false, rollback = false;
			try {
				if (transacted) {
					connection = session.getConnection();
					if (connection.getAutoCommit()) {
						commit = true;
						connection.setAutoCommit(false);
					}
				} else {
					connection = session.getConnection(true);
					if(LOG.isInfoEnabled()) {
						LOG.info("Required a new JDBC Connection[" + connection + "] from Session.");
					}
				}

				work.execute(session);

			} catch (Throwable t) {
				t.printStackTrace();
				rollback = true;
				try {
					if (transacted && connection != null
							&& !connection.isClosed()) {
						if (LOG.isInfoEnabled()) {
							LOG.info("Rolling back transaction of Session => " + session);
						}
						if(commit) {
							connection.rollback();
						}
					}
				} catch (Throwable ignore) {
					LOG.error("unable to release connection on exception ["
							+ ignore + "]");
				}

				if (t instanceof JdbcException) {
					throw (JdbcException) t;
				} else if (t instanceof SQLException) {
					throw new JdbcException("Error performing isolated work",
							(SQLException) t);
				} else {
					throw new JdbcException("Error performing isolated work", t);
				}
			} finally {
				if (connection != null) {
					if (transacted && commit) {
						if(!rollback) {
							try {
								connection.commit();
								connection.setAutoCommit(true);
							} catch (Throwable ignore) {
								LOG.error("Unable to reset connection back to auto-commit");
							}
						}
					}
					if(!transacted) {
						if(LOG.isInfoEnabled()) {
							LOG.info("Closing the new JDBC Connection[" + connection + "] required from Session.");
						}
						DBUtil.closeQuietly(connection);
					}
				}
			}
		}
		
	}
	
	/**
	 * An isolation delegate for JDBC-based transactions. 
	 */
	public static class JdbcDelegate implements Delegate {
		
		private final Session session;

		public JdbcDelegate(Session session) {
			this.session = session;
		}

		public void delegateWork(IsolatedWork work, boolean transacted)
				throws JdbcException {
			Connection connection = null;
			boolean commit = false, rollback = false;
			try {
				if (transacted) {
					connection = session.getConnection();
					if (connection.getAutoCommit()) {
						commit = true;
						connection.setAutoCommit(false);
					}
				} else {
					connection = session.getConnection(true);
					if(LOG.isInfoEnabled()) {
						LOG.info("Required a new JDBC Connection[" + connection + "] from Session.");
					}
				}

				if(work.getPreference() == Preference.CONNECTION) {
					work.doWork(connection);
					work.doWork(session);
				} else {
					work.doWork(session);
					work.doWork(connection);
				}
			} catch (Throwable t) {
				t.printStackTrace();
				rollback = true;
				try {
					if (transacted && connection != null
							&& !connection.isClosed()) {
						if (LOG.isInfoEnabled()) {
							LOG.info("Rolling back transaction of Session => " + session);
						}
						if(commit) {
							connection.rollback();
						}
					}
				} catch (Throwable ignore) {
					LOG.error("Unable to release connection on exception ["
							+ ignore + "]");
				}

				if (t instanceof JdbcException) {
					throw (JdbcException) t;
				} else if (t instanceof SQLException) {
					throw new JdbcException("Error performing isolated work",
							(SQLException) t);
				} else {
					throw new JdbcException("Error performing isolated work", t);
				}
			} finally {
				if (connection != null) {
					if (transacted && commit) {
						if(!rollback) {
							try {
								connection.commit();
								connection.setAutoCommit(true);
							} catch (Throwable ignore) {
								LOG.error("Unable to reset connection back to auto-commit");
							}
						}
					}
					if(!transacted) {
						if(LOG.isInfoEnabled()) {
							LOG.info("Closing the new JDBC Connection[" + connection + "] required from Session.");
						}
						DBUtil.closeQuietly(connection);
					}
				}
			}
		}
	}
}
