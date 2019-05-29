package com.CommonUtils.Mybatis.Config;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;

import com.CommonUtils.Utils.ArrayUtils.ArrayUtil;

public final class MybatisBaseConfig 
{	
	private MybatisBaseConfig() {}
	
	/**默认LocalCacheScope为SESSION，但如果是服务集群，建议把LocalCacheScope设置为STATEMENT；
	 * useColumnLabel要看情况，使用Oracle这些数据库是没有问题， 但使用Teradata，如果设置为True，就会有坑，需要设置为false才行
	 * ExecutorType设置为Batch，使用批处理DML
	 * */
	@SafeVarargs
	public static Configuration getInstance(final Class<? extends Log> ... logs) throws Exception
	{
		Configuration instance = new Configuration();
		instance.setCacheEnabled(true);
		instance.setMultipleResultSetsEnabled(true);
		
		instance.setLocalCacheScope(LocalCacheScope.STATEMENT);
		
		instance.setUseColumnLabel(false);
		
		instance.setUseGeneratedKeys(true);
		instance.setDefaultExecutorType(ExecutorType.BATCH);
		instance.setDefaultStatementTimeout(25000);
		
		if (!ArrayUtil.isArrayEmpty(logs))
		{
			if (logs.length == 1)
			{ instance.setLogImpl(logs[0]); }
			else
			{ throw new Exception("MybatisBaseConfig配置不正确，指定MyBatis执行输出日志的类只能是一个！！"); }
		}
		
		return instance;
	}
}