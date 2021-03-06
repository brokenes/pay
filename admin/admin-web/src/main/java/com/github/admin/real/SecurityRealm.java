package com.github.admin.real;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.admin.client.service.PermissionServiceClient;
import com.github.admin.client.service.RoleServiceClient;
import com.github.admin.client.service.UserServiceClient;
import com.github.admin.common.domain.Permission;
import com.github.admin.common.domain.Role;
import com.github.admin.common.domain.User;
import com.github.admin.common.exception.AccountLockException;
import com.github.admin.common.exception.AccountNotFoundException;
import com.github.admin.common.exception.AccountUnknownException;
import com.github.admin.common.exception.PermissionNotFoundException;
import com.github.admin.common.exception.RoleNotFoundException;
import com.github.admin.common.utils.MD5Utils;
import com.github.admin.token.SecurityToken;
import com.github.appmodel.domain.result.ModelResult;

public class SecurityRealm extends AuthorizingRealm{

	private static Logger logger = LoggerFactory.getLogger(SecurityRealm.class);
	
	@Autowired
	private UserServiceClient userServiceClient;
	
	@Autowired
    private RoleServiceClient roleServiceClient;
   
    @Autowired
    private PermissionServiceClient permissionServiceClient;
	   
	
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		User adminUser = (User)principals.getPrimaryPrincipal();
		String userName = adminUser.getUserName();
		ModelResult<User> userModelResult = userServiceClient.selectUserByUserName(userName);
		if(!userModelResult.isSuccess()) {
			logger.error("当前账号userName = 【{}】不存在！",userName);
			throw new AccountNotFoundException("当前账号不存在");
		}
		User user = userModelResult.getModel();
		Integer userId = user.getUserId();
		//当前用户角色
		ModelResult<List<Role>> roleModelResult = roleServiceClient.selectRoleByUserId(userId);
		if(!roleModelResult.isSuccess()) {
			logger.error("当前userId = 【{}】没有存在对应的角色！",userId);
			throw new RoleNotFoundException("当前角色不存在！");
		}
		List<Role> adminRoles = roleModelResult.getModel();
		Set<String> roles = new HashSet<String>();
		for(Role adminRole:adminRoles) {
			if(StringUtils.isNoneBlank(adminRole.getName())){
				roles.add(adminRole.getName());
			}
		}
		//当前用户所以权限
		ModelResult<List<Permission>> permissionModelResult = permissionServiceClient.selectPermissionByUserId(1, userId);
		if(!permissionModelResult.isSuccess()) {
			logger.error("当前系统systemId = 【1】 且userId = 【{}】没有存在对应的权限",userId);
			throw new PermissionNotFoundException("当前权限不存在！");
		}
		List<Permission> adminPermissions = permissionModelResult.getModel();
		Set<String> permisssions = new HashSet<String>();
		for(Permission permission:adminPermissions) {
			if(StringUtils.isNoneBlank(permission.getPermissionValue())) {
				permisssions.add(permission.getPermissionValue());
			}
		}
		 SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
	     simpleAuthorizationInfo.setStringPermissions(permisssions);
	     simpleAuthorizationInfo.setRoles(roles);
	     return simpleAuthorizationInfo;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
		SecurityToken token = (SecurityToken)authenticationToken;
		Session session = SecurityUtils.getSubject().getSession();
		String userName = token.getUsername();
		logger.info("获取认证信息：开始获取登录用户【{}】认证信息", userName);
		User user = null;
		
		 SimpleAuthenticationInfo simpleAuthenticationInfo = null;
	      try {
	    	  ModelResult<User> userModelResult = userServiceClient.selectUserByUserName(userName);
	  		  if(!userModelResult.isSuccess()) {
	  			logger.error("当前账号userName = 【{}】不存在！",userName);
	  			throw new AccountNotFoundException("当前账号不存在");
	  		  }
	    	  user = userModelResult.getModel();
	      } catch (Exception e) {
	         logger.error("获取认证信息：获取登录用户【{}】鉴权信息时发生异常：{}", token.getUsername(), e.getMessage());
	         throw new AccountUnknownException("未知系统异常！");
	      }
	      if (user == null) {
	         logger.info("获取认证信息：登录用户【{}】不存在", token.getUsername());
	         throw new AccountNotFoundException("用户名或密码错误！");
	      }
	      if(user.getLocked() == 1) {
	    	  logger.info("获取认证信息：登录用户【{}】已锁定", token.getUsername());
		      throw new AccountLockException("该账号已锁定，请联系管理员！");
	      }
		
		
	       char[] pass = MD5Utils.md5(new String(token.getPassword()),user.getSalt()).toCharArray();
		   token.setPassword(pass);
		   simpleAuthenticationInfo = new SimpleAuthenticationInfo(user, user.getPassword(), getName());
		   simpleAuthenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(user.getSalt()));
	       logger.info("获取认证信息：完成数据库验证登录用户【{}】信息", token.getUsername());
	      /*
	       * try { LoginLog loginLog = new LoginLog();
	       * loginLog.setSysUserId(sysUser.getSysUserId());
	       * loginLog.setIpAddr(token.getHost()); loginLog.setMacAddr(null);
	       * loginLog = loginLogService.insert(loginLog);
	       * session.setAttribute("loginLogId", loginLog.getLoginLogId()); } catch
	       * (SysException e) { e.printStackTrace(); } catch (AppException e) {
	       * e.printStackTrace(); }
	       */
	      logger.info("获取认证信息：记录登录用户【{}】日志", token.getUsername());
	      return simpleAuthenticationInfo;
	}

}
