package com.github.admin.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.github.admin.client.service.PermissionServiceClient;
import com.github.admin.client.service.SystemServiceClient;
import com.github.admin.client.service.UserServiceClient;
import com.github.admin.common.constants.Constants;
import com.github.admin.common.domain.Permission;
import com.github.admin.common.domain.User;
import com.github.admin.common.exception.AccountNotFoundException;
import com.github.admin.utils.RedisUtils;
import com.github.appmodel.domain.result.ModelResult;

@Controller
@RequestMapping("/manage")
public class ManagerController {

	@Autowired
	private SystemServiceClient systemServiceClient;
	
	@Autowired
	private UserServiceClient userServiceClient;
	
	@Autowired
	private PermissionServiceClient permissionServiceClient;
	
	@Autowired
	private RedisUtils redisUtils;
	
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(ModelMap modelMap,HttpServletRequest httpServletRequest) {
//		ModelResult<List<System>> adminSystems = systemServiceClient.querySystemByStatus(1);
		Subject subject = SecurityUtils.getSubject();
		User user = (User)subject.getPrincipal();
		//用于测试session 拦截器是否成功
//		if(true) {
//			HttpSession session = httpServletRequest.getSession();
//	        String sessionId = session.getId();
//	        Session shiroSession = subject.getSession();
//	        String shiroSessionKey = Constants.ADMIN_SERVER_SESSION + sessionId;
//	        Object sessionObj = shiroSession.getAttribute(shiroSessionKey);
//	        redisUtils.delete(Constants.ADMIN_CURRENT_USER + user.getUserName());
//		}
		
		if(user == null) {
			throw new AccountNotFoundException("账号不存在！");
		}
		ModelResult<User> adminModelResult = userServiceClient.selectUserByUserName(user.getUserName());
		if(!adminModelResult.isSuccess()) {
			throw new AccountNotFoundException("账号不存在！");
		}
		User adminUser = adminModelResult.getModel();
		if(adminUser == null) {
			throw new AccountNotFoundException("账号不存在！");
		}
		ModelResult<List<Permission>> permissionModelResult = permissionServiceClient.selectPermissionByUserId(1, adminUser.getUserId());
//		modelMap.put("adminSystems", adminSystems);
		if(!permissionModelResult.isSuccess()) {
			throw new AuthorizationException("账号对应的权限不存在！");
		}
		List<Permission> adminPermissions = permissionModelResult.getModel();
		if(CollectionUtils.isEmpty(adminPermissions)) {
			throw new AuthorizationException("账号对应的权限不存在！");
		}
		modelMap.put("adminPermissions", adminPermissions);
		modelMap.put("adminUser", adminUser);
		
		return "manager/index";
	}
	
}
