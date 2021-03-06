package com.github.pattern.web.controller;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baidu.unbiz.fluentvalidator.ComplexResult;
import com.baidu.unbiz.fluentvalidator.FluentValidator;
import com.baidu.unbiz.fluentvalidator.ResultCollectors;
import com.github.admin.common.constants.Constants;
import com.github.appmodel.domain.result.ModelResult;
import com.github.appmodel.domain.result.ResultUtils;
import com.github.appmodel.vo.PageVo;
import com.github.pattern.client.service.PaymentChannelServiceClient;
import com.github.pattern.client.service.PaymentTemplateServiceClient;
import com.github.pattern.common.domain.PaymentChannel;
import com.github.pattern.common.domain.PaymentTemplate;
import com.github.pattern.common.request.PaymentChannelRequest;
import com.github.pattern.utils.LengthValidator;
import com.github.pattern.utils.NotNullValidator;
import com.github.pattern.utils.PhoneValidator;

import io.swagger.annotations.ApiOperation;




@Controller
@RequestMapping("/manage/paymentchannel")
public class PaymentChannelController {
	
	@Autowired
	private PaymentChannelServiceClient paymentChannelServiceClient;
	
	@Autowired
	private PaymentTemplateServiceClient paymentTemplateServiceClient;
	
	@ApiOperation("支付渠道首页")
    @RequiresPermissions("pattern:paymentchannel:read")
    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String index(){
       return "/manager/paymentchannel/index";
    }
	
	@ApiOperation("支付渠道首页")
    @RequiresPermissions("pattern:paymentchannel:read")
    @RequestMapping(value = "/list",method = RequestMethod.POST)
	public @ResponseBody Object list(@RequestBody PaymentChannelRequest request) {
		ModelResult<PageVo> modelResult = paymentChannelServiceClient.page(request);
		return ResultUtils.buildPageResult(modelResult);
		
	}
	
	@ApiOperation("添加支付渠道")
    @RequiresPermissions("pattern:paymentchannel:create")
    @RequestMapping(value = "/create",method = RequestMethod.GET)
	public String create() {
		return "/manager/paymentchannel/create";
	}

	
	@ApiOperation("添加支付渠道")
    @RequiresPermissions("pattern:paymentchannel:create")
    @RequestMapping(value = "/create",method = RequestMethod.POST)
	public @ResponseBody Object create(PaymentChannel paymentChannel) {
		ComplexResult result = valid(paymentChannel);
		if (!result.isSuccess()) {
            return ResultUtils.buildErrorMsg(Constants.FAIL_MSG_CODE, result.getErrors());
        }
		ModelResult<Integer> modelResult = paymentChannelServiceClient.insertSelective(paymentChannel);
		return ResultUtils.buildResult(modelResult);
	}
	
	private ComplexResult valid(PaymentChannel paymentChannel) {
		ComplexResult result = FluentValidator.checkAll()
	            .on(paymentChannel.getChannelName(), new LengthValidator(2, 50, "渠道名称"))
	            .on(paymentChannel.getThirdChannelName(), new LengthValidator(2, 50, "三方渠道名称"))
	            .on(paymentChannel.getPayType(), new NotNullValidator("支付类型"))
	            .on(paymentChannel.getPaymentTemplateId() == null ? null :String.valueOf(paymentChannel.getPaymentTemplateId()),new NotNullValidator("渠道模板"))
	            .on(paymentChannel.getBusinessContacts(), new LengthValidator(2, 50, "联系人"))
	            .on(paymentChannel.getQq(), new LengthValidator(5, 50, "QQ"))
	            .on(paymentChannel.getWechat(), new LengthValidator(2, 50, "微信"))
	            .on(paymentChannel.getMobile(), new PhoneValidator("手机号码"))
	            .on(paymentChannel.getRemark(), new LengthValidator(2, 50, "备注"))
	            .doValidate()
	            .result(ResultCollectors.toComplex());
		return result;
	}

	@ApiOperation("编辑支付渠道")
    @RequiresPermissions("pattern:paymentchannel:update")
    @RequestMapping(value = "/update/{paymentChannelId}",method = RequestMethod.GET)
	public String update(@PathVariable("paymentChannelId") Integer paymentChannelId,ModelMap modelMap) {
		ModelResult<PaymentChannel> modelResult = paymentChannelServiceClient.selectByPrimaryKey(paymentChannelId);
		if(!modelResult.isSuccess()) {
			throw new NullPointerException("查询失败");
		}
		PaymentChannel paymentChannel = modelResult.getModel();
		String payType = paymentChannel.getPayType();
		ModelResult<List<PaymentTemplate>> templateModelResult = paymentTemplateServiceClient.selectByPayType(payType);
		if(!templateModelResult.isSuccess()) {
			throw new NullPointerException("查询失败");
		}
		modelMap.put("paymentChannel", paymentChannel);
		modelMap.put("paymentTemplateList", templateModelResult.getModel());
		return "/manager/paymentchannel/update";
	}
	
	@ApiOperation("编辑支付渠道")
    @RequiresPermissions("pattern:paymentchannel:update")
    @RequestMapping(value = "/update",method = RequestMethod.POST)
	public @ResponseBody Object update(PaymentChannel paymentChannel) {
		ComplexResult result = valid(paymentChannel);
		if (!result.isSuccess()) {
            return ResultUtils.buildErrorMsg(Constants.FAIL_MSG_CODE, result.getErrors());
        }
		ModelResult<Integer> modelResult = paymentChannelServiceClient.updateByPrimaryKeySelective(paymentChannel);
		return ResultUtils.buildResult(modelResult);
	}
}
