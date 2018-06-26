package com.dchat.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dchat.common.DChatConstans;
import com.dchat.pojo.DChatUser;
import com.dchat.pojo.DGroup;
import com.dchat.response.DChatLog;
import com.development.response.ResponseResult;
import com.dw.en.service.EnterpriseService;
import com.dw.zp.controller.Zp_Util_Controller;
import com.dw.zp.formmap.Enterprise_FormMap;
import com.dw.zp.formmap.Recruit_user_FormMap;
import com.lanyuan.controller.index.BaseController;

/**
 * 第壹才团即时通讯:数据操作controller
 * @Author:Chosen
 * @CrateTime:2017年3月6日
 */
@Controller
@RequestMapping("/dchat")
@SuppressWarnings("all")
public class DchatController extends BaseController {
	
	
	@Autowired
	private EnterpriseService enterpriseService;
	
	/**
	 * 第壹才团即时聊天登录入口
	 * @loginType 登录类型:bole-伯乐登录,guest-访客登录
	 * @Author:Chosen
	 * @CrateTime:2017年3月10日
	 */
	@RequestMapping("/{loginType}/login")
	@ResponseBody
	public Object loginByBole(@PathVariable String loginType,String phone,String pwd,HttpServletRequest request){
		try {
			HttpSession httpSession = request.getSession();
			
			if(DChatConstans.LOGIN_TYPE_BOLE.equals(loginType)){
				//获取当前登录的伯乐信息
				Recruit_user_FormMap reuser = Zp_Util_Controller.getlogin(request);
				Object boleId = reuser.get("usercId");
				if(boleId == null)
					return ResponseResult.build(404, "请先登录伯乐账号");
				
				Enterprise_FormMap bole = enterpriseService.findById(boleId);
				if(bole == null)
					return ResponseResult.build(403, "请先注册伯乐账号");
				
				//将伯乐信息存储到session中
				httpSession.setAttribute("bole_"+bole.getInt("id"), bole);
				bole.put("loginType", loginType);
				return ResponseResult.build(200, "登录成功",bole);
			}else if(DChatConstans.LOGIN_TYPE_GUEST.equals(loginType)){
				String sessionId = httpSession.getId();
				System.out.println("用户登录的sessionId为="+sessionId);
				Map<String, Object> guest = (Map<String, Object>) httpSession.getAttribute("guest_"+sessionId);
				if(guest  == null){
					guest = new HashMap<String,Object>();
					guest.put("loginType", loginType);
					guest.put("id", sessionId);
					guest.put("sign", "欢迎来到第壹才团");
					guest.put("userName", "访客" + new Random().nextInt(1000) + "号");
					httpSession.setAttribute("guest_"+sessionId, guest);
				}
				return ResponseResult.build(200,"访客登录成功",guest);
			}else{
				return ResponseResult.build(500, "非法无效登录");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseResult.build(500, "内部服务器错误");
		}
		
	}
	
	@RequestMapping("/init/{loginType}/{userId}")
	@ResponseBody
	public Object init(@PathVariable String loginType,@PathVariable String userId,HttpServletRequest request) throws Exception {
		
		try {
			DChatUser chatUser = null;
			List<DGroup> groups = new ArrayList<DGroup>();
			HttpSession httpSession = request.getSession();
			
			if(DChatConstans.LOGIN_TYPE_BOLE.equals(loginType)){
				Enterprise_FormMap bole = (Enterprise_FormMap) request.getSession().getAttribute("bole_"+userId);
				chatUser = new DChatUser(bole.getInt("id")+"",bole.getStr("userName"),"online",bole.getStr("sign"),"https://ps.ssl.qhimg.com/t01f7251b5a36d0fc82.jpg");
				
				//好友分组列表
				DGroup group = new DGroup(9999,"当前访客",DChatConstans.guestList);
				groups.add(group);
				
				//记录当前登录在线的伯乐
				DChatConstans.boleList.add(chatUser);
			}else if(DChatConstans.LOGIN_TYPE_GUEST.equals(loginType)){
				Map<String, Object> guest = (Map<String, Object>) httpSession.getAttribute("guest_"+userId);
				chatUser = new DChatUser(String.valueOf(guest.get("id")),String.valueOf(guest.get("userName")),"online",String.valueOf(guest.get("sign")),"https://ps.ssl.qhimg.com/t01f7251b5a36d0fc82.jpg");
				
				//好友分组列表
				DGroup group = new DGroup(9999,"第壹才团自营伯乐",DChatConstans.boleList);
				groups.add(group);
				
				//记录当前登录在线的访客
				DChatConstans.guestList.add(chatUser);
			}
			
			/**
			 * 返回聊天数据
			 */
			Map<String,Object> chatData = new HashMap<String,Object>();
			chatData.put("mine", chatUser);
			chatData.put("friend", groups);
			
			Map<String,Object> retMap = new HashMap<String,Object>();
			retMap.put("code", 0);
			retMap.put("msg", "");
			retMap.put("data", chatData);
			return retMap;
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseResult.build(500, "内部服务器错误");
		}
	}
	
	@RequestMapping("/updateSign")
	@ResponseBody
	public Object updateSign(String userId,String loginType,String sign,HttpServletRequest request){
		
		try {
			HttpSession httpSession = request.getSession();
			
			if(DChatConstans.LOGIN_TYPE_BOLE.equals(loginType)){
				Enterprise_FormMap bole = enterpriseService.findById(userId);
				bole.set("sign", sign);
				ResponseResult result = enterpriseService.update(bole);
				if(result.getStatus() == 200){
					DChatUser chatUser = new DChatUser(bole.getInt("id")+"",bole.getStr("userName"),"online",bole.getStr("sign"),"https://ps.ssl.qhimg.com/t01f7251b5a36d0fc82.jpg");
					remove(DChatConstans.boleList,userId);
					DChatConstans.boleList.add(chatUser);
				}
				
				return result;
			}else if(DChatConstans.LOGIN_TYPE_GUEST.equals(loginType)){
				//修改session里的信息
				Map<String, Object> guest = (Map<String, Object>) httpSession.getAttribute("guest_"+userId);
				guest.put("sign", sign);
				httpSession.setAttribute("guest_"+userId,guest);
				
				//修改在线用户中的信息
				DChatUser chatUser = new DChatUser(String.valueOf(guest.get("id")),String.valueOf(guest.get("userName")),"online",String.valueOf(guest.get("sign")),"https://ps.ssl.qhimg.com/t01f7251b5a36d0fc82.jpg");
				remove(DChatConstans.guestList,userId);
				DChatConstans.guestList.add(chatUser);
			}
			
			return ResponseResult.ok();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseResult.build(500, "内部服务器错误");
		}
	}
	
	/**
	 * 
	 * @param id 对话窗口:目标用户Id
	 * @param type 会话类型:friend
	 * @return
	 * @Author:Chosen
	 * @CrateTime:2017年3月14日
	 */
	@RequestMapping("/chatLog")
	@ResponseBody
	public Object chatLog(String id,String type,HttpServletRequest request){
		try {
			if(StringUtils.isEmpty(id)){
				return new HashMap<String,Object>(){{
					put("msg", "参数为空");
					put("code", -1);
					put("data", null);
					
				}};
			}
			
			/**
			 * 根据id判断,如果id属于sessionId字符串,则发起查看聊天记录的人是伯乐,否而则是访客
			 */
			List<DChatLog> dChatLogs = Collections.EMPTY_LIST;
			try {
				Integer toUserId = Integer.parseInt(id);
				String sessionId = request.getSession().getId();
				dChatLogs = MessageOperate.getDChatLogs(sessionId.toString(), id);
			} catch (NumberFormatException e) {
				//获取当前登录的伯乐id作为mineId
				Recruit_user_FormMap reuser = Zp_Util_Controller.getlogin(request);
				Object boleId = reuser.get("usercId");
				dChatLogs = MessageOperate.getDChatLogs(boleId.toString(), id);
			}
			
			Map<String, Object> retMap = new HashMap<String, Object>();
			retMap.put("msg", "");
			retMap.put("code", 0);
			retMap.put("data", dChatLogs);
			return retMap;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * 根据条件遍历删除集合中的元素
	 * @param set
	 * @param removeKey
	 * @Author:Chosen
	 * @CrateTime:2017年3月13日
	 */
	public static void remove(Set<DChatUser> set,String removeKey){
		//删除
		Iterator<DChatUser> it = set.iterator();
		while (it.hasNext()) {
			DChatUser next = it.next();
			String id = next.getId();
			if(removeKey.equals(id))
				it.remove();
		}
	}
	
	@RequestMapping("/clear")
	@ResponseBody
	public Object clear(){
		try {
			Map<String, Session> sessionMap = DChatWebSocket.getSessionMap();
			Set<Entry<String,Session>> entrySet = sessionMap.entrySet();
			for (Entry<String, Session> entry : entrySet) {
				System.out.println("session key = " + entry.getKey() + ",session value = " + entry.getValue());
				sessionMap.remove(entry.getKey());
				System.out.println("session已销毁");
			}
			return ResponseResult.ok();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseResult.build(500, "内部服务器错误");
		}
	}
	
	
}