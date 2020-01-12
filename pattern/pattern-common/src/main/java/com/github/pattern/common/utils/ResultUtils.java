package com.github.pattern.common.utils;

import org.apache.commons.lang3.StringUtils;

import com.github.pattern.common.vo.ErrorMsgVo;
import com.github.pattern.common.vo.PageVo;
import com.github.pattern.common.vo.ResultVo;
import com.github.appmodel.domain.result.ModelResult;

public class ResultUtils {
	
	private static final String SUCCESS_MSG = "请求成功!";
	private static final String FAIL_MSG = "请求失败!";
	private static final String SUCCESS_CODE = "1";
	private static final String FAIL_CODE = "0";
			
	public static <T> ResultVo  buildResult(ModelResult<T> modelResult){
		ResultVo resultVo = new ResultVo();
		if(modelResult.isSuccess()) {
			resultVo.setData(modelResult.getModel());
			resultVo.setCode(SUCCESS_CODE);
			resultVo.setSuccess(true);
			resultVo.setMsg(SUCCESS_MSG);
		} else {
			resultVo.setData("");
			resultVo.setSuccess(false);
			resultVo.setCode(StringUtils.isNotBlank(modelResult.getErrorCode()) ? modelResult.getErrorCode() : FAIL_CODE);
			resultVo.setMsg(StringUtils.isNotBlank(modelResult.getErrorMsg()) ? modelResult.getErrorMsg() : FAIL_MSG);
		}
		return resultVo;
	}
	
	public static ErrorMsgVo buildErrorMsg(String code,Object data) {
		ErrorMsgVo errorMsgVo = new ErrorMsgVo(code,data);
		errorMsgVo.setMsg(FAIL_MSG);
		errorMsgVo.setSuccess(false);
		return errorMsgVo;
	}

	public static PageVo buildPageResult(ModelResult<PageVo> modelResult){
		PageVo pageVo = new PageVo();
		if(modelResult.isSuccess()) {
			pageVo = modelResult.getModel();
			pageVo.setCode(SUCCESS_CODE);
			pageVo.setSuccess(true);
			pageVo.setMsg(SUCCESS_MSG);
		} else {
			pageVo.setRows("");
			pageVo.setTotal(0);
			pageVo.setSuccess(false);
			pageVo.setCode(StringUtils.isNotBlank(modelResult.getErrorCode()) ? modelResult.getErrorCode() : FAIL_CODE);
			pageVo.setMsg(StringUtils.isNotBlank(modelResult.getErrorMsg()) ? modelResult.getErrorMsg() : FAIL_MSG);
		}
		return pageVo;
	}
}