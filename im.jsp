<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>

	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
		<title>LayIM 3.x PC版本地演示</title>

		<link rel="stylesheet" href="${pageContext.request.contextPath}/layIM/layui/min/css/layui.css">
		<link href="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
		<style>
			html {
				background-color: #333;
			}
		</style>
	</head>

	<body>
		<input type="hidden" id="basePath" value="<%=basePath %>" />
		<input type="hidden" id="rootPath" value="${pageContext.request.contextPath}" />
		
		<input name="pwd" id="pwd" value="123456">
		<input name="phone" id="phone" value="13192736759">
		<button class="btn btn-success" onclick="dchat.init('bole')">伯乐登录</button>
		<button class="btn btn-success" onclick="dchat.init('guest')">游客登录</button>
		<script src="${pageContext.request.contextPath}/layIM/layui/min/layui.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/en/js/jquery-1.8.3.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/cd/index/controller/dchat.js"></script>
	</body>

</html>