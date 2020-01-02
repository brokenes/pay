package com.github.admin.server.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPermissionDao {

	int deleteByUserId(@Param("userId") Integer userId);

}
