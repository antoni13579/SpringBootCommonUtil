package com.CommonUtils.Utils.ThreadUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.CommonUtils.Utils.ArrayUtils.ArrayUtil;
import com.CommonUtils.Utils.CollectionUtils.JavaCollectionsUtil;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ThreadUtil 
{
	private ThreadUtil() {}
	
	public static void submit(final ThreadPoolTaskExecutor threadPool, final Collection<Future<?>> futures, final Runnable ... tasks)
	{
		if (!ArrayUtil.isArrayEmpty(tasks) && (null != futures) && (null != threadPool))
		{
			for (Runnable task : tasks)
			{ futures.add(threadPool.submit(task)); }
		}
	}
	
	@SafeVarargs
	public static <T> void submit(final ThreadPoolTaskExecutor threadPool, final Collection<Future<T>> futures, final Callable<T> ... tasks)
	{
		if (!ArrayUtil.isArrayEmpty(tasks) && (null != futures) && (null != threadPool))
		{
			for (Callable<T> task : tasks)
			{ futures.add(threadPool.submit(task)); }
		}
	}
	
	public static List<Future<?>> submit(final ThreadPoolTaskExecutor threadPool, final Runnable ... tasks)
	{
		List<Future<?>> result = new ArrayList<Future<?>>();
		if (!ArrayUtil.isArrayEmpty(tasks))
		{
			for (Runnable task : tasks)
			{ result.add(threadPool.submit(task)); }
		}
		return result;
	}
	
	@SafeVarargs
	public static <T> List<Future<T>> submit(final ThreadPoolTaskExecutor threadPool, final Callable<T> ... tasks)
	{
		List<Future<T>> result = new ArrayList<Future<T>>();
		if (!ArrayUtil.isArrayEmpty(tasks))
		{
			for (Callable<T> task : tasks)
			{ result.add(threadPool.submit(task)); }
		}
		return result;
	}
	
	public static void waitForFinished(final long sleepTime, final boolean remove, final Future<?> ... futures)
	{ waitForFinished(sleepTime, remove, Arrays.asList(futures)); }
	
	@SneakyThrows(InterruptedException.class)
	public static void waitForFinished(final long sleepTime, final boolean remove, final Collection<Future<?>> futures)
	{
		if (!JavaCollectionsUtil.isCollectionEmpty(futures))
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
	
	/**
	 * 
	 * 判断线程是否结束，true为结束，false为未结束
	 * */
	public static boolean isDone(final Future<?> future)
	{ return (null == future || future.isDone()); }
}