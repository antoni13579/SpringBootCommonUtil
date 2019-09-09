package com.CommonUtils.Utils.DBUtils.RedisUtils;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;

import com.CommonUtils.Utils.DBUtils.Bean.RedisEntry;
import com.CommonUtils.Utils.DataTypeUtils.ArrayUtils.ArrayUtil;
import com.CommonUtils.Utils.DataTypeUtils.BytesUtils.BytesUtil;
import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class RedisUtil 
{
	private RedisUtil() {}
	
	public synchronized static <K, V> boolean setExpireForValue(final RedisTemplate<K, V> redisTemplate, final RedisEntry<K, V> redisEntry, final long timeout, final TimeUnit unit)
	{
		try
		{ return redisTemplate.expire(redisEntry.getKey(), timeout, unit); }
		catch (Exception ex)
		{
			log.error("通过RedisTemplate设置超时时间出现异常，异常原因为：", ex);
			return false;
		}
	}
	
	@SafeVarargs
	public synchronized static <K, V> boolean setExpireForValues(final RedisTemplate<K, V> redisTemplate, final long timeout, final TimeUnit unit, final RedisEntry<K, V> ... redisEntrys)
	{
		Set<Boolean> records = new HashSet<>();
		if (!ArrayUtil.isArrayEmpty(redisEntrys))
		{
			for (RedisEntry<K, V> redisEntry : redisEntrys)
			{
				boolean executeResult = setExpireForValue(redisTemplate, redisEntry, timeout, unit);
				records.add(executeResult);
			}
		}
		
		if (records.size() > 1)
		{ return false; }
		else
		{ return JavaCollectionsUtil.getItem(records, 0); }
	}
	
	public synchronized static <K, V> boolean setExpireForValue(final ReactiveRedisTemplate<K, V> reactiveRedisTemplate, final RedisEntry<K, V> redisEntry, final Duration timeout)
	{
		try
		{ return reactiveRedisTemplate.expire(redisEntry.getKey(), timeout).block(); }
		catch (Exception ex)
		{
			log.error("通过ReactiveRedisTemplate设置超时时间出现异常，异常原因为：", ex);
			return false;
		}
	}
	
	@SafeVarargs
	public synchronized static <K, V> boolean setExpireForValues(final ReactiveRedisTemplate<K, V> reactiveRedisTemplate, final Duration timeout, final RedisEntry<K, V> ... redisEntrys)
	{
		Set<Boolean> records = new HashSet<>();
		if (!ArrayUtil.isArrayEmpty(redisEntrys))
		{
			for (RedisEntry<K, V> redisEntry : redisEntrys)
			{
				boolean executeResult = setExpireForValue(reactiveRedisTemplate, redisEntry, timeout);
				records.add(executeResult);
			}
		}
		
		if (records.size() > 1)
		{ return false; }
		else
		{ return JavaCollectionsUtil.getItem(records, 0); }
	}
	
	@Synchronized
	public static <K, V> void setForValue(final RedisTemplate<K, V> redisTemplate, final RedisEntry<K, V> redisEntry, final long timeout, final TimeUnit unit)
	{
		try
		{
			redisTemplate.opsForValue()
			 			 .set(redisEntry.getKey(), redisEntry.getValue(), timeout, unit);
		}
		catch (Exception ex)
		{ log.error("通过RedisTemplate设置值出现异常，异常原因为：", ex); }
	}
	
	@SafeVarargs
	public synchronized static <K, V> void setForValues(final RedisTemplate<K, V> redisTemplate, final long timeout, final TimeUnit unit, final RedisEntry<K, V> ... redisEntrys)
	{
		if (!ArrayUtil.isArrayEmpty(redisEntrys))
		{
			for (RedisEntry<K, V> redisEntry : redisEntrys)
			{ setForValue(redisTemplate, redisEntry, timeout, unit); }
		}
	}
	
	public synchronized static <K, V> void setForValue(final RedisTemplate<K, V> redisTemplate, final RedisEntry<K, V> redisEntry)
	{
		try
		{
			redisTemplate.opsForValue()
			 			 .set(redisEntry.getKey(), redisEntry.getValue());
		}
		catch (Exception ex)
		{ log.error("通过RedisTemplate设置值出现异常，异常原因为：", ex); }
	}
	
	@SafeVarargs
	public synchronized static <K, V> void setForValues(final RedisTemplate<K, V> redisTemplate, final RedisEntry<K, V> ... redisEntrys)
	{
		if (!ArrayUtil.isArrayEmpty(redisEntrys))
		{
			for (RedisEntry<K, V> redisEntry : redisEntrys)
			{ setForValue(redisTemplate, redisEntry); }
		}
	}
	
	public synchronized static <K, V> boolean setForValue(final ReactiveRedisTemplate<K, V> reactiveRedisTemplate, final RedisEntry<K, V> redisEntry, final Duration timeout)
	{
		boolean result = false;
		
		try
		{
			result = reactiveRedisTemplate.opsForValue()
										  .set(redisEntry.getKey(), redisEntry.getValue(), timeout)
										  .block();
		}
		catch (Exception ex)
		{ log.error("通过ReactiveRedisTemplate设置值出现异常，异常原因为：", ex); }
		
		return result;
	}
	
	@SafeVarargs
	public synchronized static <K, V> boolean setForValues(final ReactiveRedisTemplate<K, V> reactiveRedisTemplate, final Duration timeout, final RedisEntry<K, V> ... redisEntrys)
	{
		Set<Boolean> result = new HashSet<Boolean>();
		if (!ArrayUtil.isArrayEmpty(redisEntrys))
		{
			for (RedisEntry<K, V> redisEntry : redisEntrys)
			{ result.add(setForValue(reactiveRedisTemplate, redisEntry, timeout)); }
		}
		
		if (!JavaCollectionsUtil.isCollectionEmpty(result))
		{
			if (result.size() == 1)
			{ return JavaCollectionsUtil.getItem(result, 0); }
			else
			{ return false; }
		}
		else
		{ return false; }
	}
	
	public synchronized static <K, V> boolean setForValue(final ReactiveRedisTemplate<K, V> reactiveRedisTemplate, final RedisEntry<K, V> redisEntry)
	{
		boolean result = false;
		
		try
		{
			result = reactiveRedisTemplate.opsForValue()
										  .set(redisEntry.getKey(), redisEntry.getValue())
										  .block();
		}
		catch (Exception ex)
		{ log.error("通过ReactiveRedisTemplate设置值出现异常，异常原因为：", ex); }
		
		return result;
	}
	
	@SafeVarargs
	public synchronized static <K, V> boolean setForValues(final ReactiveRedisTemplate<K, V> reactiveRedisTemplate, final RedisEntry<K, V> ... redisEntrys)
	{
		Set<Boolean> result = new HashSet<Boolean>();
		if (!ArrayUtil.isArrayEmpty(redisEntrys))
		{
			for (RedisEntry<K, V> redisEntry : redisEntrys)
			{ result.add(setForValue(reactiveRedisTemplate, redisEntry)); }
		}
		
		if (!JavaCollectionsUtil.isCollectionEmpty(result))
		{
			if (result.size() == 1)
			{ return JavaCollectionsUtil.getItem(result, 0); }
			else
			{ return false; }
		}
		else
		{ return false; }
	}
	
	public synchronized static <K, V> boolean setForValue(final ReactiveRedisConnection reactiveRedisConnection, final RedisEntry<K, V> redisEntry)
	{
		boolean result = false;
		
		try
		{
			result = reactiveRedisConnection.stringCommands()
											.set(ByteBuffer.wrap(BytesUtil.toBytes(redisEntry.getKey())), 
												 ByteBuffer.wrap(BytesUtil.toBytes(redisEntry.getValue())))
											.block();
		}
		catch (Exception ex)
		{ log.error("通过ReactiveRedisConnection设置值出现异常，异常原因为：", ex); }
		
		return result;
	}
	
	@SafeVarargs
	public synchronized static <K, V> boolean setForValues(final ReactiveRedisConnection reactiveRedisConnection, final RedisEntry<K, V> ... redisEntrys)
	{
		Set<Boolean> result = new HashSet<Boolean>();
		if (!ArrayUtil.isArrayEmpty(redisEntrys))
		{
			for (RedisEntry<K, V> redisEntry : redisEntrys)
			{ result.add(setForValue(reactiveRedisConnection, redisEntry)); }
		}
		
		if (!JavaCollectionsUtil.isCollectionEmpty(result))
		{
			if (result.size() == 1)
			{ return JavaCollectionsUtil.getItem(result, 0); }
			else
			{ return false; }
		}
		else
		{ return false; }
	}
	
	public static <K, V> V getForValue(final RedisTemplate<K, V> redisTemplate, final K key)
	{
		V result = null;
		
		try
		{
			result = redisTemplate.opsForValue()
								  .get(key);
		}
		catch (Exception ex)
		{ log.error("通过RedisTemplate获取值出现异常，异常原因为：", ex); }
		
		return result;
	}
	
	@SafeVarargs
	public static <K, V> Collection<V> getForValues(final RedisTemplate<K, V> redisTemplate, final K ... keys)
	{
		Collection<V> result = new ArrayList<>();
		if (!ArrayUtil.isArrayEmpty(keys))
		{
			for (K key : keys)
			{ result.add(getForValue(redisTemplate, key)); }
		}
		return result;
	}
	
	public static <K, V> V getForValue(final ReactiveRedisTemplate<K, V> reactiveRedisTemplate, final K key)
	{
		V result = null;
		
		try
		{
			result = reactiveRedisTemplate.opsForValue()
										  .get(key)
										  .block();
		}
		catch (Exception ex)
		{ log.error("通过ReactiveRedisTemplate获取值出现异常，异常原因为：", ex); }
		
		return result;
	}
	
	@SafeVarargs
	public static <K, V> Collection<V> getForValues(final ReactiveRedisTemplate<K, V> reactiveRedisTemplate, final K ... keys)
	{
		Collection<V> result = new ArrayList<>();
		if (!ArrayUtil.isArrayEmpty(keys))
		{
			for (K key : keys)
			{ result.add(getForValue(reactiveRedisTemplate, key)); }
		}
		return result;
	}
	
	public static <K, V> V getForValue(final ReactiveRedisConnection reactiveRedisConnection, final K key)
	{
		V result = null;
		
		try
		{
			ByteBuffer byteBuffer = reactiveRedisConnection.stringCommands()
					   									   .get(ByteBuffer.wrap(BytesUtil.toBytes(key)))
					   									   .block();
			result = BytesUtil.fromBytes(byteBuffer.array());
		}
		catch (Exception ex)
		{ log.error("通过ReactiveRedisConnection获取值出现异常，异常原因为：", ex); }
		
		return result;
	}
	
	@SafeVarargs
	public static <K, V> Collection<V> getForValues(final ReactiveRedisConnection reactiveRedisConnection, final K ... keys)
	{
		Collection<V> result = new ArrayList<>();
		if (!ArrayUtil.isArrayEmpty(keys))
		{
			for (K key : keys)
			{ result.add(getForValue(reactiveRedisConnection, key)); }
		}
		return result;
	}
	
	@SafeVarargs
	public synchronized static <K, V> long deleteForValues(final RedisTemplate<K, V> redisTemplate, final K ... keys)
	{
		long result = 0;
		
		try
		{ result = redisTemplate.delete(Arrays.asList(keys)); }
		catch (Exception ex)
		{ log.error("通过RedisTemplate删除信息出现异常，异常原因为：", ex); }
		
		return result;
	}
	
	@SafeVarargs
	public synchronized static <K, V> long deleteForValues(final ReactiveRedisTemplate<K, V> reactiveRedisTemplate, final K... keys)
	{
		long result = 0;
		
		try
		{
			result = reactiveRedisTemplate.delete(keys)
										  .block();
		}
		catch (Exception ex)
		{ log.error("通过ReactiveRedisTemplate删除信息出现异常，异常原因为：", ex); }
		
		return result;
	}
}