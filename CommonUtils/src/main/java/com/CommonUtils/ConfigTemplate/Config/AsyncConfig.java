package com.CommonUtils.ConfigTemplate.Config;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.CommonUtils.Config.ThreadPool.Config.ThreadPoolTaskExecutorConfig;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer
{
	private ThreadPoolTaskExecutor commonThreadPool;
	
	@Bean(name = "commonThreadPool")
	public ThreadPoolTaskExecutor commonThreadPool()
	{
		this.commonThreadPool = ThreadPoolTaskExecutorConfig.getThreadPoolTaskExecutor(false, 15, 30);
		return this.commonThreadPool;
	}
	
	@Override
	public Executor getAsyncExecutor() 
	{ return this.commonThreadPool; }
	
	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() 
	{
		return (Throwable ex, Method method, Object... params) -> 
		log.error(String.format("Async method: %s has uncaught exception, params: %s.", method, JSONUtil.toJsonStr(params)), ex); 
	}
}