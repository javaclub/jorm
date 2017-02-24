/*
 * @(#)AbstractPersistentCollection.java	2011-9-19
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.collection;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.annotation.ManyToMany;
import com.github.javaclub.jorm.annotation.OneToMany;
import com.github.javaclub.jorm.common.Reflections;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.config.JdbcConfigXmlParser;
import com.github.javaclub.jorm.jdbc.ClassMetadata;
import com.github.javaclub.jorm.jdbc.JdbcException;
import com.github.javaclub.jorm.jdbc.sql.JoinFragment;
import com.github.javaclub.jorm.jdbc.sql.QueryStatement;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;
import com.github.javaclub.jorm.jdbc.sql.SqlPrepared;
import com.github.javaclub.jorm.jdbc.sql.util.PersistentEntityUtil;
import com.github.javaclub.jorm.proxy.LazyInitializationException;

/**
 * Base class implementing {@link PersistentCollection}
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: AbstractPersistentCollection.java 2011-9-19 下午01:16:38 Exp $
 */
@SuppressWarnings("unchecked")
public abstract class AbstractPersistentCollection implements Serializable, PersistentCollection {

	private static final long serialVersionUID = 1L;
	
	protected static final Log LOG = LogFactory.getLog(AbstractPersistentCollection.class);

	private transient Session session;
	private boolean initialized;
	private transient boolean initializing;
	
	private Object owner;
	private Class persistentClass;
	private boolean dirty;
	
	/** Collection's size */
	protected long total = -1;
	
	protected int cursorIndex = 0;
	
	/** The max amount of PersistentCollection's holding size. */
	protected int holdingSize = JdbcConfigXmlParser.constant("collection.holding_size") 
				== null ? 500 
				: JdbcConfigXmlParser.constant("collection.holding_size").intValue();

	/**
	 * Not called by Hibernate, but used by non-JDK serialization,
	 * eg. SOAP libraries.
	 */
	public AbstractPersistentCollection() {	}

	protected AbstractPersistentCollection(Object owner, Class persistentClass) {
		this.session = Jorm.getSession(true);
		this.owner = owner;
		this.persistentClass = persistentClass;
		this.total = this.getTotalSize();
	}

	public final void clearDirty() {
		dirty = false;
	}
	
	public final boolean isDirty() {
		return dirty;
	}

	public final void dirty() {
		dirty = true;
	}

	public abstract long count();
	
	public boolean hasNext() {
		return (getCursorIndex() < count());
	}

	public Object getOwner() {
		return this.owner;
	}
	
	public void setOwner(Object owner) {
		this.owner = owner;
	}
	
	/**
	 * Get the current session
	 */
	public final Session getSession() {
		return session;
	}
	
	/**
	 * Initialize the collection, if possible, wrapping any exceptions
	 * in a runtime exception
	 * 
	 * @param writing currently obsolete
	 * @throws LazyInitializationException if we cannot initialize
	 */
	protected final void initialize(boolean writing) {
		if (!initialized) {
			if (initializing) {
				throw new LazyInitializationException("illegal access to loading collection");
			}
			throwLazyInitializationExceptionIfNotConnected();
			// TODO: 初始化Collection
			/*session.initializeCollection(this, writing);*/
		}
	}
	
	protected int getCursorIndex() {
		return this.cursorIndex;
	}
	
	protected final int getHoldingSize() {
		return holdingSize;
	}

	/**
	 * Called by the <tt>size()</tt> method
	 */
	protected boolean readSize() {
		if (!initialized) {
			if (total != -1) {
				return true;
			} else {
			}
		}
		read();
		return false;
	}
	
	/**
	 * Called by any read-only method of the collection interface
	 */
	protected final void read() {
		initialize(false);
	}
	
	/**
	 * Called by any writer method of the collection interface
	 */
	protected final void write() {
		initialize(true);
		dirty();
	}
	
	public <T> Collection<T> presentAll() throws JdbcException {
		return this.fetchCollection(-1, -1);
	}
	
	protected long getTotalSize() {
		SqlParams params = prepareSqlParams(-1, -1);
		try {
			return session.count(params);
		} finally {
			params = null;
		}
	}
	
	protected <T> Collection<T> fetchCollection(int start, int limit) {
		SqlParams params = prepareSqlParams(start, limit);
		try {
			if(LOG.isInfoEnabled()) {
				LOG.info("Fetch collection start => " + start + ", limit => " + limit);
			}
			if(session.isAutoClose() && session.isClosed()) {
				LOG.warn("The holding session had been auto closed, requiring a new auto-close session.");
				this.session = Jorm.getSession(true);
			}
			return session.list(params);
		} finally {
			params = null;
		}
	}

