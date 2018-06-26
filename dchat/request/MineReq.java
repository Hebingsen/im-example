package com.dchat.request;

import java.io.Serializable;

/**
 * 第壹才团即时通讯请求bean:发送方
 * 
 * @Author:Chosen
 * @CrateTime:2017年3月9日
 */
public class MineReq implements Serializable {

	private static final long serialVersionUID = 1L;

	private String username;
	private String avatar;
	private String id;
	private Boolean mine;
	private String content;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getMine() {
		return mine;
	}

	public void setMine(Boolean mine) {
		this.mine = mine;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "MineReq [username=" + username + ", avatar=" + avatar + ", id=" + id + ", mine=" + mine + ", content=" + content + "]";
	}

}
