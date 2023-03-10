<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="basePath" value="${pageContext.request.contextPath}"/>
<div id="updateDialog" class="crudDialog">
	<form id="updateForm" method="post">
		<div class="radio">
			<div class="radio radio-inline radio-success">
				<input id="type_1" type="radio" name="type" value="1" <c:if test="${permission.type==1}">checked</c:if>>
				<label for="type_1">目录 </label>
			</div>
			<div class="radio radio-inline radio-info">
				<input id="type_2" type="radio" name="type" value="2" <c:if test="${permission.type==2}">checked</c:if>>
				<label for="type_2">菜单 </label>
			</div>
			<div class="radio radio-inline radio-warning">
				<input id="type_3" type="radio" name="type" value="3" <c:if test="${permission.type==3}">checked</c:if>>
				<label for="type_3">按钮 </label>
			</div>
		</div>
		<div class="form-group">
			<span class="type1 type2 type3">
				<select id="permissionSystemId" name="systemId">
					<option value="0">请选择系统</option>
					<c:forEach var="system" items="${systems}">
						<option value="${system.systemId}"
								<c:if test="${permission.systemId==system.systemId}">selected="selected"</c:if>>
								${system.title}
						</option>
					</c:forEach>
				</select>
			</span>
			<span class="type2 type3" hidden>
				<select id="permissionParentId" name="parentId">
					<option value="0">请选择上级2</option>
				</select>
			</span>
		</div>
		<div class="form-group">
			<label for="name">名称</label>
			<input id="name" type="text" class="form-control" name="name" maxlength="20" value="${permission.name}">
		</div>
		<div class="form-group type2 type3" hidden>
			<label for="permissionValue">权限值</label>
			<input id="permissionValue" type="text" class="form-control" name="permissionValue" maxlength="50" value="${permission.permissionValue}">
		</div>
		<div class="form-group type2 type3" hidden>
			<label for="uri">路径</label>
			<input id="uri" type="text" class="form-control" name="uri" maxlength="100" value="${permission.uri}">
		</div>
		<div class="form-group type1 type3">
			<label for="icon">图标</label>
			<input id="icon" type="text" class="form-control" name="icon" maxlength="50" value="${permission.icon}">
			<!-- 
			<label for="icon">图标</label>
		    <select class="form-control" name="icon">
		     <option value="zmdi zmdi-accounts-list" <c:if test="${permission.icon eq 'zmdi zmdi-accounts-list'}">selected="selected"</c:if>>目录-代理商管理</option>
		      <option value="zmdi zmdi-paypal" <c:if test="${permission.icon eq 'zmdi zmdi-paypal'}">selected="selected"</c:if>>目录-支付渠道</option>
		      <option value="zmdi zmdi-plus" <c:if test="${permission.icon eq 'zmdi zmdi-plus'}">selected="selected"</c:if>>按钮-新增</option>
		      <option value="zmdi zmdi-close" <c:if test="${permission.icon eq 'zmdi zmdi-close'}">selected="selected"</c:if>>按钮-删除</option>
		      <option value="zmdi zmdi-edit" <c:if test="${permission.icon eq 'zmdi zmdi-edit'}">selected="selected"</c:if>>按钮-编辑</option>
		      <option value="zmdi zmdi-key" <c:if test="${permission.icon eq 'zmdi zmdi-key'}">selected="selected"</c:if>>按钮-角色授权</option>
		      <option value="zmdi zmdi-accounts-list" <c:if test="${permission.icon eq 'zmdi zmdi-accounts-list'}">selected="selected"</c:if>>按钮-用户组织</option>
		      <option value="zmdi zmdi-accounts" <c:if test="${permission.icon eq 'zmdi zmdi-accounts'}">selected="selected"</c:if>>按钮-用户角色</option>
		    </select>
		     -->
		</div>
		<div class="radio">
			<div class="radio radio-inline radio-success">
				<input id="status_1" type="radio" name="status" value="1" <c:if test="${permission.status==1}">checked</c:if>>
				<label for="status_1">正常 </label>
			</div>
			<div class="radio radio-inline">
				<input id="status_0" type="radio" name="status" value="0" <c:if test="${permission.status==0}">checked</c:if>>
				<label for="status_0">锁定 </label>
			</div>
		</div>
		<div class="form-group text-right dialog-buttons">
			<a class="waves-effect waves-button" href="javascript:;" onclick="updateSubmit();">保存</a>
			<a class="waves-effect waves-button" href="javascript:;" onclick="updateDialog.close();">取消</a>
		</div>
	</form>
