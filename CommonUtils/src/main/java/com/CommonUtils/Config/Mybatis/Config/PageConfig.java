package com.CommonUtils.Config.Mybatis.Config;

import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.CustomCollections.Properties;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.github.pagehelper.PageInterceptor;

public final class PageConfig 
{
	private static PageInterceptor GITHUB_PAGE_INTERCEPTOR = null;
	private static PaginationInterceptor BAOMIDOU_PAGINATION_INTERCEPTOR = null;
	
	private PageConfig() {}
	
	public static PaginationInterceptor getBaomidouPaginationInterceptor()
	{
		if (null == BAOMIDOU_PAGINATION_INTERCEPTOR)
		{
			synchronized (PageConfig.class)
			{
				if (null == BAOMIDOU_PAGINATION_INTERCEPTOR)
				{ BAOMIDOU_PAGINATION_INTERCEPTOR = new PaginationInterceptor(); }
			}
		}
		
		return BAOMIDOU_PAGINATION_INTERCEPTOR;
	}
	
	public static PageInterceptor getGithubPageInterceptor()
	{
		if (null == GITHUB_PAGE_INTERCEPTOR)
		{
			synchronized (PageConfig.class)
			{
				if (null == GITHUB_PAGE_INTERCEPTOR)
				{
					GITHUB_PAGE_INTERCEPTOR = new PageInterceptor();			        
					GITHUB_PAGE_INTERCEPTOR.setProperties
			        (
			        		new Properties().setProperty("reasonable", "true")
			        						.setProperty("supportMethodsArguments", "true")
			        						.setProperty("returnPageInfo", "check")
			        						.setProperty("params", "count=countSql")
			        						.setProperty("pageSizeZero", "true")
			        						.getProperties()
			        );
				}
			}
		}
		
		return GITHUB_PAGE_INTERCEPTOR;
	}
}