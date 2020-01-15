package com.CommonUtils.Utils.KettleUtils;

import java.util.Map;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class TransUtil 
{
	private TransUtil() {}
	
	public static void callNativeTrans(final Map<String, String> params, final String transPathName) throws TransUtilException, KettleException
    {
    	if (KettleUtil.init())
    	{ 
            TransMeta transMeta = new TransMeta(transPathName);
            Trans trans = new Trans(transMeta);
        	String isWithParams;
        	if (null != params && !params.isEmpty())
        	{ 
        		isWithParams = "有";
        		for (Map.Entry<String, String> entry : params.entrySet())
                { trans.setParameterValue(entry.getKey(), entry.getValue()); }
        	}
        	else
        	{ isWithParams = "无"; }
        	
        	log.info("{} Trans文件（{}参数）运行开始", transPathName, isWithParams);

        	trans.setLogLevel(LogLevel.BASIC);
            trans.execute(null);
            trans.waitUntilFinished();
             
            if(trans.getErrors() > 0)
            {
            	StringBuilder sb = new StringBuilder();
            	sb.append("Trans文件：");
            	sb.append(transPathName);
            	sb.append("；传输过程中发生异常");
            	throw new TransUtilException(sb.toString()); 
            }
            else
            { log.info("{} Trans文件（{}参数）运行结束", transPathName, isWithParams); }
    	}
    	else
    	{ log.warn("因Kettle运行环境初始化失败，所有步骤不会执行"); }
    }
	
	public static void callNativeTrans(final String transPathName) throws TransUtilException, KettleException
    { callNativeTrans(null, transPathName); }
	
	private static class TransUtilException extends Exception
	{
		private static final long serialVersionUID = -536062342633923969L;

		private TransUtilException(final String message)
		{ super(message); }
	}
}