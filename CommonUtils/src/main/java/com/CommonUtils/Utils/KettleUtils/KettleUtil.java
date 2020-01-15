package com.CommonUtils.Utils.KettleUtils;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.util.EnvUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class KettleUtil 
{
	private KettleUtil() {}
	
	public static void release()
	{
		try
		{
			if(KettleEnvironment.isInitialized())
			{
				KettleEnvironment.shutdown();
				log.info("释放Kettle运行环境资源完毕");
			}
		}
		catch (Exception ex)
		{ log.error("释放Kettle运行环境资源失败，异常原因为：", ex); }
	}
	
	public static boolean init()
	{
		try 
		{
			if(!KettleEnvironment.isInitialized())
			{
				KettleEnvironment.init();
				EnvUtil.environmentInit();
				log.info("Kettle运行环境初始化完毕");
			}
			
			return true;
		} 
		catch (Exception e) 
		{
			log.error("Kettle运行环境初始化失败，后续所有操作禁止执行，异常原因为{}", e);
			return false;
		}
	}
}