</div>
<script>
    var parentIdType = 0;
    var systemId = ${permission.systemId};
    var type = ${permission.type};
    $(function() {
        // 选择分类
        $('input:radio[name="type"]').change(function() {
            type = $(this).val();
            initType();
        });
        $('#permissionSystemId').change(function() {
    		systemId = $(this).val();
    		initparentId();
    	});
        
    });
    function initType() {
        // 显示对应必填项
        $('.type1,.type2,.type3').hide(0, function () {
            $('.type' + type).show();
        });
        // 级联菜单
        if (type == 2) {
            parentIdType = 1;
            initparentId();
        }
        if (type == 3) {
            parentIdType = 2
            initparentId();
        }
    }
    function initparentId(val) {
        if (systemId != 0) {
        	var requestData = {
        			'systemId':systemId,
        			'type':parentIdType,
        			'limit':10000
        		  };
        	$.ajax({
        		type: "post",
                url : "${basePath}/manage/permission/list",
                contentType: "application/json;charset=utf-8", 
                data:JSON.stringify(requestData),
                dataType: "json",
                success:function (result) {
                	var datas = [{id: 0, text: '请选择上级'}];
                    for (var i = 0; i < result.rows.length; i ++) {
                    	 var data = {};
                         data.id = result.rows[i].permissionId;
                         data.text = result.rows[i].name;
                         datas.push(data);
                    }
                    $('#permissionParentId').empty();
                    $('#permissionParentId').select2({
                        data : datas
                    });
                    if (!!val) {
                        $('#permissionParentId').select2().val(val).trigger('change');
                    }
                },
                error:function (message) {
                	$('#permissionParentId').empty();
                    $('#permissionParentId').select2({
                        data : [{id: 0, text: '请选择上级'}]
                    });
                }
            });
        } else {
             $('#permissionParentId').empty();
             $('#permissionParentId').select2({
                 data : [{id: 0, text: '请选择上级'}]
             });
        }
    } 
    function initSelect2() {
    	var parentId = '${permission.parentId}';
        if (type == 2) {
            parentIdType = 1;
        }
        if (type == 3) {
            parentIdType = 2
        }
        initparentId(parentId);
    }
    function updateSubmit() {
        $.ajax({
            type: 'post',
            url: '${basePath}/manage/permission/update/${permission.permissionId}',
            data: $('#updateForm').serialize(),
            beforeSend: function() {
                if ($('#permissionSystemId').val() == 0) {
                    $.confirm({
                        title: false,
                        content: '请选择系统！',
                        autoClose: 'cancel|3000',
                        backgroundDismiss: true,
                        buttons: {
                            cancel: {
                                text: '取消',
                                btnClass: 'waves-effect waves-button'
                            }
                        }
                    });
                    return false;
                }
                if (type == 1) {
                    if ($('#name').val() == '') {
                        $('#name').focus();
                        return false;
                    }
                }
                if (type == 2 || type == 3) {
                    if ($('#permissionParentId').val() == 0) {
                        $.confirm({
                            title: false,
                            content: '请选择上级！',
                            autoClose: 'cancel|3000',
                            backgroundDismiss: true,
                            buttons: {
                                cancel: {
                                    text: '取消',
                                    btnClass: 'waves-effect waves-button'
                                }
                            }
                        });
                        return false;
                    }
                    if ($('#name').val() == '') {
                        $('#name').focus();
                        return false;
                    }
                    if ($('#permissionValue').val() == '') {
                        $('#permissionValue').focus();
                        return false;
                    }
                    if ($('#uri').val() == '') {
                        $('#uri').focus();
                        return false;
                    }
                }
            },
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
                    }else{
                        $.confirm({
                            theme: 'dark',
                            animation: 'rotateX',
                            closeAnimation: 'rotateX',
                            title: false,
                            content: result.msg,
                            buttons: {
                                confirm: {
                                    text: '确认',
                                    btnClass: 'waves-effect waves-button waves-light'
                                }
                            }
                        });
                    }
                } else {
                    updateDialog.close();
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
</script>