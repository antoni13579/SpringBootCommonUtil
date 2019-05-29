package com.CommonUtils.Mybatis.Config;

import com.CommonUtils.Utils.CollectionUtils.CustomCollections.Properties;
import com.github.pagehelper.PageInterceptor;

public final class PageConfig 
{
	private static PageInterceptor INSTANCE = null;
	
	private PageConfig() {}
	
	public static PageInterceptor getInstance()
	{
		if (null == INSTANCE)
		{
			synchronized (PageConfig.class)
			{
				if (null == INSTANCE)
				{
					INSTANCE = new PageInterceptor();			        
			        INSTANCE.setProperties
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
		
		return INSTANCE;
	}
}