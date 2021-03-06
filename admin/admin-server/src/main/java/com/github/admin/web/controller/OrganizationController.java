package com.github.admin.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.admin.common.domain.Organization;
import com.github.admin.common.request.OrganizationRequest;
import com.github.admin.common.service.OrganizationService;
import com.github.appmodel.domain.result.ModelResult;
import com.github.appmodel.vo.PageVo;

@RestController
@RequestMapping("/admin/server/organization")
public class OrganizationController {
	
	@Autowired
	private OrganizationService organizationServiceImpl;
	
	@PostMapping("/pageOrganizationList")
	ModelResult<PageVo> pageOrganizationList(@RequestBody OrganizationRequest organizationRequest){
		return organizationServiceImpl.pageOrganizationList(organizationRequest);
	}
	
	@PostMapping("/insertSelective")
	public ModelResult<Integer> insertSelective(@RequestBody Organization organization){
		return organizationServiceImpl.insertSelective(organization);
	}

	@GetMapping("selectByPrimaryKey/{organizationId}")
	public ModelResult<Organization> selectByPrimaryKey(@PathVariable("organizationId")Integer organizationId) {
		return organizationServiceImpl.selectByPrimaryKey(organizationId);
	}

	@PostMapping("/updateByPrimaryKeySelective")
	public ModelResult<Integer> updateByPrimaryKeySelective(@RequestBody Organization organization){
		return organizationServiceImpl.updateByPrimaryKeySelective(organization);
	}

	@GetMapping("/deleteByPrimaryKeys/{organizationId}")
	public ModelResult<Integer> deleteByPrimaryKeys(@PathVariable("organizationId") String organizationId){
		return organizationServiceImpl.deleteByPrimaryKey(organizationId);
	}

	@PostMapping("/allOrganizationList")
	public ModelResult<List<Organization>> allOrganizationList(){
		return organizationServiceImpl.allOrganizationList();
	}

}
