package com.github.admin.server.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.admin.common.constants.Constants;
import com.github.admin.common.domain.Permission;
import com.github.admin.common.domain.PermissionInfo;
import com.github.admin.common.domain.RolePermission;
import com.github.admin.common.domain.System;
import com.github.admin.common.domain.UserPermission;
import com.github.admin.common.request.PermissionRequest;
import com.github.admin.common.service.PermissionService;
import com.github.admin.server.dao.PermissionDao;
import com.github.admin.server.dao.RolePermissionDao;
import com.github.admin.server.dao.SystemDao;
import com.github.admin.server.dao.UserPermissionDao;
import com.github.appmodel.domain.result.ModelResult;
import com.github.appmodel.page.DataPage;
import com.github.appmodel.vo.PageVo;

@Service
public class PermissionServiceImpl extends BaseService implements PermissionService {

	private static Logger logger = LoggerFactory.getLogger(PermissionServiceImpl.class);
	
	@Autowired
	private PermissionDao permissionDao;
	@Autowired
	private UserPermissionDao userPermissionDao;
	@Autowired
	private RolePermissionDao rolePermissionDao;
	@Autowired
	private SystemDao systemDao;
	
	@Override
	public ModelResult<List<Permission>> selectPermissionByUserId(Integer systemId, Integer userId) {
		logger.info("根据systemId = 【{}】，userId = 【{}】查询用户权限！",systemId,userId);
		ModelResult<List<Permission>> modelResult = new ModelResult<List<Permission>>();
		if(systemId == null || userId == null) {
			logger.error("系统对应的systemId或者账号对应的userId为空！");
			modelResult.withError("0","系统ID或者账号ID为空！");
			return modelResult;
		}
		List<Permission> permissions = permissionDao.selectPermissionByUserId(systemId,userId);
		modelResult.setModel(permissions);
		return modelResult;
	}


	@Override
	public ModelResult<PageVo> pagePermissionInfoList(PermissionRequest permissionRequest) {
		ModelResult<PageVo> modelResult = new ModelResult<PageVo>();
		DataPage<PermissionInfo> dataPage = new DataPage<PermissionInfo>();
		PageVo pageVo = new PageVo();
		this.setDataPage(dataPage,permissionRequest);
		int start = dataPage.getStartIndex();
		int offset = dataPage.getPageSize();
		Integer systemId = permissionRequest.getSystemId();
		Integer type = permissionRequest.getType();
		String name = permissionRequest.getName();
		Integer parentId = permissionRequest.getParentId();
		long totalCount = permissionDao.pagePermissionInfoListCount(systemId,type,name,parentId);
		List<PermissionInfo> result = permissionDao.pagePermissionInfoList(start,offset,systemId,type,name,parentId);
		pageVo.setTotal(totalCount);
		pageVo.setRows(result);
		modelResult.setModel(pageVo);
		return modelResult;
	}


