/*
 * @(#)PersistentList.java	2011-9-19
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.github.javaclub.jorm.Session;

/**
 * A persistent wrapper for a <tt>java.util.List</tt>. Underlying
 * collection is an <tt>ArrayList</tt>.
 *
 * @see java.util.ArrayList
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: PersistentList.java 2011-9-19 下午02:47:18 Exp $
 */
@SuppressWarnings("unchecked")
public class PersistentList extends AbstractPersistentCollection implements java.util.List {

	private static final long serialVersionUID = 9169693249257346989L;
	
	protected List list;
	
	public PersistentList() {} // needed for SOAP libraries, etc
	
	public PersistentList(Session session, Object owner, Class persistentClass) {
		super(owner, persistentClass);
		this.list = (List) fetchCollection(0, getHoldingSize());
	}

	public long count() {
		return total;
	}

	public Object next() {
		int idx = getCursorIndex();
		if(idx >= getHoldingSize()) {
			idx = (getCursorIndex() % getHoldingSize());
			if(idx == 0) {
				this.list = (List) fetchCollection(getCursorIndex(), getHoldingSize());
			}
		}
		Object result = this.get(idx);
		// 下一个游标
		cursorIndex = getCursorIndex() + 1;
		return result;
	}

	/**
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(Object o) {
		boolean flag = this.list.add(o);
		if(flag) {
			dirty();
			// TODO addToInserts();
		}
		return flag;
	}

	/**
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int index, Object element) {
		this.list.add(index, element);
		dirty();
		// TODO addToInserts();
	}

	/**
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection c) {
		boolean flag = this.list.addAll(c);
		if(flag) {
			dirty();
			// TODO addToInserts();
		}
		return flag;
	}

	/**
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int index, Collection c) {
		if ( c.size()>0 ) {
			dirty();
			// TODO addToInserts();
			return list.addAll(index,  c);
		} else {
			return false;
		}
	}

	/**
	 * @see java.util.List#clear()
	 */
	public void clear() {
		this.list.clear();
		// TODO 清除所有
		dirty();
	}

	/**
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		Boolean exists = readElementExistence(o);
		return exists == null ? list.contains(o) : exists.booleanValue();
	}

	/**
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection c) {
		boolean r = true;
		Boolean exists = Boolean.FALSE;
		for (Object object : c) {
			exists = readElementExistence(object);
			if(null == exists || false == exists.booleanValue()) {
				r = false;
				break;
			}
		}
		return r;
	}

	/**
	 * @see java.util.List#get(int)
	 */
	public Object get(int index) {
		if (index < 0) {
			throw new ArrayIndexOutOfBoundsException("Negative index");
		}
		if(index >= holdingSize) {
			return null;
		}
		return list.get(index);
	}

	/**
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	/**
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty() {
		return list.isEmpty();
	}

	/**
	 * @see java.util.List#iterator()
	 */
	public Iterator iterator() {
		return new IteratorProxy( list.iterator() );
	}

	/**
	 * @see java.util.List#lastIndexOf(java.lang.Object)
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
	public ListIterator listIterator(int index) {
		return new ListIteratorProxy( list.listIterator(index) );
	}

	/**
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		dirty();
		// TODO addToDeletes()
		return list.remove(o);
	}

	/**
	 * @see java.util.List#remove(int)
	 */
	public Object remove(int index) {
		Object obj = list.remove(index);
		if(null != obj) {
			dirty();
			// TODO addToDeletes()
		}
		return obj;
	}

	/**
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection c) {
		if (c.size() > 0) {
			if (list.removeAll(c)) {
				dirty();
				// TODO addToDeletes()
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection c) {
		if (list.retainAll(c)) {
			dirty();
			// TODO addToDeletes()
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public Object set(int index, Object element) {
		return this.list.set(index, element);
	}

	/**
	 * @see java.util.List#size()
	 */
	public int size() {
		return list.size();
	}

	/**
	 * @see java.util.List#subList(int, int)
	 */
	public List subList(int fromIndex, int toIndex) {
		return new ListProxy( list.subList(fromIndex, toIndex) );
	}

	/**
	 * @see java.util.List#toArray()
	 */
	public Object[] toArray() {
		return list.toArray();
	}

	/**
	 * @see java.util.List#toArray(T[])
	 */
	public Object[] toArray(Object[] a) {
		return list.toArray(a);
	}

	public String toString() {
		if(list.size() == this.count()) {
			return list.toString();
		}
		int start = (getCursorIndex() / getHoldingSize()) * getHoldingSize();
		return "{ count = " + count() 
				+ ", current = [" + start + " - " + ((start + getHoldingSize()) >= count() ? (count() - 1) : (start + getHoldingSize())) 
				+ "], items = " + list.toString() + " }";
	}
	
}
