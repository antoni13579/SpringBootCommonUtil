package com.CommonUtils.Config.SQL.Mybatis.Config;

import java.util.Properties;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.github.pagehelper.PageInterceptor;

public final class PageConfig 
{	
	private PageConfig() {}
	
	public static PaginationInterceptor getBaomidouPaginationInterceptor()
	{ return PaginationInterceptorSingletonContainer.instance; }
	
	public static PageInterceptor getGithubPageInterceptor()
	{ return PageInterceptorSingletonContainer.instance; }
	
	private static class PaginationInterceptorSingletonContainer
	{ private static PaginationInterceptor instance = new PaginationInterceptor(); }
	
	private static class PageInterceptorSingletonContainer
	{
		private static PageInterceptor instance = new PageInterceptor();
		static
		{
			Properties properties = new Properties();
			properties.setProperty("reasonable", "true");
			properties.setProperty("supportMethodsArguments", "true");
			properties.setProperty("returnPageInfo", "check");
			properties.setProperty("params", "count=countSql");
			properties.setProperty("pageSizeZero", "true");
			instance.setProperties(properties);
		}
	}
}