	@Override
	public ModelResult<JSONArray> getTreeByRoleId(Integer roleId) {
		ModelResult<JSONArray> modelResult = new ModelResult<JSONArray>();
		if(roleId == null || roleId == 0) {
			modelResult.withError("0","角色ID为非法参数");
			return modelResult;
		}
		List<RolePermission> rolePermissions = rolePermissionDao.selectByRoleId(roleId);
		JSONArray systems = new JSONArray();
		//系统
		List<System> adminSystems = systemDao.querySystemByStatus(1);
		for (System system : adminSystems) {
			JSONObject node = new JSONObject();
			node.put("id", system.getSystemId());
			node.put("name", system.getTitle());
			node.put("nocheck", true);
			node.put("open", true);
			systems.add(node);
		}
		if(CollectionUtils.isNotEmpty(systems)) {
			for(Object system:systems) {
				Integer systemId = ((JSONObject) system).getIntValue("id");
				List<Permission> permissions = permissionDao.selectByStatusAndSystemId(1, systemId);
				if(CollectionUtils.isEmpty(permissions)) {
					continue;
				}
				//目录
				JSONArray folders = new JSONArray();
				for(Permission permission:permissions) {
					if (permission.getParentId().intValue() != 0 || permission.getType() != 1) {
						continue;
					}
					JSONObject node = new JSONObject();
					node.put("id", permission.getPermissionId());
					node.put("name", permission.getName());
					node.put("open", true);
					for(RolePermission rolePermission:rolePermissions) {
						if (rolePermission.getPermissionId().intValue() == permission.getPermissionId().intValue()) {
							node.put("checked", true);
						}
					}
					folders.add(node);
					//菜单
					JSONArray menus = new JSONArray();
					for (Object folder : folders) {
						for (Permission permission2: permissions) {
							if (permission2.getParentId().intValue() != ((JSONObject) folder).getIntValue("id") || permission2.getType() != 2) {
								continue;
							}
							JSONObject node2 = new JSONObject();
							node2.put("id", permission2.getPermissionId());
							node2.put("name", permission2.getName());
							node2.put("open", true);
							for (RolePermission rolePermission : rolePermissions) {
								if (rolePermission.getPermissionId().intValue() == permission2.getPermissionId().intValue()) {
									node2.put("checked", true);
								}
							}
							menus.add(node2);
							// 按钮
							JSONArray buttons = new JSONArray();
							for (Object menu : menus) {
								for (Permission permission3: permissions) {
									if (permission3.getParentId().intValue() != ((JSONObject) menu).getIntValue("id") || permission3.getType() != 3) {
										continue;
									}
									JSONObject node3 = new JSONObject();
									node3.put("id", permission3.getPermissionId());
									node3.put("name", permission3.getName());
									node3.put("open", true);
									for (RolePermission rolePermission : rolePermissions) {
										if (rolePermission.getPermissionId().intValue() == permission3.getPermissionId().intValue()) {
											node3.put("checked", true);
										}
									}
									buttons.add(node3);
								}
								if (buttons.size() > 0) {
									((JSONObject) menu).put("children", buttons);
									buttons = new JSONArray();
								}
							}
						}
						if (menus.size() > 0) {
							((JSONObject) folder).put("children", menus);
							menus = new JSONArray();
						}
					}
				}
				if (folders.size() > 0) {
					((JSONObject) system).put("children", folders);
				}
			}
		}
		
		modelResult.setModel(systems);
		return modelResult;
	}


	@Override
	public ModelResult<Permission> selectByPrimaryKey(Integer permissionId) {
		ModelResult<Permission> modelResult = new ModelResult<Permission>();
		if(permissionId == null || permissionId == 0) {
			modelResult.withError("0","权限ID为非法参数");
			return modelResult;
		}
		Permission permission = permissionDao.selectByPrimaryKey(permissionId);
		modelResult.setModel(permission);
		return modelResult;
	}


	@Override
	public ModelResult<List<Permission>> selectByParentId(Integer parentId) {
		ModelResult<List<Permission>> modelResult = new ModelResult<List<Permission>>();
		if(parentId == null) {
			modelResult.withError("0","父ID为非法参数");
			return modelResult;
		}
		List<Permission> list = permissionDao.selectByParentId(parentId);
		modelResult.setModel(list);
		return modelResult;
	}


	@Override
	public ModelResult<Integer> updateByPrimaryKeySelective(Permission permission) {
		ModelResult<Integer> modelResult = new ModelResult<Integer>();
		if(permission == null || permission.getPermissionId() == null) {
			modelResult.withError("0","编辑权限输入包含了非法参数");
			return modelResult;
		}
		int result = permissionDao.updateByPrimaryKeySelective(permission);
		if(result > 0) {
			modelResult.setModel(result);
		}else {
			modelResult.withError(Constants.FAIL_MSG_CODE,Constants.UPDATE_FAIL_MSG);
		}
		return modelResult;
	}


	@Override
	public ModelResult<Integer> insertSelective(Permission permission) {
		ModelResult<Integer> modelResult = new ModelResult<Integer>();
		if(permission == null) {
			modelResult.withError("0","添加权限输入包含了非法参数");
			return modelResult;
		}
		int result = permissionDao.insertSelective(permission);
		if(result > 0) {
			modelResult.setModel(result);
		}else {
			modelResult.withError(Constants.FAIL_MSG_CODE,Constants.ADD_FAIL_MSG);
		}
		return modelResult;
	}


