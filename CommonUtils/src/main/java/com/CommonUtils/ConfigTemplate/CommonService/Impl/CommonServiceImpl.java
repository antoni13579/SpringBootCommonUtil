package com.CommonUtils.ConfigTemplate.CommonService.Impl;

import java.util.Date;

import javax.annotation.Resource;

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
	
	@TransactionalJtaAnnotation(transactionManager = "txManager")
	@Override
	public void test()
	{
		this.demoService.saveOrUpdate(new Demo().setId("1").setAge(123));
		this.testDataService.saveOrUpdate(new TestData().setId("1").setStatus("1").setCreateBy("1").setCreateDate(new Date()).setUpdateBy("1").setUpdateDate(new Date()));
	}
	
	/*适用于事务下唯一数据源切换
	 * @Transactional(rollbackFor={Exception.class,RuntimeException.class})
@DataSource("master")
    public GmcSmsInfo addMaster(GmcSmsInfo smsInfo) throws BusinessServiceException {
        try {
            smsInfo.setSmsId(gmcSmsInfoDaoImpl.save(smsInfo));
        }
        catch (FrameworkDAOException e) {
            throw new BusinessServiceException(e);
        }
        return smsInfo;
    }
	 * 
	 * */
}