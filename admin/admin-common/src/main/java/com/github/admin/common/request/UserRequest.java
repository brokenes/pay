package com.github.admin.common.request;

import com.github.admin.common.domain.BaseObject;
import com.github.admin.common.domain.UserInfo;
import com.github.appmodel.page.DataPage;

public class UserRequest extends BaseObject{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5599126119006893534L;
	
	
	private DataPage<UserInfo> dataPage;
	private Integer userType;
	private String userName;
	private String organizationName;
	private Integer roleId;
	
	public DataPage<UserInfo> getDataPage() {
		return dataPage;
	}
	public void setDataPage(DataPage<UserInfo> dataPage) {
		this.dataPage = dataPage;
	}
	public Integer getUserType() {
		return userType;
	}
	public void setUserType(Integer userType) {
		this.userType = userType;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

}
