package com.CommonUtils.Config.MiddleWare.GracefulShutdowns;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GracefulShutdownTomcat implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent>
{
	private volatile Connector connector;
	private final int waitTime = 30;
	
	@Override
	public void onApplicationEvent(ContextClosedEvent event) 
	{
		if (null != this.connector)
		{
			this.connector.pause();
			Executor executor = this.connector.getProtocolHandler().getExecutor();
			if (executor instanceof ThreadPoolExecutor) 
			{
	            try 
	            {
	            	log.info("等待Tomcat容器关闭中。。。。。。。。。。。。。。。。。。");
	                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
	                threadPoolExecutor.shutdown();
	                if (!threadPoolExecutor.awaitTermination(waitTime, TimeUnit.SECONDS)) 
	                { log.warn("已等待{}秒了，不能再像绅士一样等待了，开启暴力模式，直接关闭Tomcat", this.waitTime); }
	            } 
	            catch (InterruptedException ex) 
	            { Thread.currentThread().interrupt(); }
	        }
		}
	}

	@Override
	public void customize(Connector connector) 
	{ this.connector = connector; }
}