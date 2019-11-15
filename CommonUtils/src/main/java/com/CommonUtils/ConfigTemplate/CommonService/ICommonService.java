package com.CommonUtils.ConfigTemplate.CommonService;

public interface ICommonService
{
	void jtaTransactionForDB();
	void localTransactionByAnnotation() throws Exception;
	void localTransactionForDB();
	void localTransactionForRedis();
	void localTransactionForKafka();
}