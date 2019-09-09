package com.CommonUtils.Config.Mybatis.Config;

import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;

public final class PerformanceInterceptorConfig 
{
	private static PerformanceInterceptor INSTANCE = null;
	
	private PerformanceInterceptorConfig() {}
	
	public static PerformanceInterceptor getInstance()
	{
		if (null == INSTANCE)
		{
			synchronized (PerformanceInterceptorConfig.class)
			{
				if (null == INSTANCE)
				{ INSTANCE = new PerformanceInterceptor(); }
			}
		}
		
		return INSTANCE;
	}
}