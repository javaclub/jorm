/*
 * @(#)BookType.java	2011-10-10
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.mysql.manytomany;

import java.util.HashSet;
import java.util.Set;

import com.github.javaclub.jorm.annotation.Column;
import com.github.javaclub.jorm.annotation.Entity;
import com.github.javaclub.jorm.annotation.Id;
import com.github.javaclub.jorm.annotation.ManyToMany;
import com.github.javaclub.jorm.annotation.PK;

/**
 * 同一种类的书可以被多个学生拥有，一个学生可以拥有不同种类的书
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: BookType.java 2011-10-10 下午05:46:38 Exp $
 */
@Entity(table = "t_mtm_book_type", lazy = true)
@PK("id")
public class BookType {

	@Id
	private long id;
	
	@Column("type_name")
	private String typeName;
	
	@Column("release_version")
	private int version;
	
	@ManyToMany(mappedId="id", type=Student.class)
	private Set<Student> owners = new HashSet<Student>();

	public BookType() {
		super();
	}

	public BookType(String typeName, int version) {
		super();
		this.typeName = typeName;
		this.version = version;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Set<Student> getOwners() {
		return owners;
	}

	public void setOwners(Set<Student> owners) {
		this.owners = owners;
	}


}
