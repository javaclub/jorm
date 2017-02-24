/*
 * @(#)RelatedModel.java	2011-8-1
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.jdbc.relation;

import java.util.Collections;
import java.util.List;

import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.JormException;
import com.github.javaclub.jorm.annotation.Relation;
import com.github.javaclub.jorm.annotation.Relations;
import com.github.javaclub.jorm.annotation.constant.RelationType;
import com.github.javaclub.jorm.common.Annotations;
import com.github.javaclub.jorm.common.CommonUtil;
import com.github.javaclub.jorm.jdbc.criterion.Order;
import com.github.javaclub.jorm.jdbc.sql.AnnotationModelHelper;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;

/**
 * RelatedModel
 * 
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: RelatedModel.java 2011-8-1 下午04:20:12 Exp $
 * @deprecated
 */
public abstract class RelatedModel {

	@SuppressWarnings("unchecked")
	public <T> T parent(Class<T> parentClass) {
		Object model = self();
		Relation relationAnn = Annotations.findAnnotation(model.getClass(), Relation.class);
		if(relationAnn != null) {
			if(!relationAnn.thatClass().equals(parentClass)) {
				throw new JormException("The parameter " + parentClass + " can't match the annotation @thatClass " + relationAnn.thatClass());
			}
			if(relationAnn.relation() != RelationType.ManyToOne && relationAnn.relation() != RelationType.OneToOne) {
				throw new JormException("The relation type must be ManyToOne or OneToOne.");
			}
			return (T) parent(model, relationAnn);
		}
		Relations relationsAnn = Annotations.findAnnotation(model.getClass(), Relations.class);
		if(relationsAnn!= null) {
			Relation[] rr = relationsAnn.value();
			for (int i = 0; i < rr.length; i++) {
				int rela = rr[i].relation();
				Class<?> clazz = rr[i].thatClass();
				if((rela == RelationType.ManyToOne || rela == RelationType.OneToOne) && clazz.equals(parentClass)) {
					return (T) parent(model, rr[i]);
				}
			}
		}
		return null;
	}
	
	public <T> List<T> child(Class<T> childClass, Order... orders) {
		return child(childClass, 1, 2000, orders);
	}

	public <T> List<T> child(Class<T> childClass, int start, int limit, Order... orders) {
		Object model = self();
		Class<?> thisClass = model.getClass();
		Relation relationAnn = Annotations.findAnnotation(thisClass, Relation.class);
		if(relationAnn != null) {
			if(!relationAnn.thatClass().equals(childClass)) {
				throw new JormException("The parameter " + childClass + " can't match the annotation @thatClass " + relationAnn.thatClass());
			}
			if(relationAnn.relation() != RelationType.ManyToMany && relationAnn.relation() != RelationType.OneToMany) {
				throw new JormException("The relation type must be ManyToOne or OneToOne.");
			}
			return fetchChild(start, limit, model, relationAnn, orders);
		}
		Relations relationsAnn = Annotations.findAnnotation(model.getClass(), Relations.class);
		if(relationsAnn!= null) {
			Relation[] rr = relationsAnn.value();
			for (int i = 0; i < rr.length; i++) {
				int rela = rr[i].relation();
				if((rela == RelationType.ManyToMany || rela == RelationType.OneToMany) && rr[i].thatClass().equals(childClass)) {
					return fetchChild(start, limit, model, rr[i], orders);
				}
			}
		}
		return Collections.emptyList();
	}

	protected abstract Object self();
	
	@SuppressWarnings("unchecked")
	private Object parent(Object model, Relation relationAnn) {
		String[] thisFieldNames = relationAnn.thisField();
		String[] thatFieldNames = relationAnn.thatField();
		Object[] params = AnnotationModelHelper.getSpecifiedFieldValues(model, thisFieldNames);
		SqlParams sqlParams = RelationHelper.querySql(relationAnn.thatClass(), params, thatFieldNames);
		
		return Jorm.getSession().loadFirst(sqlParams.setObjectClass(relationAnn.thatClass()));
	}
	
	@SuppressWarnings("unchecked")
	private <T> List<T> fetchChild(int start, int limit, Object model,
			Relation relationAnn, Order... orders) {
		Class<?> thatClass = relationAnn.thatClass();
		String[] thisFieldNames = relationAnn.thisField();
		String[] thatFieldNames = relationAnn.thatField();
		Object[] params = AnnotationModelHelper.getSpecifiedFieldValues(model, thisFieldNames);
		SqlParams sqlParams = RelationHelper.querySql(thatClass, params, thatFieldNames);
		if(!CommonUtil.isEmpty(orders)) {
			for (Order order : orders) {
				sqlParams.addOrder(order);
			}
		}
		sqlParams.setObjectClass(thatClass);
		sqlParams.setFirstResult(start).setMaxResults(limit);
		
		return Jorm.getSession().list(sqlParams);
	}
}
