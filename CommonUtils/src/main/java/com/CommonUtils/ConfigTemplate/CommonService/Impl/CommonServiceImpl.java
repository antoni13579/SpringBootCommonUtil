package com.CommonUtils.ConfigTemplate.CommonService.Impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.CommonUtils.ConfigTemplate.CommonService.ICommonService;
import com.CommonUtils.ConfigTemplate.MyBatis.MySql.jeesite.entity.TestData;
import com.CommonUtils.ConfigTemplate.MyBatis.MySql.jeesite.service.ITestDataService;
import com.CommonUtils.ConfigTemplate.MyBatis.MySql.lukaboot.entity.Demo;
import com.CommonUtils.ConfigTemplate.MyBatis.MySql.lukaboot.service.IDemoService;
import com.CommonUtils.Utils.Aops.Annotations.TransactionalJtaAnnotation;

//@Service("commonService")
public class CommonServiceImpl implements ICommonService
{
	@Resource
	private IDemoService demoService;
	
	@Resource
	private ITestDataService testDataService;
	
	@Resource
	private JdbcTemplate myJdbcTemplate;
	
	@Resource
	private JdbcTemplate lukabootJdbcTemplate;
	
	@TransactionalJtaAnnotation(transactionManager = "txManager")
	@Override
	public void test()
	{
		this.demoService.saveOrUpdate(new Demo().setId("1").setAge(123));
		this.testDataService.saveOrUpdate(new TestData().setId("1").setStatus("1").setCreateBy("1").setCreateDate(new Date()).setUpdateBy("1").setUpdateDate(new Date()));
		
		this.myJdbcTemplate.batchUpdate("insert into test_data(id, status, create_by, create_date, update_by, update_date) values('2', '2', '2', '2019-10-12', '2', '2019-10-12')");
	}
}