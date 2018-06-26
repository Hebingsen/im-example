package com.dchat.response;

import java.io.Serializable;

/**
 * 第壹才团即时通讯聊天记录pojo
 * 
 * @Author:Chosen
 * @CrateTime:2017年3月14日
 */
public class DChatLog implements Serializable {

	private static final long serialVersionUID = 1L;

	private String username;
	private String id;
	private String avatar;
	private Long timestamp;
	private String content;

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

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "ChatLogs [username=" + username + ", id=" + id + ", avatar=" + avatar + ", timestamp=" + timestamp + ", content=" + content + "]";
	}

}
