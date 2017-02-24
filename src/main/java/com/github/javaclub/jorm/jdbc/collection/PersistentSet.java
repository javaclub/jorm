/*
 * @(#)PersistentSet.java	2011-9-19
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.collection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.common.CommonUtil;

/**
 * A persistent wrapper for a <tt>java.util.Set</tt>. The underlying
 * collection is a <tt>HashSet</tt>.
 *
 * @see java.util.HashSet
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: PersistentSet.java 2011-9-19 下午02:54:48 Exp $
 */
@SuppressWarnings("unchecked")
public class PersistentSet extends AbstractPersistentCollection implements java.util.Set {
	
	private static final long serialVersionUID = -3473332871716667059L;
	
	protected Set set;
	protected transient List tempList;
	
	public PersistentSet() {
		super();
	}
	
	public PersistentSet(Session session, Object owner, Class persistentClass) {
		super(owner, persistentClass);
		this.set = new HashSet();
		tempList = (List) fetchCollection(0, getHoldingSize());
		this.set.addAll(tempList);
		tempList = null;
	}

	public long count() {
		return total;
	}
	
	public Object next() {
		int idx = getCursorIndex();
		if(idx >= getHoldingSize()) {
			idx = (getCursorIndex() % getHoldingSize());
			if(idx == 0) {
				this.set.clear();
				tempList = (List) fetchCollection(getCursorIndex(), getHoldingSize());
				this.set.addAll(tempList);
				tempList = null;
			}
		}
		Object result = CommonUtil.get(this.set, idx);
		// 下一个游标
		cursorIndex = getCursorIndex() + 1;
		return result;
	}

	/**
	 * @see java.util.Set#add(java.lang.Object)
	 */
	public boolean add(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see java.util.Set#clear()
	 */
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see java.util.Set#isEmpty()
	 */
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see java.util.Set#iterator()
	 */
	public Iterator iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see java.util.Set#size()
	 */
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see java.util.Set#toArray()
	 */
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see java.util.Set#toArray(T[])
	 */
	public Object[] toArray(Object[] a) {
		// TODO Auto-generated method stub
		return null;
	}

}
