package com.github.channel.client.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.appmodel.domain.result.ModelResult;
import com.github.channel.common.request.WechatPayRequest;
import com.github.channel.common.response.WechatPayResponse;

@FeignClient(name="channel-server")
@RequestMapping("/channel/server/wechatpay")
public interface WechatPayServiceClient {

	
	@PostMapping("/pay")
	public ModelResult<WechatPayResponse> pay(@RequestBody WechatPayRequest request);
}