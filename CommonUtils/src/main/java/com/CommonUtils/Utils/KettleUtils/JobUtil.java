package com.CommonUtils.Utils.KettleUtils;

import java.util.Map;

import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class JobUtil 
{
	private JobUtil() {}
	
	public static void callNativeJob(final Map<String, String> params, final String jobPathName) throws JobUtilException, KettleXMLException, UnknownParamException
    {
    	if (KettleUtil.init())
    	{            
            String isWithParams;
            JobMeta jobMeta = new JobMeta(jobPathName, null);
            Job job = new Job(null, jobMeta);
        	if (null != params && !params.isEmpty())
        	{ 
        		isWithParams = "有";
        		for (Map.Entry<String, String> entry : params.entrySet())
                { job.setParameterValue(entry.getKey(), entry.getValue()); }
        	}
        	else
        	{ isWithParams = "无"; }
        	
        	log.info("{} Job文件（{}参数）运行开始", jobPathName, isWithParams);
            
        	job.setLogLevel(LogLevel.BASIC);
            job.start();
            job.waitUntilFinished();
            
            if (job.getErrors() > 0) 
            {
            	StringBuilder sb = new StringBuilder();
            	sb.append("Job文件：");
            	sb.append(jobPathName);
            	sb.append("；执行job发生异常");
            	throw new JobUtilException(sb.toString()); 
            }
            else
            { log.info("{} Job文件（{}参数）运行结束", jobPathName, isWithParams); }
    	}
    	else
    	{ log.warn("因Kettle运行环境初始化失败，所有步骤不会执行"); }
    }
	
	public static void callNativeJob(final String jobPathName) throws JobUtilException, UnknownParamException, KettleXMLException
	{ callNativeJob(null, jobPathName); }
	
	private static class JobUtilException extends Exception
	{
		private static final long serialVersionUID = 5519003980807534714L;

		private JobUtilException(final String message)
		{ super(message); }
	}
}