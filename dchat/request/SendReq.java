package com.dchat.request;

import java.io.Serializable;

/**
 * 第壹才团即时通讯发送消息时的请求对象
 * 
 * @Author:Chosen
 * @CrateTime:2017年3月9日
 */
public class SendReq implements Serializable {

	private static final long serialVersionUID = 1L;
	private Object _id;
	private MineReq mine;
	private ToReq to;
	private Object create_time;
	private String timestamp;

	public Object get_id() {
		return _id;
	}

	public void set_id(Object _id) {
		this._id = _id;
	}

	public MineReq getMine() {
		return mine;
	}

	public void setMine(MineReq mine) {
		this.mine = mine;
	}

	public ToReq getTo() {
		return to;
	}

	public void setTo(ToReq to) {
		this.to = to;
	}

	public Object getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Object create_time) {
		this.create_time = create_time;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "SendReq [_id=" + _id + ", mine=" + mine + ", to=" + to + ", create_time=" + create_time + ", timestamp=" + timestamp + "]";
	}

}
