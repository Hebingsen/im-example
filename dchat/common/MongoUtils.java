package com.dchat.common;

import java.util.List;
import org.bson.Document;
import com.dchat.request.SendReq;
import com.lanyuan.util.JsonUtils;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;

/**
 * mongodb操作工具类
 * @Author:Chosen
 * @CrateTime:2017年3月10日
 */
public final class MongoUtils {

	static String HOST = "120.24.0.24";
	static int PORT = 27017;
	public static String DATABASE = "chat";
	private static MongoClient mongo = null;
	private static final MongoUtils instance = new MongoUtils();

	public static void main(String[] args) {

		MongoCollection<Document> collection = instance.getCollection("dchat", "unread");
		FindIterable<Document> msgs = collection.find(new Document("to.id","2"));
		for (Document document : msgs) {
			System.err.println("=========================="+document);
			String json = document.toJson();
			SendReq sendReq = JsonUtils.jsonToPojo(json, SendReq.class);
			System.out.println(sendReq);
		}
		
		DeleteResult deleteResult = collection.deleteMany(new Document("to.id","2"));
		long deletedCount = deleteResult.getDeletedCount();
		System.out.println("deletedCount="+deletedCount);

	}

	/**
	 * 单例
	 */
	public static MongoUtils getInstance() {
		return instance;
	}

	/**
	 * 初始化MongoDB
	 */
	public static void createMongoClient() {
		try {
			MongoClientOptions.Builder build = MongoClientOptions.builder();
			build.connectionsPerHost(100); // 与目标数据库能够建立的最大connection数量为100
			build.threadsAllowedToBlockForConnectionMultiplier(50); // 如果当前所有的connection都在使用中，则每个connection上可以有50个线程排队等待
			build.maxWaitTime(1000 * 60 * 2);
			build.connectTimeout(1000 * 60 * 1); // 与数据库建立连接的timeout设置为1分钟
			MongoClientOptions myOptions = build.build();
			// 下面的server可以是一个list，主要用于mongos集群
			ServerAddress server = new ServerAddress(HOST, PORT);
			mongo = new MongoClient(server, myOptions);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("MongoDB连接失败！");
		}
	}

	public static void closeConnection() {
		if (mongo != null) {
			mongo.close();
		}
	}

	/**
	 * 获取MongoDatabase对象
	 */
	public MongoDatabase getDataBase(String dbName) {
		try {
			if (mongo == null) {
				createMongoClient();
			}
			return mongo.getDatabase(dbName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取集合对象
	 * 
	 * @param dbName
	 *            tableName
	 * @return
	 */
	public MongoCollection<Document> getCollection(String dbName, String tableName) {
		try {
			return getDataBase(dbName).getCollection(tableName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 单个Document插入
	public void insertOne(String dbName, String tableName, Document doc) {
		try {
			getCollection(dbName, tableName).insertOne(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 多个Document插入
	public void insertMany(String dbName, String tableName, List<Document> list) {
		try {
			getCollection(dbName, tableName).insertMany(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
