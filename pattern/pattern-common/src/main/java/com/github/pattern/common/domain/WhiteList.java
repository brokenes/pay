package com.github.pattern.common.domain;

import java.util.Date;

/**
 * 商户支付白名单
 * 
 */
public class WhiteList extends BaseObject {

	private static final long serialVersionUID = -8459939368321454348L;

	/**
	 * pay_white_list_id 主键
	 */
	private Integer whiteListId;
	
	/**
	 * 请求IP
	 */
	private String ip;
	/**
	 * status 状态，0未生效，1生效
	 */
	private Integer status;
	/**
	 * create_time 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * remark 备注
	 */
	private String remark;
	
	
	public Integer getWhiteListId() {
		return whiteListId;
	}
	public void setWhiteListId(Integer whiteListId) {
		this.whiteListId = whiteListId;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
	

}