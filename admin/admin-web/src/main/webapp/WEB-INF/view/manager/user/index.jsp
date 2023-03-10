<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="basePath" value="${pageContext.request.contextPath}"/>
<!DOCTYPE HTML>
<html lang="zh-cn">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>用户管理</title>
	<jsp:include page="../../common/inc/head.jsp" flush="true"/>
</head>
<body>
<div id="main">
	
	<div class="panel panel-default">
    <form class="form-horizontal">
        <div class="panel-body">
            <div class="row pd-bottom-2">
                <div class="col-md-4">
                    <div class="form-group">
                        <label class="col-md-4 control-label text-right">用户名称：</label>
                        <div class="col-md-8">
                            <input class="form-control"  id="userName">
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                        <label class="col-md-4 control-label text-right">用户组织：</label>
                        <div class="col-md-8">
                            <input class="form-control"  id="organizationName">
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                        <label class="col-md-4 control-label">用户角色：</label>
                        <div class="col-md-8">
                            <select id ="queryRole" class="selectpicker" style="width: 150px">
								<option value="0">请选择</option>
							</select>
                        </div>
                    </div>
                </div>
            </div>
                <!-- 
                <div class="col-md-12 text-right ">
                    <div params="vm.searchParams" class="ng-isolate-scope">
						<a class="btn btn-success" onclick="query()"><span class="glyphicon glyphicon-search"></span>搜索</button>
						<a onclick="reset()" class="btn btn-danger pull-right">重置</a>
					</div>
                </div>
                 -->
            </div>
        </form>
     </div>

	<div id="toolbar" >
		<shiro:hasPermission name="admin:user:create">
			<a class="waves-effect waves-button" href="javascript:;" onclick="createAction()"><i class="zmdi zmdi-plus"></i> 新增用户</a>
		</shiro:hasPermission>
		<shiro:hasPermission name="admin:user:update">
			<a class="waves-effect waves-button" href="javascript:;" onclick="updateAction()"><i class="zmdi zmdi-edit"></i> 编辑用户</a>
		</shiro:hasPermission>
		<shiro:hasPermission name="admin:password:update">
			<a class="waves-effect waves-button" href="javascript:;" onclick="passwordAction()"><i class="zmdi zmdi-edit"></i> 修改密码</a>
		</shiro:hasPermission>
		<shiro:hasPermission name="admin:user:delete">
			<a class="waves-effect waves-button" href="javascript:;" onclick="deleteAction()"><i class="zmdi zmdi-close"></i> 删除用户</a>
		</shiro:hasPermission>
		<shiro:hasPermission name="admin:user:organization">
			<a class="waves-effect waves-button" href="javascript:;" onclick="organizationAction()"><i class="zmdi zmdi-accounts-list"></i> 用户组织</a>
		</shiro:hasPermission>
		<shiro:hasPermission name="admin:user:role">
			<a class="waves-effect waves-button" href="javascript:;" onclick="roleAction()"><i class="zmdi zmdi-accounts"></i> 用户角色</a>
		</shiro:hasPermission>
		<shiro:hasPermission name="admin:user:permission">
			<a class="waves-effect waves-button" href="javascript:;" onclick="permissionAction()"><i class="zmdi zmdi-key"></i> 用户权限</a>
		</shiro:hasPermission>
        
	</div>
	<table id="table"></table>
</div>

