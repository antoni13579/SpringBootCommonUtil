package com.CommonUtils.Config.SQL.Mybatis.Config;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.config.GlobalConfig.DbConfig;

import cn.hutool.core.util.ArrayUtil;

public final class MybatisBaseConfig 
{	
	private MybatisBaseConfig() {}
	
	/**默认LocalCacheScope为SESSION，但如果是服务集群，建议把LocalCacheScope设置为STATEMENT；
	 * useColumnLabel要看情况，使用Oracle这些数据库是没有问题， 但使用Teradata，如果设置为True，就会有坑，需要设置为false才行
	 * ExecutorType设置为Batch，使用批处理DML
	 * @throws Exception 
	 * */
	@SafeVarargs
	private static Configuration getConfiguration(final Configuration instance, final Class<? extends Log> ... logs) throws MybatisBaseConfigException
	{
		instance.setCacheEnabled(true);
		instance.setMultipleResultSetsEnabled(true);
		instance.setLocalCacheScope(LocalCacheScope.STATEMENT);
		instance.setUseColumnLabel(false);
		instance.setUseGeneratedKeys(true);
		instance.setDefaultExecutorType(ExecutorType.BATCH);
		instance.setDefaultStatementTimeout(25000);
		
		if (!ArrayUtil.isEmpty(logs))
		{
			if (logs.length == 1)
			{ instance.setLogImpl(logs[0]); }
			else
			{ throw new MybatisBaseConfigException("MybatisBaseConfig配置不正确，指定MyBatis执行输出日志的类只能是一个！！"); }
		}
		
		return instance;
	}
	
	@SafeVarargs
	public static Configuration getConfigurationForMyBatis(final Class<? extends Log> ... logs) throws MybatisBaseConfigException
	{ return getConfiguration(new Configuration(), logs); }
	
	@SafeVarargs
	public static MybatisConfiguration getConfigurationForMyBatisPlus(final IdType idType, 
																	  final long workerId, 
																	  final long datacenterId, 
																	  final Class<? extends Log> ... logs) throws MybatisBaseConfigException
	{
		Configuration instance = getConfiguration(new MybatisConfiguration(), logs);
		((MybatisConfiguration)instance).setGlobalConfig(new GlobalConfig().setBanner(false).setDbConfig(new DbConfig().setIdType(idType)).setWorkerId(workerId).setDatacenterId(datacenterId));
		return ((MybatisConfiguration)instance);
	}
	
	@SafeVarargs
	public static MybatisConfiguration getConfigurationForMyBatisPlus(final IdType idType, 
																	  final Class<? extends Log> ... logs) throws MybatisBaseConfigException
	{
		Configuration instance = getConfiguration(new MybatisConfiguration(), logs);
		((MybatisConfiguration)instance).setGlobalConfig(new GlobalConfig().setBanner(false).setDbConfig(new DbConfig().setIdType(idType)));
		return ((MybatisConfiguration)instance);
	}
	
	private static class MybatisBaseConfigException extends Exception
	{
		private static final long serialVersionUID = 8919408853751166402L;

		private MybatisBaseConfigException(final String message)
		{ super(message); }
	}
}