package com.github.admin.web.controller;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.baidu.unbiz.fluentvalidator.ComplexResult;
import com.baidu.unbiz.fluentvalidator.FluentValidator;
import com.baidu.unbiz.fluentvalidator.ResultCollectors;
import com.github.admin.client.service.PermissionServiceClient;
import com.github.admin.client.service.SystemServiceClient;
import com.github.admin.common.constants.Constants;
import com.github.admin.common.domain.Permission;
import com.github.admin.common.domain.PermissionInfo;
import com.github.admin.common.domain.System;
import com.github.admin.common.request.PermissionRequest;
import com.github.admin.common.utils.ResultUtils;
import com.github.admin.common.vo.PageVo;
import com.github.admin.utils.LengthValidator;
import com.github.appmodel.domain.result.ModelResult;
import com.github.appmodel.page.DataPage;

import io.swagger.annotations.ApiOperation;


@Controller
@RequestMapping("/manage/permission")
public class PermissionController {

		@Autowired
		private PermissionServiceClient permissionServiceClient;
		@Autowired
		private SystemServiceClient systemServiceClient;
	
		
		@ApiOperation("权限首页")
	    @RequiresPermissions("admin:permission:read")
	    @RequestMapping(value = "/index",method = RequestMethod.GET)
	    public String index(ModelMap map){
	        ModelResult<List<System>> modelResult = systemServiceClient.querySystemByStatus(1);
	        if(!modelResult.isSuccess()) {
	        	throw new NullPointerException("查询异常");
	        }
	        List<System> systems = modelResult.getModel();
	        map.put("systems",systems);
	        return "/manager/permission/index";
	    }
		 
		@ApiOperation("权限列表")
	    @RequiresPermissions("admin:permission:read")
	    @RequestMapping(value = "/list",method = RequestMethod.GET)
	    @ResponseBody
	    public Object list(
	            @RequestParam(required = false, defaultValue = "0", value = "offset") int offset,
	            @RequestParam(required = false, defaultValue = "10", value = "limit") int limit,
	            @RequestParam(required = false, defaultValue = "", value = "search") String search,
	            @RequestParam(required = false, value = "sort") String sort,
	            @RequestParam(required = false, value = "order") String order,
	            @RequestParam(required = false,value = "systemId")Integer systemId,
	            @RequestParam(required = false,value = "type")Integer type,
	            @RequestParam(required = false,defaultValue = "",value = "name")String name) {
	        DataPage<PermissionInfo> dataPage = new DataPage<PermissionInfo>();
	        PermissionRequest permissionRequest = new PermissionRequest();
	        dataPage.setPageSize(limit);
	        dataPage.setPageNo(offset/limit+1);//TODO 分页问题
	        permissionRequest.setSystemId(systemId);
	        permissionRequest.setType(type);
	        permissionRequest.setDataPage(dataPage);
	        ModelResult<PageVo> modelResult = permissionServiceClient.pagePermissionInfoList(permissionRequest);
	        return ResultUtils.buildPageResult(modelResult);
	        
	    }
		
		
		
		
		@ApiOperation("增加权限页")
	    @RequiresPermissions("admin:permission:create")
	    @RequestMapping(value = "/create", method = RequestMethod.GET)
	    public String create(ModelMap modelMap) {
	        ModelResult<List<System>> modelResult = systemServiceClient.querySystemByStatus(1);
	        if(!modelResult.isSuccess()) {
	        	throw new NullPointerException("查询系统异常");
	        }
	        modelMap.put("systems", modelResult.getModel());
	        return "/manager/permission/create";
	    }
		 
		
		@ApiOperation("增加权限")
	    @RequiresPermissions("admin:permission:create")
	    @ResponseBody
	    @RequestMapping(value = "/create", method = RequestMethod.POST)
	    public Object create(Permission permission) {
	        ComplexResult result = FluentValidator.checkAll()
	                .on(permission.getName(), new LengthValidator(1, 20, "名称"))
	                .doValidate()
	                .result(ResultCollectors.toComplex());
	        if (!result.isSuccess()) {
	            return ResultUtils.buildErrorMsg(Constants.FAIL_MSG_CODE, result.getErrors());
	        }
	        ModelResult<Integer> modelResult = permissionServiceClient.insertSelective(permission);
	        return ResultUtils.buildResult(modelResult);
	    }
		
		 @ApiOperation("修改权限页")
	     @RequiresPermissions("admin:permission:update")
	     @RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
	     public String update(@PathVariable("id") int id, ModelMap modelMap) {
	        ModelResult<List<System>> systemModelResult = systemServiceClient.querySystemByStatus(1);
	        ModelResult<Permission> permissionModelResult = permissionServiceClient.selectByPrimaryKey(id);
	        if(!systemModelResult.isSuccess() || !permissionModelResult.isSuccess()) {
	        	throw new NullPointerException("查询系统或权限异常");
	        }
	        modelMap.put("permission", permissionModelResult.getModel());
	        modelMap.put("systems", systemModelResult.getModel());
	        return "/manager/permission/update";
	     }
		 
		 
		@ApiOperation("修改权限")
	    @RequiresPermissions("admin:permission:update")
	    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
	    @ResponseBody
	    public Object update(@PathVariable("id") int id, Permission permission) {
	        ComplexResult result = FluentValidator.checkAll()
	                .on(permission.getName(), new LengthValidator(1, 20, "名称"))
	                .doValidate()
	                .result(ResultCollectors.toComplex());
	        if (!result.isSuccess()) {
	        	return ResultUtils.buildErrorMsg(Constants.FAIL_MSG_CODE, result.getErrors());
	        }
	        ModelResult<List<Permission>> permissionModelResult = permissionServiceClient.selectByParentId(id);
	        if(!permissionModelResult.isSuccess() || CollectionUtils.isEmpty(permissionModelResult.getModel())) {
	        	throw new NullPointerException("父菜单不存在");
	        }
	       
	        permission.setPermissionId(id);
	        ModelResult<Integer> modelResult = permissionServiceClient.updateByPrimaryKeySelective(permission);
	        return ResultUtils.buildResult(modelResult);
	    }
		 
		@ApiOperation("删除权限")
	    @RequiresPermissions("admin:permission:delete")
	    @RequestMapping(value = "/delete/{ids}",method = RequestMethod.GET)
	    @ResponseBody
	    public Object delete(@PathVariable("ids") String ids) {
	        ModelResult<Integer> modelResult = permissionServiceClient.deleteByPrimaryKeys(ids);
	        return ResultUtils.buildResult(modelResult);
	    }
		
	 	@ApiOperation("角色权限树")
	    @RequiresPermissions("admin:permission:read")
	    @RequestMapping(value = "/role/{id}", method = RequestMethod.POST)
	    @ResponseBody
	    public Object role(@PathVariable("id") int roleId) {
	        ModelResult<JSONArray> modelResult = permissionServiceClient.getTreeByRoleId(roleId);
	        return modelResult.getModel();
	    }
	 	
	 	 @ApiOperation("修改用户权限")
	     @RequiresPermissions("admin:permission:read")
	     @RequestMapping(value = "/user/{id}/{type}", method = RequestMethod.POST)
	     @ResponseBody
	     public Object user(@PathVariable("id") int id, @PathVariable("type") int type) {
	 		 ModelResult<JSONArray> modelResult = permissionServiceClient.getTreeByUserId(id, type);
	         return modelResult.getModel();
	     }
	 
}