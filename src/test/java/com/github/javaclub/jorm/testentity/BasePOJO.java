package com.github.javaclub.jorm.testentity;

import java.util.Date;

import com.github.javaclub.jorm.annotation.Id;

/**
 * 所有POJO的基类，包含主键，创建时间，更新时间等字段
 * 
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: BasePOJO.java 105 2011-07-13 04:02:32Z gerald.chen.hz@gmail.com $
 */
public abstract class BasePOJO {
	
	@Id
	private Integer id;
	private Date createTime;
	private Date updateTime;
	private String createUser;
	private String updateUser;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

}
