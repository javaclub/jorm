/*
 * @(#)Idcard.java	2011-9-4
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
 * Idcard
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Idcard.java 2011-9-4 下午07:22:24 Exp $
 */
@Entity(table="t_one_to_one_card", lazy=true)
@PK("id")
public class Idcard {
	
	@Id(GenerationType.IDENTITY)
	private long id;
	
	@Column("identity_number")
	private String cardNumber;
	
	@OneToOne(selField="identity", ownerField="cardNumber")
	//@NoColumn
	private Person person;
	
	public Idcard() {
		super();
	}

	public Idcard(String cardNumber) {
		super();
		this.cardNumber = cardNumber;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	@Override
	public String toString() {
		return "Idcard [cardNumber=" + cardNumber + ", id=" + id + "]";
	}
	
}
