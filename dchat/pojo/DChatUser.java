package com.dchat.pojo;

import java.io.Serializable;
/**
 * 第壹才团即时通讯:聊天用户对象
 * @Author:Chosen
 * @CrateTime:2017年3月10日
 */
public class DChatUser implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String username;// 用户昵称
	private String id;// 我的Id
	private String status;// 在线状态 online：在线、hide：隐身
	private String sign;// 个性签名
	private String avatar;// 头像地址

	
	public DChatUser(String id,String username, String status, String sign, String avatar) {
		super();
		this.username = username;
		this.id = id;
		this.status = status;
		this.sign = sign;
		this.avatar = avatar;
	}

	public DChatUser() {
		super();
		// TODO Auto-generated constructor stub
	}

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

	@Override
	public String toString() {
		return "Mine [username=" + username + ", id=" + id + ", status=" + status + ", sign=" + sign + ", avatar=" + avatar + "]";
	}

}
