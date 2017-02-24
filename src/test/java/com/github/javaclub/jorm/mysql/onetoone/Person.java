/*
 * @(#)Person.java	2011-9-4
 *
 * Copyright (c) 2011. All Rights Reserved.
 *
 */

package com.github.javaclub.jorm.mysql.onetoone;

import com.github.javaclub.jorm.annotation.Column;
import com.github.javaclub.jorm.annotation.Entity;
import com.github.javaclub.jorm.annotation.Id;
import com.github.javaclub.jorm.annotation.OneToOne;
import com.github.javaclub.jorm.annotation.PK;
import com.github.javaclub.jorm.annotation.constant.GenerationType;

/**
 * Person
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Person.java 2011-9-4 下午07:26:09 Exp $
 */
@Entity(table="t_one_to_one_person")
@PK("identity")
public class Person {
	
	@Id(GenerationType.FOREIGN)
	@Column("identity_number")
	private String identity;

	private String name;
	
	@Column("sex")
	private boolean male;
	
	private String job;
	
	private String telephone;
	
	// SELECT * FROM t_one_to_one_a idcard WHERE idcard.identity_number = Person.identity
	@OneToOne(selField="cardNumber", ownerField="identity")
	private Idcard idcard;
	
	public Person() {
		super();
	}

	public Person(String name, boolean male, String job,
			String telephone) {
		super();
		this.name = name;
		this.male = male;
		this.job = job;
		this.telephone = telephone;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isMale() {
		return male;
	}

	public void setMale(boolean male) {
		this.male = male;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public Idcard getIdcard() {
		return idcard;
	}

	public void setIdcard(Idcard idcard) {
		this.idcard = idcard;
	}

	@Override
	public String toString() {
		return "Person [identity=" + identity + ", job="
				+ job + ", male=" + male + ", name=" + name + ", telephone="
				+ telephone + "]";
	}
	
	
}
