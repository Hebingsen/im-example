package com.dchat.common;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import com.dchat.pojo.DChatUser;

/**
 * 第壹才团即时通讯常量类
 * @Author:Chosen
 * @CrateTime:2017年3月9日
 */
public class DChatConstans {
	
	/**在线**/
	public static final String STATUS_ONLINE = "online";
	
	/**隐身离线**/
	public static final String STATUS_HIDE = "hide";
	
	/**当前聊天类型**/
	public static final String TYPE_FRIEND = "friend";
	
	/**消息类型:已读**/
	public static final String MSG_STATUS_READ = "1";
	
	/**消息类型:未读**/
	public static final String MSG_STATUS_UNREAD = "-1";
	
	/**一致的日期格式**/
	public static SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");   
	
	/**登录类型:伯乐**/
	public static final String LOGIN_TYPE_BOLE = "bole";
	/**登录类型:游客**/
	public static final String LOGIN_TYPE_GUEST = "guest";
	
	/**记录当前登录的游客**/
	public static Set<DChatUser> guestList = new HashSet<DChatUser>();
	
	/**记录当前登录的伯乐**/
	public static Set<DChatUser> boleList = new HashSet<DChatUser>();
	
	
	
	
}
