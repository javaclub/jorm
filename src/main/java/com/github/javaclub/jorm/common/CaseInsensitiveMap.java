/*
 * @(#)CaseInsensitiveMap.java	2011-8-6
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.common;

import java.util.HashMap;
import java.util.Map;

/**
 * A Map that converts all keys to lowercase Strings for case insensitive lookups.
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: CaseInsensitiveMap.java 217 2011-08-06 14:06:47Z gerald.chen.hz@gmail.com $
 */
public class CaseInsensitiveMap<K, V> extends HashMap<K, V> {

	private static final long serialVersionUID = 1L;

	/**
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key) {
        return super.containsKey(key.toString().toLowerCase());
    }

    /**
     * @see java.util.Map#get(java.lang.Object)
     */
    public V get(Object key) {
        return super.get(key.toString().toLowerCase());
    }

    /**
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
	public V put(K key, V value) {
        return super.put((K)key.toString().toLowerCase(), value);
    }

    /**
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    /**
     * @see java.util.Map#remove(java.lang.Object)
     */
    public V remove(Object key) {
        return super.remove(key.toString().toLowerCase());
    }
}