<jsp:include page="../../common/inc/footer.jsp" flush="true"/>
<script type="text/javascript">
    var $table = $('#table');
    $(function() {
        // bootstrap table初始化
        $table.bootstrapTable({
            url: '${basePath}/manage/user/list',
            height: getHeight(),
            method:'post',
    		dataType:'json',
            striped: true,
            // search: true,
            showRefresh: true,
            // showColumns: true,
            minimumCountColumns: 2,
            clickToSelect: true,
            // detailView: true,
            detailFormatter: 'detailFormatter',
            pagination: true,
            paginationLoop: false,
            sidePagination: 'server',
            silentSort: false,
            smartDisplay: false,
            escape: true,
            searchOnEnterKey: true,
            idField: 'userId',
            maintainSelected: true,
            toolbar: '#toolbar',
            queryParams: queryParams,
            responseHandler:function(result){
    			if(result.code == '10110'){
                	layer.msg(result.msg);
                    location:top.location.href = '${basePath}/login';
                }
    			return{                            //return bootstrap-table能处理的数据格式
    		        "total":result.total,
    		        "rows":result.rows
    		    }
    		},
            columns: [
                {field: 'ck', checkbox: true},
                {field: 'userId', title: '编号', sortable: true, align: 'center'},
                {field: 'userName', title: '帐号', align: 'center'},
                {field: 'realName', title: '姓名', align: 'center'},
                {field: 'organizationName', title: '组织', align: 'center'},
                {field: 'roleName', title: '角色', align: 'center'},
                {field: 'avatar', title: '头像', align: 'center', formatter: 'avatarFormatter'},
                {field: 'phone', title: '电话', align: 'center'},
                {field: 'email', title: '邮箱', align: 'center'},
                {field: 'sex', title: '性别', formatter: 'sexFormatter', align: 'center'},
                {field: 'userType', title: '类型', formatter: 'userTypeFormatter', align: 'center'},
                {field: 'locked', title: '状态', sortable: true, align: 'center', formatter: 'lockedFormatter', align: 'center'},
                {field: 'action', title: '操作', align: 'center', formatter: 'actionFormatter', events: 'actionEvents', clickToSelect: false}
            ]
        });
    });
    // 格式化操作按钮
    function actionFormatter(value, row, index) {
        return [
            '<shiro:hasPermission name="admin:user:update"><a class="update" href="javascript:;" onclick="updateRow('+row.userId+')" data-toggle="tooltip" title="Edit"><i class="glyphicon glyphicon-edit"></i></a></shiro:hasPermission>　',
            '<shiro:hasPermission name="admin:user:delete"><a class="delete" href="javascript:;" onclick="deleteRow('+row.userId+')" data-toggle="tooltip" title="Remove"><i class="glyphicon glyphicon-remove"></i></a></shiro:hasPermission>'
        ].join('');
    }

    /**
     * 查询
     */
    function query(){
        $("#table").bootstrapTable('refresh');
    }

    $.ajax({
        type:"GET",
        url:'${basePath}/manage/role/all',
        success:function(result){
            for(var i=0;i<result.length;i++){
                var option = $("<option value="+ result[i].roleId +">" + result[i].name +"</option>");
                $("#queryRole").append(option);
            }
        }
    });

    /**
     * 重置
     */
    function reset(){
        $('#userName').val("");
        $('#organizationName').val('');
        $('#queryRole').val('0');//设置选中
        $(".selectpicker").find("option[text='请选择']").attr("selected",true);
    }

    //分页查询参数，是以键值对的形式设置的
    function queryParams(params) {
        return {
            userName: $('#userName').val().trim(),
            organizationName: $('#organizationName').val().trim(),
            limit: params.limit, // 每页显示数量
            offset: parseInt(params.offset),
            // offset: parseInt(params.offset/params.limit) + 1,
            roleId: $('#queryRole').val()==0 ? null : $('#queryRole').val(),
        }
    }

    //编辑行
    function updateRow(userId){
        updateDialog = $.dialog({
            animationSpeed: 300,
            title: '编辑用户',
            columnClass: 'col-md-10 col-md-offset-1 col-sm-6 col-sm-offset-3 col-xs-10 col-xs-offset-1',
            content: 'url:${basePath}/manage/user/update/'+userId,
            onContentReady:function(){
                initMaterialInput();
                //initTree();
            },
            contentLoaded: function(data, status, xhr){
                if(data.code == '10110'){
                	layer.msg(data.msg);
                    location:top.location.href = '${basePath}/login';
                }
            }
        });
    }
    //删除行
    function deleteRow(userId) {
        deleteDialog = $.confirm({
            type: 'red',
            animationSpeed: 300,
            title: false,
            content: '确认删除该用户吗？',
            buttons: {
                confirm: {
                    text: '确认',
                    btnClass: 'waves-effect waves-button',
                    action: function () {
                        var ids = new Array();
                        ids.push(userId);
                        $.ajax({
                            type: 'get',
                            url: '${basePath}/manage/user/delete/' + ids.join("-"),
                            success: function(result) {
                                if (result.code != 1) {
                                    if (result.data instanceof Array) {
                                        $.each(result.data, function(index, value) {
                                            $.confirm({
                                                theme: 'dark',
                                                animation: 'rotateX',
                                                closeAnimation: 'rotateX',
                                                title: false,
                                                content: value.errorMsg,
                                                buttons: {
                                                    confirm: {
                                                        text: '确认',
                                                        btnClass: 'waves-effect waves-button waves-light'
                                                    }
                                                }
                                            });
                                        });
                                    }else if(result.code == '10110'){
                                    	layer.msg(result.msg);
                                        location:top.location.href = '${basePath}/login';
                                    }else {
                                        $.confirm({
                                            theme: 'dark',
                                            animation: 'rotateX',
                                            closeAnimation: 'rotateX',
                                            title: false,
                                            content: result.msg,
                                            buttons: {
                                                confirm: {
                                                    text: '确认',
                                                    btnClass: 'waves-effect waves-button waves-light',
                                                    location:top.location.href = location.href
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    deleteDialog.close();
                                    $table.bootstrapTable('refresh');
                                }
                            },
                            error: function(XMLHttpRequest, textStatus, errorThrown) {
                                $.confirm({
                                    theme: 'dark',
                                    animation: 'rotateX',
                                    closeAnimation: 'rotateX',
                                    title: false,
                                    content: textStatus,
                                    buttons: {
                                        confirm: {
                                            text: '确认',
                                            btnClass: 'waves-effect waves-button waves-light'
                                        }
                                    }
                                });
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
    // 格式化图标
    function avatarFormatter(value, row, index) {
        return '<img src='+ value +' style="width:20px;height:20px;"/>';
    }
    // 格式化性别
    function sexFormatter(value, row, index) {
        if (value == 1) {
            return '男';
        }
        if (value == 2) {
            return '女';
        }
        return '-';
    }
    // 格式化用户类型
    function userTypeFormatter(value, row, index) {
        if (value == 0) {
            return '管理员';
        }
        if (value == 1) {
            return '普通用户';
        }
        return '-';
    }
    // 格式化状态
    function lockedFormatter(value, row, index) {
        if (value == 1) {
            return '<span class="label label-default">锁定</span>';
        } else {
            return '<span class="label label-success">正常</span>';
        }
    }
    // 新增
    var createDialog;
    function createAction() {
        createDialog = $.dialog({
            animationSpeed: 300,
            title: '新增用户',
            columnClass: 'col-md-10 col-md-offset-1 col-sm-6 col-sm-offset-3 col-xs-10 col-xs-offset-1',
            content: 'url:${basePath}/manage/user/create',
            onContentReady: function () {
                initMaterialInput();
                //initTree();
            },
    		contentLoaded: function(data, status, xhr){
                if(data.code == '10110'){
                	layer.msg(data.msg);
                    location:top.location.href = '${basePath}/login';
                }
            }
        });
    }
    // 编辑
    var updateDialog;
    function updateAction() {
        var rows = $table.bootstrapTable('getSelections');
        if (rows.length != 1) {
            $.confirm({
                title: false,
                content: '请选择一条记录！',
                autoClose: 'cancel|3000',
                backgroundDismiss: true,
                buttons: {
                    cancel: {
                        text: '取消',
                        btnClass: 'waves-effect waves-button'
                    }
                }
            });
        } else {
            updateDialog = $.dialog({
                animationSpeed: 300,
                title: '编辑用户',
                columnClass: 'col-md-10 col-md-offset-1 col-sm-6 col-sm-offset-3 col-xs-10 col-xs-offset-1',
                content: 'url:${basePath}/manage/user/update/'+rows[0].userId,
                onContentReady:function(){
                    initUploadTree();
                },
                contentLoaded: function(data, status, xhr){
                    if(data.code == '10110'){
                    	layer.msg(data.msg);
                        location:top.location.href = '${basePath}/login';
                    }
                },
            });
        }
    }

    var passwordDialog;
    function passwordAction() {
        var rows = $table.bootstrapTable('getSelections');
        if (rows.length != 1) {
            $.confirm({
                title: false,
                content: '请选择一条记录！',
                autoClose: 'cancel|3000',
                backgroundDismiss: true,
                buttons: {
                    cancel: {
                        text: '取消',
                        btnClass: 'waves-effect waves-button'
                    }
                }
            });
        } else {
            passwordDialog = $.dialog({
                animationSpeed: 300,
                title: '修改密码',
                // columnClass: 'col-md-10 col-md-offset-1 col-sm-6 col-sm-offset-3 col-xs-10 col-xs-offset-1',
                content: 'url:${basePath}/manage/user/password/?userId='+rows[0].userId,
                onContentReady:function(){
                    initMaterialInput();
                },
                contentLoaded: function(data, status, xhr){
                    if(data.code == '10110'){
                    	layer.msg(data.msg);
                        location:top.location.href = '${basePath}/login';
                    }
                }
            });
        }
    }

    // 删除
    var deleteDialog;
    function deleteAction() {
        var rows = $table.bootstrapTable('getSelections');
        if (rows.length == 0) {
            $.confirm({
                title: false,
                content: '请至少选择一条记录！',
                autoClose: 'cancel|3000',
                backgroundDismiss: true,
                buttons: {
                    cancel: {
                        text: '取消',
                        btnClass: 'waves-effect waves-button'
                    }
                }
            });
        } else {
            deleteDialog = $.confirm({
                type: 'red',
                animationSpeed: 300,
                title: false,
                content: '确认删除该用户吗？',
                buttons: {
                    confirm: {
                        text: '确认',
                        btnClass: 'waves-effect waves-button',
                        action: function () {
                            var ids = new Array();
                            for (var i in rows) {
                                ids.push(rows[i].userId);
                            }
                            $.ajax({
                                type: 'get',
                                url: '${basePath}/manage/user/delete/' + ids.join("-"),
                                success: function(result) {
                                    if (result.code != 1) {
                                        if (result.data instanceof Array) {
                                            $.each(result.data, function(index, value) {
                                                $.confirm({
                                                    theme: 'dark',
                                                    animation: 'rotateX',
                                                    closeAnimation: 'rotateX',
                                                    title: false,
                                                    content: value.errorMsg,
                                                    buttons: {
                                                        confirm: {
                                                            text: '确认',
                                                            btnClass: 'waves-effect waves-button waves-light'
                                                        }
                                                    }
                                                });
                                            });
                                        } else {
                                            $.confirm({
                                                theme: 'dark',
                                                animation: 'rotateX',
                                                closeAnimation: 'rotateX',
                                                title: false,
                                                content: result.data,
                                                buttons: {
                                                    confirm: {
                                                        text: '确认',
                                                        btnClass: 'waves-effect waves-button waves-light',
                                                        location:top.location.href = location.href
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        deleteDialog.close();
                                        $table.bootstrapTable('refresh');
                                    }
                                },
                                error: function(XMLHttpRequest, textStatus, errorThrown) {
                                    $.confirm({
                                        theme: 'dark',
                                        animation: 'rotateX',
                                        closeAnimation: 'rotateX',
                                        title: false,
                                        content: textStatus,
                                        buttons: {
                                            confirm: {
                                                text: '确认',
                                                btnClass: 'waves-effect waves-button waves-light'
                                            }
                                        }
                                    });
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
    }
    // 用户组织
    var organizationDialog;
    var organizationUserId;
    function organizationAction() {
        var rows = $table.bootstrapTable('getSelections');
        if (rows.length != 1) {
            $.confirm({
                title: false,
                content: '请选择一条记录！',
                autoClose: 'cancel|3000',
                backgroundDismiss: true,
                buttons: {
                    cancel: {
                        text: '取消',
                        btnClass: 'waves-effect waves-button'
                    }
                }
            });
        } else {
            organizationUserId = rows[0].userId;
            organizationDialog = $.dialog({
                animationSpeed: 300,
                title: '用户组织',
                content: 'url:${basePath}/manage/user/organization/' + organizationUserId,
                onContentReady: function () {
                    initMaterialInput();
                    $('select').select2({
                        placeholder: '请选择用户组织',
                        allowClear: true
                    });
                },
                contentLoaded: function(data, status, xhr){
                    if(data.code == '10110'){
                    	layer.msg(data.msg);
                        location:top.location.href = '${basePath}/login';
                    }
                }
            });
        }
    }
    // 用户角色
    var roleDialog;
    var roleUserId;
    function roleAction() {
        var rows = $table.bootstrapTable('getSelections');
        if (rows.length != 1) {
            $.confirm({
                title: false,
                content: '请选择一条记录！',
                autoClose: 'cancel|3000',
                backgroundDismiss: true,
                buttons: {
                    cancel: {
                        text: '取消',
                        btnClass: 'waves-effect waves-button'
                    }
                }
            });
        } else {
            roleUserId = rows[0].userId;
            roleDialog = $.dialog({
                animationSpeed: 300,
                title: '用户角色',
                content: 'url:${basePath}/manage/user/role/' + roleUserId,
                onContentReady: function () {
                    initMaterialInput();
                    $('select').select2({
                        placeholder: '请选择用户角色',
                        allowClear: true
                    });
                },
                contentLoaded: function(data, status, xhr){
                    if(data.code == '10110'){
                    	layer.msg(data.msg);
                        location:top.location.href = '${basePath}/login';
                    }
                }
            });
        }
    }
    // 用户权限
    var permissionDialog;
    var permissionUserId;
    function permissionAction() {
        var rows = $table.bootstrapTable('getSelections');
        if (rows.length != 1) {
            $.confirm({
                title: false,
                content: '请选择一条记录！',
                autoClose: 'cancel|3000',
                backgroundDismiss: true,
                buttons: {
                    cancel: {
                        text: '取消',
                        btnClass: 'waves-effect waves-button'
                    }
                }
            });
        } else {
            permissionUserId = rows[0].userId;
            permissionDialog = $.dialog({
                animationSpeed: 300,
                title: '用户授权',
                columnClass: 'large',
                content: 'url:${basePath}/manage/user/permission/' + permissionUserId,
                onContentReady: function () {
                    initMaterialInput();
                    initTree();
                },
                contentLoaded: function(data, status, xhr){
                    if(data.code == '10110'){
                    	layer.msg(data.msg);
                        location:top.location.href = '${basePath}/login';
                    }
                }
            });
        }
    }
</script>
</body>
</html>