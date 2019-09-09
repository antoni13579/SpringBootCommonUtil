package com.CommonUtils.Config.ThreadPool;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

/**使用此模板类，记得要进行create bean动作，而且，建议加入@EnableAsync注解，开启异步任务模式
 * 
 * 开启异步任务模式后，Spring提供了注解方式来方便我们使用线程池，只需要在要异步处理的方法上加 @Async("你配置的线程池名字")就可以了,注意这个类需要被spring扫描并纳入管理，所以要加@Service、@Component等注解
 * 
 * 注： @Async所修饰的函数不要定义为static类型，这样异步调用不会生效。
 * 
 * 
 * */
public final class ThreadPoolTaskExecutorConfig 
{
	private ThreadPoolTaskExecutorConfig() {}
	
	public static ThreadPoolTaskExecutor getThreadPoolTaskExecutor(final boolean initialize, final int minPoolSize, final int maxPoolSize)
	{
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(minPoolSize);
		threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
		threadPoolTaskExecutor.setQueueCapacity(1000);
		threadPoolTaskExecutor.setKeepAliveSeconds(300);
		threadPoolTaskExecutor.setRejectedExecutionHandler(new CallerRunsPolicy());
		
		if (initialize){ threadPoolTaskExecutor.initialize(); }
		
		return threadPoolTaskExecutor;
	}
}