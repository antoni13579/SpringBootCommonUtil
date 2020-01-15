package com.CommonUtils.Config.SQL.Mybatis.Log.Impl;

import org.apache.ibatis.logging.Log;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyBatisSqlLogBaseImpl implements Log
{
	public MyBatisSqlLogBaseImpl(String clazz) {}
	
	private static final String MY_BATIS_DEFAULT_DESC = "MyBatis相关信息为：{}";
	
	@Override
	public boolean isDebugEnabled() 
	{ return true; }
	
	@Override
	public boolean isTraceEnabled() 
	{ return true; }

	@Override
	public void error(String s, Throwable e) 
	{ log.error("MyBatis相关信息为：{}，异常为：", s, e); }

	@Override
	public void error(String s) 
	{ log.error(MY_BATIS_DEFAULT_DESC, s); }

	@Override
	public void debug(String s) 
	{ log.info(MY_BATIS_DEFAULT_DESC, s); }

	//这里的话，如果需要，就把trace改为info输出，就可以输出SQL执行后的结果
	@Override
	public void trace(String s) 
	{ log.trace(MY_BATIS_DEFAULT_DESC, s); }

	@Override
	public void warn(String s) 
	{ log.warn(MY_BATIS_DEFAULT_DESC, s); }
}