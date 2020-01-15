package com.CommonUtils.ConfigTemplate.CommonService;

import com.CommonUtils.ConfigTemplate.CommonService.Impl.CommonServiceImpl.CommonServiceException;

public interface ICommonService
{
	void jtaTransactionForDB();
	void localTransactionByAnnotation() throws CommonServiceException;
	void localTransactionByAnnotationWithKafka() throws CommonServiceException;
	void localTransactionForDB();
	void localTransactionForRedis();
	void localTransactionForKafka();
	void elasticSearchByTransportClient();
	void r2dbc();
}