	private SqlParams prepareSqlParams(int start, int limit) {
		ClassMetadata ownerMetadata = ClassMetadata.getClassMetadata(getOwner().getClass());
		ClassMetadata persitentClassMetadata = ClassMetadata.getClassMetadata(getPersistentClass());
		String[] fieldNames = null; Object[] keyValues = null; 
		boolean isOneToMany = true;
		Field field = matchedOneToMany(ownerMetadata.OneToManyFields, getPersistentClass());
		if(null == field) {
			field = matchedManyToMany(ownerMetadata.ManyToManyFields, getPersistentClass());
			isOneToMany = false;
		}
		
		SqlParams params = null;
		if(isOneToMany) {
			fieldNames = field.getAnnotation(OneToMany.class).selField();
			keyValues = getOwnerFieldsValue(field.getAnnotation(OneToMany.class), getOwner());
			params = SqlPrepared.preparedSelect(getPersistentClass(), fieldNames, keyValues);
		} else { // ManyToMany
			QueryStatement sql = new QueryStatement(session.getDialect());
			sql.addSelectColumn("r_mtm_table." + PersistentEntityUtil.entityIdColumname(ownerMetadata.clazz), 
						"r_mtm_" + PersistentEntityUtil.entityName(ownerMetadata.clazz) + "_id_");
			sql.addSelectColumn("r_mtm_table." + PersistentEntityUtil.entityIdColumname(persitentClassMetadata.clazz), 
					"r_mtm_" + Strings.lowerCase(persitentClassMetadata.clazz.getSimpleName()) + "_id_");
			// target query entity field columns
			sql.addSelectFragmentString("t_entity_" + Strings.lowerCase(persitentClassMetadata.clazz.getSimpleName()) + ".*");
			
			sql.getJoinFragment().addJoins(" " + ClassMetadata.getMappingTablename(getOwner(), field) + " r_mtm_table", "");
			sql.getJoinFragment().addJoin(persitentClassMetadata.tableName, 
					"t_entity_" + Strings.lowerCase(persitentClassMetadata.clazz.getSimpleName()), 
					new String[] { "r_mtm_table." + PersistentEntityUtil.entityIdColumname(persitentClassMetadata.clazz) }, 
					new String[] { persitentClassMetadata.column(persitentClassMetadata.identifierField.getName()) }, 
					JoinFragment.LEFT_OUTER_JOIN);
			sql.getJoinFragment().addCondition("r_mtm_table." + PersistentEntityUtil.entityIdColumname(ownerMetadata.clazz) + " = ?");
			
			// Jorm.format(sql.toQueryString());
			params = new SqlParams(sql.toQueryString(), new Object[] { Reflections.getFieldValue(getOwner(), ownerMetadata.identifierField) });
			params.setLoadAssociated(false);
		}
		
		params.setObjectClass(getPersistentClass());
		if(start > -1) {
			params.setFirstResult(start);
		}
		if(limit > -1) {
			params.setMaxResults(limit);
		}
		return params;
	}
	
	protected void mergeJoins(JoinFragment joinFragment) {
		
	}

	public Class getPersistentClass() {
		return this.persistentClass;
	}
	
	protected Boolean readElementExistence(Object element) {
		return new Boolean(session.has(element));
	}
	
	private void throwLazyInitializationExceptionIfNotConnected() {
		if ( null == session || session.isClosed() )  {
			throw new LazyInitializationException("No session or session was closed");
		}
	}
	
	private Object[] getOwnerFieldsValue(OneToMany otm, Object obj) {
		Object[] result = new Object[otm.ownerField().length];
		for (int i = 0; i < otm.ownerField().length; i++) {
			result[i] = Reflections.getFieldValue(obj, otm.ownerField()[i]);
		}
		try {
			return result;
		} finally {
			result = null;
		}
	}
	
	private Field matchedOneToMany(List<Field> list, Class<?> holdType) {
		for (Field field : list) {
			if(field.getAnnotation(OneToMany.class).type() == holdType) {
				return field;
			}
		}
		return null;
	}
	
	private Field matchedManyToMany(List<Field> list, Class<?> holdType) {
		for (Field field : list) {
			if(field.getAnnotation(ManyToMany.class).type() == holdType) {
				return field;
			}
		}
		return null;
	}

	final class IteratorProxy implements Iterator {
		
		private final Iterator iter;
		
		IteratorProxy(Iterator iter) {
			this.iter=iter;
		}
		
		public boolean hasNext() {
			return iter.hasNext();
		}

		public Object next() {
			return iter.next();
		}

		public void remove() {
			write();
			iter.remove();
		}

	}

	final class ListIteratorProxy implements ListIterator {
		
		private final ListIterator iter;
		
		ListIteratorProxy(ListIterator iter) {
			this.iter = iter;
		}
		public void add(Object o) {
			write();
			iter.add(o);
		}

		public boolean hasNext() {
			return iter.hasNext();
		}

		public boolean hasPrevious() {
			return iter.hasPrevious();
		}

		public Object next() {
			return iter.next();
		}

