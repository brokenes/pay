package com.github.pattern.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.baidu.unbiz.fluentvalidator.ComplexResult;
import com.baidu.unbiz.fluentvalidator.FluentValidator;
import com.baidu.unbiz.fluentvalidator.ResultCollectors;
import com.github.admin.common.constants.Constants;
import com.github.appmodel.domain.result.ModelResult;
import com.github.appmodel.domain.result.ResultUtils;
import com.github.appmodel.vo.PageVo;
import com.github.pattern.client.service.AgentServiceClient;
import com.github.pattern.common.constants.PatternConstants;
import com.github.pattern.common.domain.Agent;
import com.github.pattern.common.request.AgentRequest;
import com.github.pattern.utils.EmailValidator;
import com.github.pattern.utils.LengthValidator;
import com.github.pattern.utils.NotBlankValidator;
import com.github.pattern.utils.NotNullValidator;
import com.github.pattern.utils.PhoneValidator;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/manage/agent")
public class AgentController {

	private static final int MAX_FILE_SIZE = 10 * 1024 * 1024;
	
	private final static Logger logger = LoggerFactory.getLogger(AgentController.class);
	
	
	@Autowired
	private AgentServiceClient agentServiceClient;
	
	@ApiOperation("???????????????")
    @RequiresPermissions("pattern:agent:read")
    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String index(){
       return "/manager/agent/index";
    }
	
	
	@ApiOperation("???????????????")
    @RequiresPermissions("pattern:agent:read")
    @RequestMapping(value = "/list",method = RequestMethod.POST)
	public @ResponseBody Object list(@RequestBody AgentRequest request) {
		ModelResult<PageVo> modelResult = agentServiceClient.page(request);
		return ResultUtils.buildPageResult(modelResult);
	}
	
	@ApiOperation("??????")
    @RequiresPermissions("pattern:agent:create")
    @RequestMapping(value = "/create",method = RequestMethod.GET)
	public String create() {
		return "/manager/agent/create";
	}
	
	@ApiOperation("????????????")
    @RequiresPermissions("pattern:agent:create")
    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST)
	public Object create(Agent agent) {
		ComplexResult result = valid(agent);
        if (!result.isSuccess()) {
            return ResultUtils.buildErrorMsg(Constants.FAIL_MSG_CODE, result.getErrors());
        }
        
        ModelResult<Integer> modelResult = agentServiceClient.insertSelective(agent);
        return ResultUtils.buildResult(modelResult);
	}
	
	
	private ComplexResult valid(Agent agent) {
		ComplexResult result = FluentValidator.checkAll()
                .on(agent.getAgentName(), new LengthValidator(2, 50, "???????????????"))
                .on(agent.getAgentName(), new NotBlankValidator("???????????????"))
                .on(agent.getBusinessLicense(), new LengthValidator(2, 20, "???????????????"))
                .on(agent.getAddress(), new LengthValidator(2, 100, "??????"))
                .on(agent.getIdCardFrontPath(), new NotNullValidator("???????????????"))
                .on(agent.getIdCardBackPath(), new NotNullValidator("???????????????"))
                .on(agent.getCompanyPicPath(), new NotNullValidator("????????????????????????"))
                .on(agent.getPhone(), new PhoneValidator("????????????"))
                .on(agent.getEmail(), new EmailValidator("email"))
                .on(agent.getQq(), new LengthValidator(2, 20, "qq"))
                .on(agent.getWechat(), new LengthValidator(2, 30, "??????"))
                .doValidate()
                .result(ResultCollectors.toComplex());
		return result;
	}
	
	@ApiOperation("??????")
    @RequiresPermissions("pattern:agent:update")
    @RequestMapping(value = "/update/{id}",method = RequestMethod.GET)
	public String update(@PathVariable("id") int id, ModelMap modelMap) {
		ModelResult<Agent> modelResult = agentServiceClient.selectByPrimaryKey(id);
        if(!modelResult.isSuccess()) {
        	throw new NullPointerException("????????????");
        }
        modelMap.put("agent", modelResult.getModel());
		return "/manager/agent/update";
	}
	
	@ApiOperation("????????????")
    @RequiresPermissions("pattern:agent:update")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Object update(Agent agent) {
		ComplexResult result = valid(agent);
        if (!result.isSuccess()) {
            return ResultUtils.buildErrorMsg(Constants.FAIL_MSG_CODE, result.getErrors());
        }
        ModelResult<Integer> modelResult =  agentServiceClient.updateByPrimaryKeySelective(agent);
        return ResultUtils.buildResult(modelResult);
    }
	
	@ApiOperation("????????????")
    @RequiresPermissions("pattern:agent:upload")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
	public Object upload(@RequestParam("file") MultipartFile myFile,HttpServletRequest request) throws IOException{
		ModelResult<String> modelResult = new ModelResult<String>();
//		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//        CommonsMultipartFile myFile = (CommonsMultipartFile) multipartRequest.getFile("file");
        if(myFile.getSize() > MAX_FILE_SIZE) {
        	modelResult.withError("0", "????????????????????????10M??????????????????");
        	return ResultUtils.buildResult(modelResult);
        }
        if(myFile == null || myFile.isEmpty()) {
        	modelResult.withError("0", "??????????????????????????????????????????");
        	return ResultUtils.buildResult(modelResult);
        }
        
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
        Date date = new Date();
        Long time = date.getTime();
        String realPath = PatternConstants.UPLOAD_FILE_PATH;  // ?????????????????????????????????????????????????????????
        // ???????????????
        // ?????????????????????
        String originalFilename = time.toString().substring(time.toString().length() - 8,time.toString().length());
               originalFilename = originalFilename.concat(".");
               originalFilename = originalFilename.concat(myFile.getOriginalFilename().toString().substring(myFile.getOriginalFilename().toString().indexOf(".") + 1));
            // ????????????Apache???FileUtils?????????????????????
          FileUtils.copyInputStreamToFile(myFile.getInputStream(), new File(realPath, originalFilename));
          String fileUrl = basePath + PatternConstants.UPLOAD_ACCESS_PATH + originalFilename;
          logger.info("??????????????????????????????fileUrl = ???{}???",fileUrl);
          modelResult.setModel(fileUrl);
		 return ResultUtils.buildResult(modelResult);
	}
}
