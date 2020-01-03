package com.github.admin.common.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.github.admin.common.domain.Permission;
import com.github.admin.common.domain.PermissionInfo;
import com.github.admin.common.vo.PageVo;
import com.github.appmodel.domain.result.ModelResult;
import com.github.appmodel.page.DataPage;

public interface PermissionService {
	
	/***
	 * 根据系统ID和用户ID查询权限
	 * @param systemId
	 * @param userId
	 * @return
	 */
	ModelResult<List<Permission>> selectPermissionByUserId(Integer systemId, Integer userId);

	ModelResult<PageVo> pagePermissionInfoList(DataPage<PermissionInfo> dataPage);

	ModelResult<JSONArray> getTreeByRoleId(Integer roleId);

	ModelResult<Permission> selectByPrimaryKey(Integer permissionId);

	ModelResult<List<Permission>> selectByParentId(Integer parentId);

	ModelResult<Integer> updateByPrimaryKeySelective(Permission permission);

	ModelResult<Integer> insertSelective(Permission permission);

	ModelResult<JSONArray> getTreeByUserId(Integer userId, Integer userPermissionType);

	ModelResult<Integer> deleteByPrimaryKeys(String ids);

}
