package com.dchat.controller;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.bson.BSON;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.dchat.common.DChatConstans;
import com.dchat.common.MongoUtils;
import com.dchat.request.MineReq;
import com.dchat.request.SendReq;
import com.dchat.request.ToReq;
import com.dchat.response.DChatLog;
import com.dchat.response.ToMessage;
import com.lanyuan.util.JsonUtils;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;

/**
 * 第壹才团即时通讯信息操作类
 * 
 * @Author:Chosen
 * @CrateTime:2017年3月9日
 */
@SuppressWarnings("all")
public class MessageOperate {

	private static MongoUtils mongo;
	private final static MongoCollection<Document> readCollection;
	private final static MongoCollection<Document> unreadCollection;
	
	
	static {
		mongo = MongoUtils.getInstance();
		readCollection = mongo.getCollection("dchat", "read");
		unreadCollection = mongo.getCollection("dchat", "unread");
	}
	
	/**
	 * 获取聊天记录
	 * @param mineId 发送方
	 * @param toUserId 接收方
	 * @return
	 * @Author:Chosen
	 * @CrateTime:2017年3月14日
	 */
	public synchronized static List<DChatLog> getDChatLogs(String mineId,String toUserId){
		
		List<DChatLog> chatlogs = new LinkedList<DChatLog>();
		
		//正向
		Document positiveCon = new Document("mine.id",mineId).append("to.id", toUserId);
		
		//反向
		Document reverseCon = new Document("mine.id",toUserId).append("to.id", mineId);
		
		//正向文档集合
		FindIterable<Document> positiveDocs = readCollection.find(positiveCon);
		//遍历添加
		for (Document document : positiveDocs) {
			DChatLog dChatLog = new DChatLog();
			String json = document.toJson();
			SendReq sendReq = JsonUtils.jsonToPojo(json, SendReq.class);
			MineReq mine = sendReq.getMine();
			dChatLog.setAvatar(mine.getAvatar());
			dChatLog.setContent(mine.getContent());
			dChatLog.setId(mine.getId());
			dChatLog.setUsername(mine.getUsername());
			String timestamp = document.getString("timestamp");
			dChatLog.setTimestamp(Long.parseLong(timestamp));
			chatlogs.add(dChatLog);
		}
		
		//反向文档集合
		FindIterable<Document> reverseDocs = readCollection.find(reverseCon);
		//遍历添加
		for (Document document : reverseDocs) {
			DChatLog dChatLog = new DChatLog();
			String json = document.toJson();
			SendReq sendReq = JsonUtils.jsonToPojo(json, SendReq.class);
			MineReq mine = sendReq.getMine();
			dChatLog.setAvatar(mine.getAvatar());
			dChatLog.setContent(mine.getContent());
			dChatLog.setId(mine.getId());
			dChatLog.setUsername(mine.getUsername());
			String timestamp = document.getString("timestamp");
			dChatLog.setTimestamp(Long.parseLong(timestamp));
			chatlogs.add(dChatLog);
		}
		
		/**
		 * 根据时间戳排序
		 */
		Collections.sort(chatlogs, new Comparator<DChatLog>(){
			@Override
			public int compare(DChatLog arg0, DChatLog arg1) {
				return arg0.getTimestamp().compareTo(arg1.getTimestamp());
			}
			
		});
		
		return chatlogs;
	} 
	
	
	/**
	 * 保存聊天记录
	 * 
	 * @param message
	 * @param msgStatus
	 * @Author:Chosen
	 * @CrateTime:2017年3月9日
	 */
	public synchronized static void saveFriendMessage(SendReq sendReq, String msgStatus) {
		Document document = converToDocument(sendReq);
		if (DChatConstans.MSG_STATUS_READ.equals(msgStatus)) {
			readCollection.insertOne(document);
		} else {
			unreadCollection.insertOne(document);
		}
	}
	
	/**
	 * 获取该用户离线未接收消息
	 * @param userId
	 * @Author:Chosen
	 * @CrateTime:2017年3月9日
	 */
	public static List<SendReq> getOfflineFirendMessage(String userId){
		List<SendReq> msgList = new LinkedList<SendReq>();
		FindIterable<Document> documents = unreadCollection.find(new Document("to.id",userId));
		for (Document document : documents) {
			String json = document.toJson();
			SendReq sendReq = JsonUtils.jsonToPojo(json, SendReq.class);
			msgList.add(sendReq);
		}
		return msgList;
	}
	
	/**
	 * 未读消息转换为已读消息
	 * @param msgList 
	 * @param userId 
	 * @Author:Chosen
	 * @CrateTime:2017年3月10日
	 */
	public synchronized static void unReadToRead(final String userId, final List<SendReq> msgList){
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(new Runnable() {
			
			@Override
			public void run() {
				DeleteResult deleteResult = unreadCollection.deleteMany(new Document("to.id",userId));
				System.out.println("成功删除了"+deleteResult.getDeletedCount()+"未读消息");
				LinkedList<Document> documents = new LinkedList<Document>();
				for (SendReq sendReq : msgList) {
					documents.add(converToDocument(sendReq));
				}
				readCollection.insertMany(documents);
			}
		});
	}
	
	/**
	 * 转换为document对象
	 * @param sendReq
	 * @Author:Chosen
	 * @CrateTime:2017年3月10日
	 */
	public static Document converToDocument(SendReq sendReq){
		Document document = new Document().append("mine",converToMap(sendReq.getMine())).append("to", converToMap(sendReq.getTo()));
		Object create_time = sendReq.getCreate_time();
		if(create_time != null){
			document.append("create_time",create_time+"").append("timestamp", MessageOperate.now().getTime()+"");
		}else{
			document.append("create_time", DChatConstans.sdf.format(MessageOperate.now())).append("timestamp", MessageOperate.now().getTime()+"");
		}
		return document;
	}
	
	
	/**
	 * 对象转换成map
	 * @param obj
	 * @return
	 * @Author:Chosen
	 * @CrateTime:2017年3月9日
	 */
	private static Map<String,Object> converToMap(Object obj){
		String json = JsonUtils.objectToJson(obj);
		Map<String,Object> map = JsonUtils.jsonToPojo(json, Map.class);
		return map;
	}
	
	/**
	 * 获取当前时间
	 */
	public static Date now(){
		return new Date();
	}

}
