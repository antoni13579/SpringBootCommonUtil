package com.CommonUtils.ConfigTemplate.CommonService;

public interface ICommonService
{
	void jtaTransactionForDB();
	void localTransactionByAnnotation() throws Exception;
	void localTransactionByAnnotationWithKafka() throws Exception;
	void localTransactionForDB();
	void localTransactionForRedis();
	void localTransactionForKafka();
	void elasticSearchByTransportClient();
	void r2dbc();
}