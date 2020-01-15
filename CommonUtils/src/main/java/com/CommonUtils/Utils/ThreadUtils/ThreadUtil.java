package com.CommonUtils.Utils.ThreadUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Supplier;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Replacer;
import cn.hutool.core.util.ArrayUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ThreadUtil 
{
	private ThreadUtil() {}
	
	private static final String COMPLETABLE_FUTURE_ERROR_DESC = "使用CompletableFuture出现异常，异常原因为：";
	
	/**建议使用cn.hutool.core.thread.ThreadUtil.execAsync
	 * @deprecated
	 * */ 
	@Deprecated(since="建议使用cn.hutool.core.thread.ThreadUtil.execAsync")
	public static void submit(final ThreadPoolTaskExecutor threadPool, final Collection<Future<?>> futures, final Runnable ... tasks)
	{
		if (!ArrayUtil.isEmpty(tasks) && (null != futures) && (null != threadPool))
		{
			for (Runnable task : tasks)
			{ futures.add(threadPool.submit(task)); }
		}
	}
	
	/**建议使用cn.hutool.core.thread.ThreadUtil.execAsync
	 * @deprecated
	 * */ 
	@Deprecated(since="建议使用cn.hutool.core.thread.ThreadUtil.execAsync")
	@SafeVarargs
	public static <T> void submit(final ThreadPoolTaskExecutor threadPool, final Collection<Future<T>> futures, final Callable<T> ... tasks)
	{
		if (!ArrayUtil.isEmpty(tasks) && (null != futures) && (null != threadPool))
		{
			for (Callable<T> task : tasks)
			{ futures.add(threadPool.submit(task)); }
		}
	}
	
	/**建议使用cn.hutool.core.thread.ThreadUtil.execAsync
	 * @deprecated
	 * */ 
	@Deprecated(since="建议使用cn.hutool.core.thread.ThreadUtil.execAsync")
	public static List<Future<?>> submit(final ThreadPoolTaskExecutor threadPool, final Runnable ... tasks)
	{
		List<Future<?>> result = new ArrayList<>();
		if (!ArrayUtil.isEmpty(tasks))
		{
			for (Runnable task : tasks)
			{ result.add(threadPool.submit(task)); }
		}
		return result;
	}
	
	/**建议使用cn.hutool.core.thread.ThreadUtil.execAsync
	 * @deprecated
	 * */ 
	@Deprecated(since="建议使用cn.hutool.core.thread.ThreadUtil.execAsync")
	@SafeVarargs
	public static <T> List<Future<T>> submit(final ThreadPoolTaskExecutor threadPool, final Callable<T> ... tasks)
	{
		List<Future<T>> result = new ArrayList<>();
		if (!ArrayUtil.isEmpty(tasks))
		{
			for (Callable<T> task : tasks)
			{ result.add(threadPool.submit(task)); }
		}
		return result;
	}
	
	public static void waitForFinished(final long sleepTime, final boolean remove, final Future<?> ... futures)
	{ waitForFinished(sleepTime, remove, CollUtil.newArrayList(futures)); }
	
	@SneakyThrows(InterruptedException.class)
	public static void waitForFinished(final long sleepTime, final boolean remove, final Collection<Future<?>> futures)
	{
		if (!CollUtil.isEmpty(futures))
		{
			Collection<Future<?>> removeFutures = null;
			if (remove) { removeFutures = new ArrayList<>(); }
			
			for (Future<?> future : futures)
			{
				while (!future.isDone())
				{
					log.info("线程等待中。。。。需等待{}毫秒", sleepTime);
					Thread.sleep(sleepTime);
				}
				
				if (remove) { removeFutures.add(future); }
			}
			
			if (remove) { futures.removeAll(removeFutures); }
		}
	}
	
	/**建议使用cn.hutool.core.thread.ThreadUtil.sleep相关函数
	 * @deprecated
	 * */ 
	@Deprecated(since="建议使用cn.hutool.core.thread.ThreadUtil.sleep相关函数")
	public static void sleep(final long millis)
	{
		try
		{ Thread.sleep(millis); }
		catch (Exception ex)
		{ log.error("线程睡眠出现异常，异常原因为：", ex); }
	}
	
	/**
	 * 
	 * 判断线程是否结束，true为结束，false为未结束
	 * */
	public static boolean isDone(final Future<?> future)
	{ return (null == future || future.isDone()); }
	
	/**
	 * 
	 * CountDownLatch通用处理类，在CountDownLatchProcess处理接口里，要记得调用countDownLatch.countDown()，否则进程会卡着不动，如count参数设置为10，countDownLatch.countDown()就需要调用10次，
	 * @return 返回Map集合，用于存放多个返回值的情况
	 * @param count CountDownLatch的次数
	 * @param countDownLatchProcess CountDownLatch处理接口，可以使用Lambda表达式
	 * @param params 用于传入多个需处理的变量
	 * */
	public static Map<String, Object> countDownLatchProcessUtil(final int count, final CountDownLatchProcess<Map<String, Object>> countDownLatchProcess, final Map<String, Object> params)
	{
		Map<String, Object> result = null;
		
		try 
		{
			CountDownLatch countDownLatch = new CountDownLatch(count);
			result = countDownLatchProcess.process(countDownLatch, params);
			countDownLatch.await();
		} 
		catch (Exception e) 
		{
			log.error("使用CountDownLatch出现异常，异常原因为：", e);
			result = Collections.emptyMap();
		}
		
		return result;
	}
	
	/**
	 * 
	 * CountDownLatch通用处理类，在CountDownLatchProcess处理接口里，要记得调用countDownLatch.countDown()，否则进程会卡着不动，如count参数设置为10，countDownLatch.countDown()就需要调用10次，
	 * @return 返回Map集合，用于存放多个返回值的情况
	 * @param count CountDownLatch的次数
	 * @param countDownLatchProcess CountDownLatch处理接口，可以使用Lambda表达式
	 * @param params 用于传入多个需处理的变量
	 * @param timeout 超时时间
	 * @param unit 时间单位
	 * */
	public static Map<String, Object> countDownLatchProcessUtil(final int count, final CountDownLatchProcess<Map<String, Object>> countDownLatchProcess, final long timeout, final TimeUnit unit, final Map<String, Object> params)
	{
		Map<String, Object> result = null;
		
		try 
		{
			CountDownLatch countDownLatch = new CountDownLatch(count);
			result = countDownLatchProcess.process(countDownLatch, params);
			boolean awaitResult = countDownLatch.await(timeout, unit);
			result.put("AWAIT_RESULT", awaitResult);
		} 
		catch (Exception e) 
		{
			log.error("使用CountDownLatch出现异常，异常原因为：", e);
			result = Collections.emptyMap();
		}
		
		return result;
	}
	
	/**
	 * 将CountDownLatch全部进行countDown操作
	 * */
	public static boolean countDownAll(final CountDownLatch ... countDownLatchs)
	{
		return JavaCollectionsUtil.collectionProcessor
		(
				CollUtil.newArrayList(countDownLatchs), 
				(countDownLatch, indx, length) -> 
				{
					while (null != countDownLatch && countDownLatch.getCount() != 0L)
					{ countDownLatch.countDown(); } 
				}
		);
	}
	
	public static boolean completableFutureProcessAllOf(final Collection<CompletableFuture<Void>> futures, final long timeout, final TimeUnit unit)
	{
		boolean result;
		try
		{
			CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).get(timeout, unit);
			result = true;
		}
		catch (Exception ex)
		{
			log.error(COMPLETABLE_FUTURE_ERROR_DESC, ex);
			result = false;
		}
		return result;
	}
	
	public static boolean completableFutureProcessAllOf(final Collection<CompletableFuture<Void>> futures)
	{
		boolean result;
		try
		{
			CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).get();
			result = true;
		}
		catch (Exception ex)
		{
			log.error(COMPLETABLE_FUTURE_ERROR_DESC, ex);
			result = false;
		}
		return result;
	}
	
	public static Optional<Object> completableFutureProcessAnyOf(final Collection<CompletableFuture<Object>> futures)
	{
		Object result;
		try
		{ result = CompletableFuture.anyOf(futures.toArray(new CompletableFuture[futures.size()])).get(); }
		catch (Exception ex)
		{
			log.error(COMPLETABLE_FUTURE_ERROR_DESC, ex);
			result = null;
		}
		return Optional.ofNullable(result);
	}
	
	public static Optional<Object> completableFutureProcessAnyOf(final Collection<CompletableFuture<Object>> futures, final long timeout, final TimeUnit unit)
	{
		Object result;
		try
		{ result = CompletableFuture.anyOf(futures.toArray(new CompletableFuture[futures.size()])).get(timeout, unit); }
		catch (Exception ex)
		{
			log.error(COMPLETABLE_FUTURE_ERROR_DESC, ex);
			result = null;
		}
		return Optional.ofNullable(result);
	}
	
	public static CompletableFuture<Void> getCompletableFuture(final Executor executor, final Runnable runnable)
	{ return CompletableFuture.runAsync(runnable, executor); }
	
	public static <T> CompletableFuture<T> getCompletableFuture(final Executor executor, final Supplier<T> supplier)
	{ return CompletableFuture.supplyAsync(supplier, executor); }
	
	public static Map<String, Object> stampedLockProcessWithWriteMode(final Replacer<Map<String, Object>> stampedLockProcess, final Map<String, Object> params)
	{
		StampedLock sl = new StampedLock();
		
		// 获取写锁，返回一个版本号（戳）
        long stamp = sl.writeLock();
        
        Map<String, Object>  result = stampedLockProcess.replace(params);
        
        // 释放写锁，需要传入上面获取的版本号
        sl.unlockWrite(stamp);
        return result;
	}
	
	public static Map<String, Object> stampedLockProcessWithOptimisticReadMode(final Replacer<Map<String, Object>> stampedLockProcess, final Map<String, Object> params)
	{
		StampedLock sl = new StampedLock();
		
		// 乐观读
        long stamp = sl.tryOptimisticRead();
        Map<String, Object> result = stampedLockProcess.replace(params);
        
        // 验证版本号是否有变化
        if (!sl.validate(stamp))
        {
        	// 版本号变了，乐观读转悲观读
        	stamp = sl.readLock();
        	
        	result = stampedLockProcess.replace(params);
        	
        	// 释放读锁，需要传入上面获取的版本号
        	sl.unlockRead(stamp);
        }
        
        return result;
	}
	
    /**
     * @param flag 这个需要填写布尔表达式，如修改x，y变量，但是只有x==0 && y==0才能修改，那么boolean flag = (x == 0 && y == 0)
     * */
	public static Map<String, Object> stampedLockProcessWithReadMode(final Replacer<Map<String, Object>> stampedLockProcess, final boolean flag, final Map<String, Object> params)
	{
		StampedLock sl = new StampedLock();
		
		// 获取悲观读锁
        long stamp = sl.readLock();
        
        Map<String, Object> result = null;
        
        while (flag)
        {
        	// 转为写锁
            long ws = sl.tryConvertToWriteLock(stamp);
            // 转换成功
            if (ws != 0L) 
            {
                stamp = ws;
                result = stampedLockProcess.replace(params);
                break;
            }
            else 
            {
                // 转换失败
                sl.unlockRead(stamp);
                // 获取写锁
                stamp = sl.writeLock();
            }
        }
        
        // 释放锁
        sl.unlock(stamp);
        
        return result;
	}
}