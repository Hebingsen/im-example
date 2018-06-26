var socket = null;
var rootPath = $("#rootPath").val();
var basePath = $("#basePath").val();

var dchat = {
	loginType : null,
	userId : null,
	socketUrl : "ws://"+basePath+"dchat/websocket/" + this.userId,
	init : function(loginType){
		var url = rootPath+"/dchat/"+loginType+"/login.shtml";
		var param = {};
		if(loginType == "bole"){
			var pwd = $("pwd").val();
			var phone = $("phone").val();
			param = {"pwd":pwd,"phone":phone};
		}
		$.post(url,param,function(result){
			if(result.status != 200){
				alert(result.msg);
				return false;
			}
			//设置属性值
			dchat.userId = result.data.id;
			dchat.loginType = loginType;
			dchat.socketUrl = "ws://"+basePath+"dchat/websocket/" + dchat.userId;
			dchat.openSocket();
		},"json");
	},
	openSocket : function(){
		if("WebSocket" in window) {
			socket = new WebSocket(dchat.socketUrl);
			dchat.useLayIM();
			dchat.startListener();
		} else {
			alert("该浏览器不支持本系统部分功能，请使用谷歌或火狐浏览器！");
		}
	},
	startListener : function(){
		if(socket){
			// 连接发生错误的回调方法
			socket.onerror = function() {
				console.log("连接失败,尝试重新连接!");
				socket = new WebSocket(dchat.socketUrl);
			};

			// 连接成功建立的回调方法
			socket.onopen = function(event) {
				console.log("websocket连接成功!");
			}
			
			// 连接关闭的回调方法
			socket.onclose = function() {
				console.log("关闭连接！!");
				socket.close();
			}

			// 接收到消息的回调方法
			socket.onmessage = function(event) {
				//console.log("收到消息啦:" + event.data);
				var obj = eval("(" + event.data + ")");
				obj.timestamp = parseInt(obj.timestamp);
				window.layim.getMessage(obj);
				dchat.addFriend(obj);
			}
		}
	},
	useLayIM : function(){
		layui.use("layim",function(layim){
			window.layim = layim;
			layim.config({
				brief: false, 
				title: "DChat",
				isgroup: false,
				copyright: true,
				msgbox: "http://www.ccctuan.com", 
				chatLog: rootPath+"/layIM/layui/min/css/modules/layim/html/chatlog.jsp", //聊天记录页面地址，若不开启，剔除该项即可
				//chatLog: rootPath+"/layIM/layui/min/css/modules/layim/html/chatlog.html", //聊天记录页面地址，若不开启，剔除该项即可
				right : "150px",
				init: {
					url: rootPath+"/dchat/init/"+dchat.loginType+"/"+dchat.userId+".shtml"
				},
			});
			
			//监听发送消息
			layim.on("sendMessage", function(data) {
				console.log("监听发送消息事件:");
				//console.log(data);
				if(data.to.type == "friend") {
					layim.setChatStatus("<span style=\"color:#FF5722;\">对方正在输入。。。</span>");
				}
				
				var message = {
					"mine" : {
						"username" : data.mine.username,
						"avatar" : data.mine.avatar,
						"id" : data.mine.id,
						"mine" : true,
						"content" : data.mine.content
					},
					"to" : {
						"username" : data.to.username,
						"id" : data.to.id,
						"status" : data.to.status,
						"sign" : data.to.sign,
						"avatar" : data.to.avatar,
						"name" : data.to.name,
						"type" : data.to.type
					}
				}
				socket.send(JSON.stringify(message)); //发送消息倒Socket服务
			});
			
			//监听签名修改事件
			layim.on("sign",function(sign){
				//console.log("最新个性签名="+sign);
				var signUrl = rootPath+"/dchat/updateSign.shtml";
				var param = {"userId":dchat.userId,"loginType":dchat.loginType,"sign":sign};
				$.post(signUrl,param,function(result){
					if(result.status == 200)
						console.log("个性签名修改成功");
					else
						console.log("个性签名修改失败");
				},"json");
			});
		});
	},
	addFriend : function(obj){
		var check = dchat.existsFriend(obj);
		if(check){
			window.layim.addList({
				type: obj.type,
		        username: obj.username,
	            avatar: obj.avatar,
	            groupid: "9999", 
	            id: obj.id, 
	            sign: obj.sign,
		    });
		}else{
			//console.log("好友已存在");
		}
	},
	existsFriend : function(obj){
		var flag = true;
		//获取所有缓存数据
		var cache = window.layim.cache();
		$.each(cache.friend,function(index,item){
			$.each(cache.friend[index].list,function(idx,itm){
				if(itm.id == obj.id){
					//console.log("好友已存在");
					flag = false;
		        }
			});
		});
		return flag;
	}
};
