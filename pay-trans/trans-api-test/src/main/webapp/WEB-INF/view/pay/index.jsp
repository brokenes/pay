<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="basePath" value="${pageContext.request.contextPath}"/>
<!DOCTYPE HTML>
<html lang="zh-cn">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>会话管理</title>
	<meta charset="utf-8">
	<meta name="renderer" content="webkit|ie-comp|ie-stand">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
	<meta http-equiv="Cache-Control" content="no-siteapp" />
	<link href="../resources/plugins/fullPage/jquery.fullPage.css" rel="stylesheet"/>
	<link rel="stylesheet" type="text/css" href="${basePath}/resources/css/H-ui.min.css" />
	<script type="text/javascript" src="${basePath}/resources/js/jquery.min.js"></script>
	<script type="text/javascript" src="${basePath}/resources/js/scan-pay.js"></script>
	<style type="text/css">
	.ui-sortable .panel-header{ cursor:move}
	</style>
</head>
<body>
	
	<div class="panel panel-default">
				<div class="panel-header">brokenes【扫码】支付测试</div>
				<div class="panel-body">
					<a href="${basePath}/pay" target="_blank">微信扫码-支付</a>
					<a href="${basePath}/orderTracking" target="_blank">微信扫码-订单查询</a>
				</div>
			</div>

</body>

