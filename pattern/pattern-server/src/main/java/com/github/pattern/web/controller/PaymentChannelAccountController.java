package com.github.pattern.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.appmodel.domain.result.ModelResult;
import com.github.appmodel.vo.ResultVo;
import com.github.pattern.common.domain.PaymentChannelAccount;
import com.github.pattern.common.request.PaymentChannelAccountRequest;
import com.github.pattern.common.service.PaymentChannelAccountService;

@RestController
@RequestMapping("/pattern/server/paymentChannelAccount")
public class PaymentChannelAccountController {
	
	@Autowired
	private PaymentChannelAccountService paymentChannelAccountServiceImpl;
	
	
	@PostMapping("/page")
	public ModelResult<ResultVo> page(@RequestBody PaymentChannelAccountRequest request){
		return paymentChannelAccountServiceImpl.page(request);
	}
	
	@PostMapping("/deleteByPrimaryKey/{paymentChannelAccountId}")
	public ModelResult<Integer> deleteByPrimaryKey(@PathVariable("paymentChannelAccountId")Integer paymentChannelAccountId){
		return paymentChannelAccountServiceImpl.deleteByPrimaryKey(paymentChannelAccountId);
	}

	@PostMapping("/insertSelective")
	public ModelResult<Integer> insertSelective(@RequestBody PaymentChannelAccount record){
		return paymentChannelAccountServiceImpl.insertSelective(record);
	}

	@PostMapping("/selectByPrimaryKey/{paymentChannelAccountId}")
	public ModelResult<PaymentChannelAccount> selectByPrimaryKey(@PathVariable("paymentChannelAccountId")Integer paymentChannelAccountId){
		return paymentChannelAccountServiceImpl.selectByPrimaryKey(paymentChannelAccountId);
	}

	@PostMapping("/updateByPrimaryKeySelective")
	public ModelResult<Integer> updateByPrimaryKeySelective(@RequestBody PaymentChannelAccount record){
		return paymentChannelAccountServiceImpl.updateByPrimaryKeySelective(record);
	}

	@PostMapping("/updateByPrimaryKey")
	public ModelResult<Integer> updateByPrimaryKey(@RequestBody PaymentChannelAccount record){
		return paymentChannelAccountServiceImpl.updateByPrimaryKey(record);
	}

	@PostMapping("/selectByPaymentChannelId/{paymentChannelId}")
	ModelResult<List<PaymentChannelAccount>> selectByPaymentChannelId(@PathVariable("paymentChannelId")Integer paymentChannelId){
		return paymentChannelAccountServiceImpl.selectByPaymentChannelId(paymentChannelId);
	}
}