		public int nextIndex() {
			return iter.nextIndex();
		}

		public Object previous() {
			return iter.previous();
		}

		public int previousIndex() {
			return iter.previousIndex();
		}

		public void remove() {
			write();
			iter.remove();
		}

		public void set(Object o) {
			write();
			iter.set(o);
		}

	}

	class SetProxy implements java.util.Set {

		final Collection set;

		SetProxy(Collection set) {
			this.set=set;
		}
		public boolean add(Object o) {
			write();
			return set.add(o);
		}

		public boolean addAll(Collection c) {
			write();
			return set.addAll(c);
		}

		public void clear() {
			write();
			set.clear();
		}

		public boolean contains(Object o) {
			return set.contains(o);
		}

		public boolean containsAll(Collection c) {
			return set.containsAll(c);
		}

		public boolean isEmpty() {
			return set.isEmpty();
		}

		public Iterator iterator() {
			return new IteratorProxy( set.iterator() );
		}

		public boolean remove(Object o) {
			write();
			return set.remove(o);
		}

		public boolean removeAll(Collection c) {
			write();
			return set.removeAll(c);
		}

		public boolean retainAll(Collection c) {
			write();
			return set.retainAll(c);
		}

		public int size() {
			return set.size();
		}

		public Object[] toArray() {
			return set.toArray();
		}

		public Object[] toArray(Object[] array) {
			return set.toArray(array);
		}

	}

	final class ListProxy implements java.util.List {

		private final java.util.List list;

		ListProxy(java.util.List list) {
			this.list = list;
		}

		public void add(int index, Object value) {
			write();
			list.add(index, value);
		}

		/**
		 * @see java.util.Collection#add(Object)
		 */
		public boolean add(Object o) {
			write();
			return list.add(o);
		}

		/**
		 * @see java.util.Collection#addAll(Collection)
		 */
		public boolean addAll(Collection c) {
			write();
			return list.addAll(c);
		}

		/**
		 * @see java.util.List#addAll(int, Collection)
		 */
		public boolean addAll(int i, Collection c) {
			write();
			return list.addAll(i, c);
		}

		/**
		 * @see java.util.Collection#clear()
		 */
		public void clear() {
			write();
			list.clear();
		}

		/**
		 * @see java.util.Collection#contains(Object)
		 */
		public boolean contains(Object o) {
			return list.contains(o);
		}

		/**
		 * @see java.util.Collection#containsAll(Collection)
		 */
		public boolean containsAll(Collection c) {
			return list.containsAll(c);
		}

		/**
		 * @see java.util.List#get(int)
		 */
		public Object get(int i) {
			return list.get(i);
		}

		/**
		 * @see java.util.List#indexOf(Object)
		 */
		public int indexOf(Object o) {
			return list.indexOf(o);
		}

		/**
		 * @see java.util.Collection#isEmpty()
		 */
		public boolean isEmpty() {
			return list.isEmpty();
		}

		/**
		 * @see java.util.Collection#iterator()
		 */
		public Iterator iterator() {
			return new IteratorProxy( list.iterator() );
		}

		/**
		 * @see java.util.List#lastIndexOf(Object)
		 */
		public int lastIndexOf(Object o) {
			return list.lastIndexOf(o);
		}

		/**
		 * @see java.util.List#listIterator()
		 */
		public ListIterator listIterator() {
			return new ListIteratorProxy( list.listIterator() );
		}

		/**
		 * @see java.util.List#listIterator(int)
		 */
		public ListIterator listIterator(int i) {
			return new ListIteratorProxy( list.listIterator(i) );
		}

		/**
		 * @see java.util.List#remove(int)
		 */
		public Object remove(int i) {
			write();
			return list.remove(i);
		}

		/**
		 * @see java.util.Collection#remove(Object)
		 */
		public boolean remove(Object o) {
			write();
			return list.remove(o);
		}

		/**
		 * @see java.util.Collection#removeAll(Collection)
		 */
		public boolean removeAll(Collection c) {
			write();
			return list.removeAll(c);
		}

		/**
		 * @see java.util.Collection#retainAll(Collection)
		 */
		public boolean retainAll(Collection c) {
			write();
			return list.retainAll(c);
		}

		/**
		 * @see java.util.List#set(int, Object)
		 */
		public Object set(int i, Object o) {
			write();
			return list.set(i, o);
		}

		/**
		 * @see java.util.Collection#size()
		 */
		public int size() {
			return list.size();
		}

		/**
		 * @see java.util.List#subList(int, int)
		 */
		public List subList(int i, int j) {
			return list.subList(i, j);
		}

		/**
		 * @see java.util.Collection#toArray()
		 */
		public Object[] toArray() {
			return list.toArray();
		}

		/**
		 * @see java.util.Collection#toArray(Object[])
		 */
		public Object[] toArray(Object[] array) {
			return list.toArray(array);
		}

	}

}
