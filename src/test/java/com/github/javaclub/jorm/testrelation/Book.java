/*
 * @(#)Book.java	2011-7-9
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.testrelation;

import java.util.Date;

import com.github.javaclub.jorm.annotation.Column;
import com.github.javaclub.jorm.annotation.Entity;
import com.github.javaclub.jorm.annotation.Id;
import com.github.javaclub.jorm.annotation.PK;
import com.github.javaclub.jorm.annotation.Relation;
import com.github.javaclub.jorm.annotation.constant.GenerationType;
import com.github.javaclub.jorm.annotation.constant.RelationType;
import com.github.javaclub.jorm.jdbc.relation.RelatedModel;

/**
 * 书籍
 * 
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Book.java 395 2011-09-18 11:14:21Z gerald.chen.hz $
 */
@Entity(table = "t_books")
@PK(value = "id")
@Relation(relation = RelationType.ManyToOne, thisField = "categoryId", thatField = "id", thatClass = Category.class)
public class Book extends RelatedModel {

	@Id(GenerationType.UUID)
	@Column("book_id")
	private String id;

	@Column("book_name")
	private String name;

	@Column("category_id")
	private Integer categoryId;

	@Column("isbn_no")
	private String isbn;

	@Column("pub_time")
	private Date pubTime;

	@Column("create_time")
	private Date createTime;

	@Column("book_author")
	private String author;
	
	public Book() {
		super();
	}

	public Book(String name) {
		super();
		this.name = name;
	}

	public Category getCategory() {
		// return (Category) RelationHelper.one(this);
		return parent(Category.class);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public Date getPubTime() {
		return pubTime;
	}

	public void setPubTime(Date pubTime) {
		this.pubTime = pubTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String toString() {
		return "Book [author=" + author + ", categoryId=" + categoryId
				+ ", createTime=" + createTime + ", id=" + id + ", isbn="
				+ isbn + ", name=" + name + ", pubTime=" + pubTime + "]";
	}

	protected Object self() {
		return this;
	}

}
