package com.github.admin.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.github.admin.common.constants.Constants;
import com.github.admin.utils.RedisUtils;


@Component
public class SessionTimeOutInterceptor extends HandlerInterceptorAdapter{

	private static Logger logger = LoggerFactory.getLogger(SessionTimeOutInterceptor.class);
	
	private static final String LOGIN_URL = "/login";
	
	@Autowired
	private RedisUtils redisUtils;
	
	
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
       
		//1、首先判断session是否为空
		if(httpServletRequest.getSession() == null) {
			logger.error("当前用户session为空，用户需重新登录！");
			httpServletResponse.sendRedirect(LOGIN_URL);
            return false;
		}
		HttpSession session = httpServletRequest.getSession();
        String sessionId = session.getId();
        //2、判断shiro主题是否为空
        if(SecurityUtils.getSubject() == null || sessionId == null) {
        	logger.error("当前用户subject或者sessionId为空，用户需重新登录！");
        	httpServletResponse.sendRedirect(LOGIN_URL);
            return false;
        }
        Subject subject = SecurityUtils.getSubject();
        //3、判断shiro session是否为空
        if(subject.getSession() == null) {
        	logger.error("当前用户shiro sessionId为空，用户需重新登录！");
        	httpServletResponse.sendRedirect(LOGIN_URL);
            return false;
        }
        Session shiroSession = subject.getSession();
        String shiroSessionKey = Constants.ADMIN_SERVER_SESSION + sessionId;
        Object sessionObj = shiroSession.getAttribute(shiroSessionKey);
        
        if(sessionObj == null) {
        	logger.error("当前用户userName object为空，用户需重新登录！");
        	httpServletResponse.sendRedirect(LOGIN_URL);
            return false;
        }
        String currentUserName = (String)sessionObj;
        logger.info("用户session超时拦截器当前currentName = 【{}】",currentUserName);
        
        String userJSON = redisUtils.get(Constants.ADMIN_CURRENT_USER + currentUserName);
        logger.info("用户session超时拦截器当前userJson = 【{}】",userJSON);
        
       // String requestURI = httpServletRequest.getRequestURI();
      //  System.err.println(requestURI);
        if(StringUtils.isEmpty(userJSON)){
        	logger.error("当前用户redis 缓存用户数据为空，用户需重新登录！");
            httpServletResponse.sendRedirect(LOGIN_URL);
            return false;
        }
        return true;
    }

}
