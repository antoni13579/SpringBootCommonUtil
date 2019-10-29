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
	
	/**
	 * @Service
@Slf4j
public class AsyncTask {

    @Async
    public Future<String> task1() throws InterruptedException {
        log.info("task1 start");
        Thread.sleep(5000L);
        log.info("task1 end");
        return new AsyncResult<>("task1 result");
    }

    @Async
    public Future<Integer> task2() throws InterruptedException {
        Integer abc = 1;
        log.info("task2 start");
        Thread.sleep(10000L);
        log.info("task2 end");
        return new AsyncResult<>(abc);
    }
}
	 * */
}