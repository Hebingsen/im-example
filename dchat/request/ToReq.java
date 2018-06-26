package com.dchat.request;

import java.io.Serializable;

/**
 * 第壹才团即时通讯请求对象:接收方
 * 
 * @Author:Chosen
 * @CrateTime:2017年3月9日
 */
public class ToReq implements Serializable {

	private static final long serialVersionUID = 1L;

	private String username;
	private String id;
	private String status;
	private String sign;
	private String avatar;
	private String name;
	private String type;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ToReq [username=" + username + ", id=" + id + ", status=" + status + ", sign=" + sign + ", avatar=" + avatar + ", name=" + name + ", type=" + type + "]";
	}

}
