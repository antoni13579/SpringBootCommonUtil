package com.CommonUtils.ConfigTemplate.Config.SpringIntegrationConfig;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import com.CommonUtils.ConfigTemplate.Bean.ExcelBean;

/**有些函数可能不需要入参的，但如果不加，会导致SpringIntegration不能正常工作，
* 只能是强制添加，在调用的时候，不能传入null值，但是空串，0这些都可以*/
@MessagingGateway(name = "mainGateWay")
public interface MainGateWay 
{
	@Gateway(requestChannel = "startToHandleExcelData.input")
	void startToHandleExcelData(final ExcelBean excelBean);
}