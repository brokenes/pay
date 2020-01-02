package com.github.admin.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.admin.common.constants.Constants;
import com.github.admin.common.domain.UserRole;
import com.github.admin.common.service.UserRoleService;
import com.github.admin.server.dao.UserRoleDao;
import com.github.appmodel.domain.result.ModelResult;

@Service
public class UserRoleServiceImpl implements UserRoleService {

	@Autowired
	private UserRoleDao userRoleDao;
	
	@Override
	public ModelResult<List<UserRole>> selectByUserId(Integer userId) {
		ModelResult<List<UserRole>> modelResult = new ModelResult<List<UserRole>>();
		if(userId == null || userId == 0) {
			modelResult.withError(Constants.FAIL_MSG_CODE, "用户ID为空或非法参数");
			return modelResult;
		}
		List<UserRole> list = userRoleDao.selectByUserId(userId);
		modelResult.setModel(list);
		return modelResult;
	}

}