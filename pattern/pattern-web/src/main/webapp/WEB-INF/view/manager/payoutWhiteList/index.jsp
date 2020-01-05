﻿<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="basePath" value="${pageContext.request.contextPath}"/>
<div id="payoutWhiteListDialog" class="crudDialog">
	<div id="payoutToolbar">
		<shiro:hasPermission name="pattern:payoutWhiteList:create">
			<a class="waves-effect waves-button" href="javascript:;" onclick="createPayoutWhiteListAction()"><i class="zmdi zmdi-plus"></i> 新增</a>
		</shiro:hasPermission>
	</div>
	<table id="payoutTable"></table>
</div>
<script>
var $tablePayoutWhiteList = $('#payoutTable');
function init() {
	// bootstrap table初始化
	$tablePayoutWhiteList.bootstrapTable({
		url: '${basePath}/payoutWhiteList/list/${mainAgentId}',
		height: getHeight()-200,
		cache: false,
		striped: true,
		search: false,
		showRefresh: false,
		showColumns: false,
		minimumCountColumns: 2,
		clickToSelect: false,
		pagination: true,
		paginationLoop: false,
		sidePagination: 'server',
		silentSort: false,
		smartDisplay: false,
		escape: true,
		searchOnEnterKey: false,
		idField: 'payoutWhiteListId',
		maintainSelected: true,
		detailView: false,
		toolbar: '#payoutToolbar',
		columns: [
			{field:'payoutWhiteListId', visible:false},
			{field:'agentId',visible:false},
			{field:'addrType',title:'地址类型',width:'10%',align:'center',formatter: function (value, row, index) { 
				if(value==0){
					return "域名";
				}else if(value==1){
					return "IPv4";
				}else if(value==2){
					return "IPv6";
				}else{
					return "";
				}
			}},
			{field:'address',title:'地址',width:'23%',align:'center'},
			{field:'remark',title:'备注',width:'23%',align:'center'},
			{field:'status',title:'状态',width:'9%',align:'center',formatter: function (value, row, index) { 
				if(value==0){
					return "未生效";
				}else if(value==1){
					return "生效";
				}else{
					return "";
				}
			}},
			{field:'createTime',title:'创建时间',width:'20%',align:'center'},
			{field:'action',align:'center', title: '操作',width:'15%',align: 'center', formatter: 'actionPayoutWhiteListFormatter', events: 'actionEvents', clickToSelect: false}
		]
	});
}




// 格式化操作按钮
function actionPayoutWhiteListFormatter(value, row, index) {
    return [
		'<shiro:hasPermission name="pattern:payoutWhiteList:update"><button type="button" class="btn btn-info btn-sm" style="margin-right:10px;padding:0 10px;" onclick="updatePayoutWhiteListRow('+row.payoutWhiteListId+')">修改</button></shiro:hasPermission>',
		'<shiro:hasPermission name="pattern:payoutWhiteList:delete"><button type="button" class="btn btn-danger btn-sm" style="padding:0 10px;" onclick="deletePayoutWhiteListRow('+row.payoutWhiteListId+',null)">删除</button></shiro:hasPermission>'
    ].join('');
}
//新增
var createPayoutWhiteListDialog;
// 编辑
var updatePayoutWhiteListDialog;
// 删除
var deletePayoutWhiteListDialog;
function createPayoutWhiteListAction() {
	createPayoutWhiteListDialog = $.dialog({
		animationSpeed: 300,
        columnClass: 'col-md-6 col-md-offset-1 col-sm-3 col-sm-offset-3 col-xs-6 col-xs-offset-1',
		title: '新增商户代付白名单',
		content: 'url:${basePath}/payoutWhiteList/create/${mainAgentId}',
		onContentReady: function () {
			initMaterialInput();
		}
	});
}

//编辑行
function updatePayoutWhiteListRow(id){
    updatePayoutWhiteListDialog = $.dialog({
        animationSpeed: 300,
        title: '编辑商户代付白名单',
        columnClass: 'col-md-6 col-md-offset-1 col-sm-3 col-sm-offset-3 col-xs-6 col-xs-offset-1',
        content: 'url:${basePath}/payoutWhiteList/update/' + id,
        onContentReady: function () {
			initMaterialInput();
        }
    });
}
//删除行
function deletePayoutWhiteListRow(id,rows) {
    deletePayoutWhiteListDialog = $.confirm({
        type: 'red',
        animationSpeed: 300,
        title: false,
        content: '确认删除该商户代付白名单吗？',
        buttons: {
            confirm: {
                text: '确认',
                btnClass: 'waves-effect waves-button',
                action: function () {
                    var ids = new Array();
                    if(id){
                        ids.push(id);
                    }
                    if(rows){
                    	for (var i in rows) {
							ids.push(rows[i].payoutWhiteListId);
						}
                    }
                    $.ajax({
                        type: 'get',
                        url: '${basePath}/payoutWhiteList/delete/' + ids.join("-"),
                        success: function(result) {
                        	if (!jQuery.isEmptyObject(result.errorList)) {
                        		if (result.errorList instanceof Array) {
                                    $.each(result.errorList, function(index, value) {
                                    	layer.alert(value,{icon:2})
                                    });
                                } else {
                                    layer.alert(result.errorMsg,{icon:2})
                				}
                            } else {
                            	deletePayoutWhiteListDialog.close();
                                $tablePayoutWhiteList.bootstrapTable('refresh');
                            }
                        },
                        error: function(XMLHttpRequest, textStatus, errorThrown) {
                            layer.alert(textStatus,{icon:2})
                        }
                    });
                }
            },
            cancel: {
                text: '取消',
                btnClass: 'waves-effect waves-button'
            }
        }
    });
}



</script>