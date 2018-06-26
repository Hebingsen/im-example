package com.dchat.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import net.sf.json.JSONObject;

import org.bson.Document;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;

import com.dchat.common.DChatConstans;
import com.dchat.common.MongoUtils;
import com.dchat.pojo.DChatUser;
import com.dchat.request.MineReq;
import com.dchat.request.SendReq;
import com.dchat.request.ToReq;
import com.dchat.response.ToMessage;
import com.lanyuan.util.JsonUtils;
import com.mongodb.client.MongoCollection;


/**
 * 第壹才团即时通讯websocket监听服务端
 * @Author:Chosen
 * @CrateTime:2017年3月8日
 */
@ServerEndpoint(value="/dchat/websocket/{userId}")
public class DChatWebSocket {
	
	public static Map<String,Session> sessionMap = new HashMap<String,Session>();
	
	/**
	 * 连接打开时调用
	 */
	@OnOpen
	public void onOpen(Session session,@PathParam("userId") String userId){
		/**
		 * 1.存储session信息
		 */
		sessionMap.put(userId, session);
		System.out.println("连接打开,session会话保存成功..");
		
		/**
		 * 2.推送离线消息
		 * 2.1.私聊消息
		 * 2.2.群聊消息
		 */
		try {
			List<SendReq> msgList = MessageOperate.getOfflineFirendMessage(userId);
			if(!msgList.isEmpty()){
				for (SendReq sendReq : msgList) {
					ToMessage toMessage = ToMessage.setMessage(sendReq);
					String pushMsg = JsonUtils.objectToJson(toMessage);
					session.getBasicRemote().sendText(pushMsg);
				}
				MessageOperate.unReadToRead(userId,msgList);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		/**
		 * 3.统计在线人数
		 */
	}
	
	
	
	/**
	 * 接受客户端信息时调用
	 * {
	 * 	"mine":
	 * 		{"username":"我是求职者","avatar":"https://ps.ssl.qhimg.com/sdmt/130_135_100/t01962e684e67d33949.jpg","id":"-1","mine":true,"content":"111"},
	 * 	"to":
	 * 		{"username":"自营伯乐3号","id":"3","status":"online","sign":"年度最佳伯乐","avatar":"https://ps.ssl.qhimg.com/sdmt/130_135_100/t01962e684e67d33949.jpg","name":"自营伯乐3号","type":"friend"}
	 * }
	 */
	@OnMessage
	public void onMessage(Session session,String message){
		System.out.println("接受客户端信息时调用:"+message);
		
		if(StringUtils.isEmpty(message))
			throw new RuntimeException("请求消息为空");
		
		SendReq sendReq = JsonUtils.jsonToPojo(message, SendReq.class);
		
		ToMessage toMessage = ToMessage.setMessage(sendReq); 
		String pushMsg = JsonUtils.objectToJson(toMessage);
		System.out.println("pushMsg="+pushMsg);
		
		try {
			String toUserId = sendReq.getTo().getId();
			if(sessionMap.containsKey(toUserId)){
				// 存在则直接推送消息与保存聊天记录
				sessionMap.get(toUserId).getBasicRemote().sendText(pushMsg);
				MessageOperate.saveFriendMessage(sendReq, DChatConstans.MSG_STATUS_READ);
			}else{
				// 保存为离线消息
				MessageOperate.saveFriendMessage(sendReq, DChatConstans.MSG_STATUS_UNREAD);
			}
		} catch (IOException e) {
			throw new RuntimeException("信息发送失败");
		}
	}
	
	/**
	 * 发生错误时调用:注销session信息
	 */
	@OnError
	public void onError(Session session, Throwable error){
		destroySession(session);
		error.printStackTrace();
		System.err.println("连接发生错误");
		
	}
	
	/**
	 * 连接关闭时调用,销毁session
	 */
	@OnClose
	public void onClose(Session session){
		destroySession(session);
		System.err.println("连接关闭");
	}
	
	/**
	 * 连接发生错误或连接关闭时销毁session会话
	 * @param session
	 * @Author:Chosen
	 * @CrateTime:2017年3月9日
	 */
	public void destroySession(Session session){
		if(sessionMap.containsValue(session)){
			Set<Entry<String,Session>> entrySet = sessionMap.entrySet();
			for (Entry<String, Session> entry : entrySet) {
				if(entry.getValue().equals(session)){
					
					boolean removeFlag = true;
					String removeKey = entry.getKey();
					
					//删除静态集合中的伯乐登录信息
					Set<DChatUser> boleList = DChatConstans.boleList;
					if(!boleList.isEmpty()){
						Iterator<DChatUser> it = boleList.iterator();
						while(it.hasNext()){
							DChatUser chatUser = it.next();
							if(chatUser.getId().equals(removeKey)){
								it.remove();
								break;
							}
						}
					}
					
					//删除静态集合中的访客登录信息
					Set<DChatUser> guestList = DChatConstans.guestList;
					if(!guestList.isEmpty() && removeFlag){
						Iterator<DChatUser> it = guestList.iterator();
						while(it.hasNext()){
							DChatUser chatUser = it.next();
							if(chatUser.getId().equals(removeKey)){
								it.remove();
								break;
							}
						}
						
					}
					
					sessionMap.remove(removeKey);
					System.out.println("session已销毁");
					break;
				}
			}
		}
	}

	
	/**
	 * 获取sessionmap
	 * @return
	 * @Author:Chosen
	 * @CrateTime:2017年3月10日
	 */
	public static Map<String, Session> getSessionMap() {
		return sessionMap;
	}
	
	
}
