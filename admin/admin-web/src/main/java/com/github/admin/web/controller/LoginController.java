package com.github.admin.web.controller;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.github.admin.client.service.UserServiceClient;
import com.github.admin.common.constants.Constants;
import com.github.admin.common.domain.User;
import com.github.admin.common.exception.AccountLockException;
import com.github.admin.common.exception.AccountNotFoundException;
import com.github.admin.common.exception.AccountUnknownException;
import com.github.admin.common.exception.IncorrectCaptchaException;
import com.github.admin.common.exception.IncorrectPasswordException;
import com.github.admin.service.CustomGenericManageableCaptchaService;
import com.github.admin.token.SecurityToken;
import com.github.admin.utils.RedisUtils;
import com.github.appmodel.domain.result.ModelResult;
import com.github.appmodel.domain.result.ResultUtils;
import com.github.appmodel.vo.ResultVo;
import com.octo.captcha.service.CaptchaServiceException;

import io.swagger.annotations.ApiOperation;

@Controller
public class LoginController {

	private static Logger logger = LoggerFactory.getLogger(LoginController.class);
	 
//	@Autowired
//	private RedisUtils redisUtils;
	
	@Autowired
	private UserServiceClient userServiceClient;
	
	@Autowired
	private CustomGenericManageableCaptchaService customGenericManageableCaptchaService;
	
	@GetMapping("/login")
	public String index() {
		return "login";
	}

	
	@ApiOperation("????????????")
    @RequestMapping("/login")
	public @ResponseBody Object login(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
		String userName = request.getParameter("username");
        String password = request.getParameter("password");
        String captcha = request.getParameter("captcha");
        String rememberMe = "false";
        ResultVo resultVo = new ResultVo();
        ModelResult<ResultVo> modelResult = new ModelResult<ResultVo>();
        if(StringUtils.isBlank(userName)) {
        	logger.error("???????????????????????????");
        	modelResult.withError("10000", "???????????????!");
        	return ResultUtils.buildResult(modelResult);
        }
        
        if(StringUtils.isBlank(password)) {
        	logger.error("????????????????????????");
        	modelResult.withError("10001","???????????????!");
        	return ResultUtils.buildResult(modelResult);
        }
        
//        if(StringUtils.isBlank(captcha)) {
//        	logger.error("??????????????????????????????");
//        	modelResult.withError("10002", "??????????????????!");
//        	return ResultUtils.buildResult(modelResult);
//        }
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String sessionId = session.getId().toString();
//        boolean isResponseCorrect = customGenericManageableCaptchaService.validateResponseForID(request.getSession().getId(), captcha);
//        logger.info("?????????????????????:{}????????????{}",captcha,isResponseCorrect);
//        if(!isResponseCorrect) {
//            modelResult.withError("10002", "???????????????????????????!");
//            return ResultUtils.buildResult(modelResult);
//        }
      // ??????shiro??????
      try{
    	  SecurityToken usernamePasswordToken = new SecurityToken(userName, password,captcha);
            if (BooleanUtils.toBoolean(rememberMe)) {
                usernamePasswordToken.setRememberMe(true);
            } else {
                usernamePasswordToken.setRememberMe(false);
            }
            subject.login(usernamePasswordToken);
        } catch (UnknownAccountException e) {
            logger.error("???????????????:{}",e.getMessage());
            modelResult.withError("10002", "??????????????????");
            return ResultUtils.buildResult(modelResult);
        } catch (IncorrectCredentialsException e) {
            logger.error("????????????:{}",e.getMessage());
            modelResult.withError("10003", "???????????????");
            return ResultUtils.buildResult(modelResult);
        } catch (LockedAccountException e) {
            logger.error("?????????????????????:{}",e.getMessage());
            modelResult.withError("10004", "???????????????");
            return ResultUtils.buildResult(modelResult);
        } catch(AccountUnknownException e) {
        	 modelResult.withError("10005", "????????????,??????????????????");
        	 return ResultUtils.buildResult(modelResult);
        } catch(AccountNotFoundException e) {
            logger.error("???????????????:{}",e.getMessage());
            modelResult.withError("10006", "??????????????????");
            return ResultUtils.buildResult(modelResult);
        } catch(AccountLockException e) {
            logger.error("???????????????:{}",e.getMessage());
            modelResult.withError("10007", "??????????????????");
            return ResultUtils.buildResult(modelResult);
        }catch(IncorrectPasswordException e) {
            logger.error("????????????:{}",e.getMessage());
            modelResult.withError("10008", "???????????????");
            return ResultUtils.buildResult(modelResult);
        } catch(IncorrectCaptchaException e) {
            logger.error("???????????????:{}",e.getMessage());
            modelResult.withError("10009", "??????????????????????????????");
            return ResultUtils.buildResult(modelResult);
        }catch(CaptchaServiceException e) {
        	 modelResult.withError("10010", "??????????????????");
        	 return ResultUtils.buildResult(modelResult);
        }catch(Exception e) {
        	 modelResult.withError("10005", "????????????,??????????????????");
        	 return ResultUtils.buildResult(modelResult);
        }
        logger.info("??????????????????:{}",userName);
        ModelResult<User> userModelResult = userServiceClient.selectUserByUserName(userName);
        if(!modelResult.isSuccess()) {
        	modelResult.withError("0", "???????????????");
        	return ResultUtils.buildResult(modelResult);
        }
        User user = userModelResult.getModel();
//        //???userId ??????session???
        String redisVal = JSONObject.toJSONString(user);
//        redisUtils.setEx(Constants.ADMIN_CURRENT_USER + user.getUserName(),redisVal, 60 * 60,TimeUnit.MINUTES);
        String shiroSessionKey = Constants.ADMIN_SERVER_SESSION + sessionId;
        logger.info("??????shiro session ????????????shiroSessionKey = ???{}???,???????????????userName = ???{}???",shiroSessionKey,userName);
        SecurityUtils.getSubject().getSession().setAttribute(shiroSessionKey,userName);
        
//        String userJSON = redisUtils.get(Constants.ADMIN_CURRENT_USER + userName);
        
//        logger.info("??????redis??????????????????:[{}]",userJSON);
        customGenericManageableCaptchaService.removeCaptcha(request.getSession().getId());
        resultVo = new ResultVo();
        resultVo.setCode("1");
        resultVo.setMsg("???????????????");
        modelResult.setModel(resultVo);
        return ResultUtils.buildResult(modelResult);
	}
	
}
