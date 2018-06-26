<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
<title>聊天记录</title>

<link rel="stylesheet" href="${pageContext.request.contextPath}/layIM/layui/min/css/layui.css">
<style>
body .layim-chat-main{height: auto;}
</style>
</head>
<body>

<div class="layim-chat-main">
  <ul id="LAY_view"></ul>
</div>

<div id="LAY_page" style="margin: 0 10px;"></div>


<textarea title="消息模版" id="LAY_tpl" style="display:none;">
{{# layui.each(d.data, function(index, item){
  if(item.id == parent.layui.layim.cache().mine.id){ }}
    <li class="layim-chat-mine"><div class="layim-chat-user"><img src="{{ item.avatar }}"><cite><i>{{ layui.data.date(item.timestamp) }}</i>{{ item.username }}</cite></div><div class="layim-chat-text">{{ layui.layim.content(item.content) }}</div></li>
  {{# } else { }}
    <li><div class="layim-chat-user"><img src="{{ item.avatar }}"><cite>{{ item.username }}<i>{{ layui.data.date(item.timestamp) }}</i></cite></div><div class="layim-chat-text">{{ layui.layim.content(item.content) }}</div></li>
  {{# }
}); }}
</textarea>

<!-- 
上述模版采用了 laytpl 语法，不了解的同学可以去看下文档：http://www.layui.com/doc/modules/laytpl.html

-->


<script src="${pageContext.request.contextPath}/layIM/layui/min/layui.js"></script>
<script>
layui.use(['layim', 'laypage'], function(){
	var layim = layui.layim,
  	layer = layui.layer,
  	laytpl = layui.laytpl,
  	$ = layui.jquery,
  	laypage = layui.laypage;
  	
	var param =  location.search, 
  	res = {code: 0 ,msg: '',data : [{}]}
	
	var url = "${pageContext.request.contextPath}/dchat/chatLog.shtml"+param;
	console.log("url="+url);
	$.get(url,function(result){
		console.log(result);
		var html = laytpl(LAY_tpl.value).render({
	    	data: result.data
	  	});
		$('#LAY_view').html(html);
	},"json");
  	
  
});
</script>
</body>
</html>