	@Override
	public ModelResult<JSONArray> getTreeByUserId(Integer userId, Integer userPermissionType) {
		ModelResult<JSONArray> modelResult = new ModelResult<JSONArray>();
		if(userId == null || userId == 0 || userPermissionType == null) {
			modelResult.withError("0","查询权限树输入了包含了非法参数");
			return modelResult;
		}
		//用户角色权限
		List<UserPermission> userPermissions = userPermissionDao.selectByUserIdAndType(userId, userPermissionType);
		JSONArray systems = new JSONArray();
		//系统
		List<System> adminSystems = systemDao.querySystemByStatus(1);
		
		for(System adminSystem:adminSystems) {
			JSONObject node = new JSONObject();
			node.put("id", adminSystem.getSystemId());
			node.put("name", adminSystem.getTitle());
			node.put("nocheck", true);
			node.put("open", true);
			systems.add(node);
		}
		if(CollectionUtils.isNotEmpty(systems)) {
			for(Object system:systems) {
				Integer systemId = ((JSONObject) system).getIntValue("id");
				List<Permission> permissions = permissionDao.selectByStatusAndSystemId(1, systemId);
				if(CollectionUtils.isEmpty(permissions)) {
					continue;
				}
				JSONArray folders = new JSONArray();
				for(Permission permission:permissions) {
					if (permission.getParentId().intValue() != 0 || permission.getType() != 1) {
						continue;
					}
					JSONObject node = new JSONObject();
					node.put("id", permission.getPermissionId());
					node.put("name", permission.getName());
					node.put("open", true);
					for (UserPermission userPermission : userPermissions) {
						if (userPermission.getPermissionId().intValue() == permission.getPermissionId().intValue()) {
							node.put("checked", true);
						}
					}
					folders.add(node);
					// 菜单
					JSONArray menus = new JSONArray();
					for (Object folder : folders) {
						for (Permission permission2: permissions) {
							if (permission2.getParentId().intValue() != ((JSONObject) folder).getIntValue("id") || permission2.getType() != 2) {
								continue;
							}
							JSONObject node2 = new JSONObject();
							node2.put("id", permission2.getPermissionId());
							node2.put("name", permission2.getName());
							node2.put("open", true);
							for (UserPermission userPermission : userPermissions) {
								if (userPermission.getPermissionId().intValue() == permission2.getPermissionId().intValue()) {
									node2.put("checked", true);
								}
							}
							menus.add(node2);
							// 按钮
							JSONArray buttons = new JSONArray();
							for (Object menu : menus) {
								for (Permission permission3: permissions) {
									if (permission3.getParentId().intValue() != ((JSONObject) menu).getIntValue("id") || permission3.getType() != 3) {
										continue;
									}
									JSONObject node3 = new JSONObject();
									node3.put("id", permission3.getPermissionId());
									node3.put("name", permission3.getName());
									node3.put("open", true);
									for (UserPermission userPermission : userPermissions) {
										if (userPermission.getPermissionId().intValue() == permission3.getPermissionId().intValue()) {
											node3.put("checked", true);
										}
									}
									buttons.add(node3);
								}
								if (buttons.size() > 0) {
									((JSONObject) menu).put("children", buttons);
									buttons = new JSONArray();
								}
							}
						}
						if (menus.size() > 0) {
							((JSONObject) folder).put("children", menus);
							menus = new JSONArray();
						}
					}
					
				}
				if (folders.size() > 0) {
					((JSONObject) system).put("children", folders);
				}
			}
		}
		modelResult.setModel(systems);
		return modelResult;
	}


	@Override
	@Transactional
	public ModelResult<Integer> deleteByPrimaryKeys(String ids) {
		ModelResult<Integer> modelResult = new ModelResult<Integer>();
		if(ids == null) {
			modelResult.withError("0","删除权限输入包含了非法参数");
			return modelResult;
		}
		String[] idArray = ids.split("-");
		int result = 0;
		for (String idStr : idArray) {
			if (StringUtils.isBlank(idStr)) {
				continue;
			}
			Integer permissionId = Integer.parseInt(idStr);
			userPermissionDao.deleteByPermissionId(permissionId);
			rolePermissionDao.deleteByPermissionId(permissionId);
			result += permissionDao.deleteByPrimaryKey(permissionId);
		}
		if(result > 0) {
			modelResult.setModel(result);
		}else {
			modelResult.withError(Constants.FAIL_MSG_CODE,Constants.DELETE_FAIL_MSG);
		}
		return modelResult;
	}


	@Override
	public ModelResult<List<Permission>> selectBySystemId(Integer systemId) {
		ModelResult<List<Permission>> modelResult = new ModelResult<List<Permission>>();
		if(systemId == null || systemId == 0) {
			modelResult.withError("0","系统Id输入非法参数");
			return modelResult;
		}
		List<Permission> list = permissionDao.selectBySystemId(systemId);
		modelResult.setModel(list);
		return modelResult;
	}

}
