package com.dchat.response;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.dchat.common.DChatConstans;
import com.dchat.controller.MessageOperate;
import com.dchat.request.MineReq;
import com.dchat.request.SendReq;
import com.dchat.request.ToReq;

/**
 * 第壹才团即时通讯响应信息
 * 
 * @Author:Chosen
 * @CrateTime:2017年3月9日
 */
public class ToMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	private String username;
	private String avatar;
	private String id;
	private String type;
	private String content;
	private String time;
	private String timestamp;
	private Boolean mine;
	
	/**
	 * 构造消息体
	 */
	public static ToMessage setMessage(SendReq sendReq){
		
		MineReq mine = sendReq.getMine();
		System.out.println("mine = " + mine);
		if(mine ==  null)
			throw new RuntimeException("发送方用户信息为空");
		
		ToReq to = sendReq.getTo();
		System.out.println("to = " + to);
		if(to == null)
			throw new RuntimeException("接收方用户信息为空");
		
		try {
			Object time = sendReq.getCreate_time();
			String timestamp = sendReq.getTimestamp();
			ToMessage toMessage = new ToMessage(mine.getUsername(),mine.getAvatar(),to.getType(),mine.getContent(),time,timestamp);
			if(DChatConstans.TYPE_FRIEND.equals(to.getType())){
				toMessage.setId(mine.getId());
			}else{
				toMessage.setId(to.getId());
			}
			return toMessage;
		} catch (Exception e) {
			throw new RuntimeException("消息体构造失败:"+e.getMessage());
		}
		
	}
	
	/**
	 * 构造函数
	 */
	public ToMessage(String username, String avatar,String type, String content,Object time,String timestamp) {
		this.username = username;
		this.avatar = avatar;
		this.type = type;
		this.content = content;
		this.mine = false;
		
		if(time == null){
			this.time = DChatConstans.sdf.format(MessageOperate.now()).toString();
			this.timestamp = MessageOperate.now().getTime()+"";
		}else{
			this.time = String.valueOf(time);
			this.timestamp = timestamp;
		}
	}

	public ToMessage() {
		super();
	}

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}


	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public Boolean getMine() {
		return mine;
	}

	public void setMine(Boolean mine) {
		this.mine = mine;
	}

	@Override
	public String toString() {
		return "ToMessage [username=" + username + ", avatar=" + avatar + ", id=" + id + ", type=" + type + ", content=" + content + ", time=" + time + ", timestamp=" + timestamp + ", mine=" + mine
				+ "]";
	}

}
