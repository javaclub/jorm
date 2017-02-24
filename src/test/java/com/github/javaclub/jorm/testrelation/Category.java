/*
 * @(#)Category.java	2011-7-9
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.testrelation;

import java.util.Date;
import java.util.List;

import com.github.javaclub.jorm.annotation.Column;
import com.github.javaclub.jorm.annotation.Entity;
import com.github.javaclub.jorm.annotation.Id;
import com.github.javaclub.jorm.annotation.PK;
import com.github.javaclub.jorm.annotation.Relation;
import com.github.javaclub.jorm.annotation.constant.RelationType;
import com.github.javaclub.jorm.jdbc.relation.RelatedModel;
import com.github.javaclub.jorm.jdbc.relation.RelationHelper;

/**
 * 目录
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Category.java 395 2011-09-18 11:14:21Z gerald.chen.hz $
 */
@Entity(table = "t_book_category")
@PK(value="id")
@Relation(relation=RelationType.OneToMany, thisField="id", thatField="categoryId", thatClass=Book.class)
public class Category extends RelatedModel {

	@Id
	private int id;
	
	@Column("category_name")
	private String name;
	
	@Column("r_order")
	private int order;
	
	@Column("create_time")
	private Date createTime;
	
	public Category() {
		super();
	}

	public Category(String name) {
		super();
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public List<Book> getBooks(int start, int limit) {
		return RelationHelper.many(this, start, limit);
	}
	
	public List<Book> getAllBooks() {
		return child(Book.class);
	}

	protected Object self() {
		return this;
	}

	public String toString() {
		return "Category [createTime=" + createTime + ", id=" + id + ", name="
				+ name + ", order=" + order + "]";
	}
	
}
