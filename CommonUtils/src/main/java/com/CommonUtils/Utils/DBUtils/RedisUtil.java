package com.CommonUtils.Utils.DBUtils;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisOperations;

import com.CommonUtils.Utils.DBUtils.Bean.RedisEntry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public final class RedisUtil 
{
	private RedisUtil() {}
	
	public static <K, V> boolean setExpireForValue(final RedisOperations<K, V> redisOperations, final RedisEntry<K, V> redisEntry, final long timeout, final TimeUnit unit)
	{
		try
		{
			Boolean result = redisOperations.expire(redisEntry.getKey(), timeout, unit);
			return Optional.ofNullable(result).orElse(false);
		}
		catch (Exception ex)
		{
			log.error("通过RedisTemplate设置超时时间出现异常，异常原因为：", ex);
			return false;
		}
	}
	
	@SafeVarargs
	public static <K, V> boolean setExpireForValues(final RedisOperations<K, V> redisOperations, final long timeout, final TimeUnit unit, final RedisEntry<K, V> ... redisEntrys)
	{
		Set<Boolean> records = new HashSet<>();
		if (!ArrayUtil.isEmpty(redisEntrys))
		{
			for (RedisEntry<K, V> redisEntry : redisEntrys)
			{
				boolean executeResult = setExpireForValue(redisOperations, redisEntry, timeout, unit);
				records.add(executeResult);
			}
		}
		
		if (records.size() > 1)
		{ return false; }
		else
		{ return CollUtil.get(records, 0); }
	}
	
	public static <K, V> Mono<Boolean> setExpireForValue(final ReactiveRedisTemplate<K, V> reactiveRedisTemplate, final RedisEntry<K, V> redisEntry, final Duration timeout)
	{
		try
		{ return reactiveRedisTemplate.expire(redisEntry.getKey(), timeout); }
		catch (Exception ex)
		{
			log.error("通过ReactiveRedisTemplate设置超时时间出现异常，异常原因为：", ex);
			return Mono.just(false);
		}
	}
	
	@SafeVarargs
	public static <K, V> Mono<Boolean> setExpireForValues(final ReactiveRedisTemplate<K, V> reactiveRedisTemplate, final Duration timeout, final RedisEntry<K, V> ... redisEntrys)
	{
		Set<Mono<Boolean>> records = new HashSet<>();
		if (!ArrayUtil.isEmpty(redisEntrys))
		{
			for (RedisEntry<K, V> redisEntry : redisEntrys)
			{
				Mono<Boolean> executeResult = setExpireForValue(reactiveRedisTemplate, redisEntry, timeout);
				records.add(executeResult);
			}
		}
		
		if (records.size() > 1)
		{ return Mono.just(false); }
		else
		{ return CollUtil.get(records, 0); }
	}
	
	@Synchronized
	public static <K, V> void setForValue(final RedisOperations<K, V> redisOperations, final RedisEntry<K, V> redisEntry, final long timeout, final TimeUnit unit)
	{
		try
		{
			redisOperations.opsForValue()
						   .set(redisEntry.getKey(), redisEntry.getValue(), timeout, unit);
		}
		catch (Exception ex)
		{ log.error("通过RedisTemplate设置值出现异常，异常原因为：", ex); }
	}
	
	@SafeVarargs
	public static <K, V> void setForValues(final RedisOperations<K, V> redisOperations, final long timeout, final TimeUnit unit, final RedisEntry<K, V> ... redisEntrys)
	{
		if (!ArrayUtil.isEmpty(redisEntrys))
		{
			for (RedisEntry<K, V> redisEntry : redisEntrys)
			{ setForValue(redisOperations, redisEntry, timeout, unit); }
		}
	}
	
	public static <K, V> void setForValue(final RedisOperations<K, V> redisOperations, final RedisEntry<K, V> redisEntry)
	{
		try
		{
			redisOperations.opsForValue()
			 			   .set(redisEntry.getKey(), redisEntry.getValue());
		}
		catch (Exception ex)
		{ log.error("通过RedisTemplate设置值出现异常，异常原因为：", ex); }
	}
	
	@SafeVarargs
	public static <K, V> void setForValues(final RedisOperations<K, V> redisOperations, final RedisEntry<K, V> ... redisEntrys)
	{
		if (!ArrayUtil.isEmpty(redisEntrys))
		{
			for (RedisEntry<K, V> redisEntry : redisEntrys)
			{ setForValue(redisOperations, redisEntry); }
		}
	}
	
	public static <K, V> Mono<Boolean> setForValue(final ReactiveRedisTemplate<K, V> reactiveRedisTemplate, final RedisEntry<K, V> redisEntry, final Duration timeout)
	{
		Mono<Boolean> result = Mono.just(false);
		
		try
		{
			result = reactiveRedisTemplate.opsForValue()
										  .set(redisEntry.getKey(), redisEntry.getValue(), timeout);
		}
		catch (Exception ex)
		{ log.error("通过ReactiveRedisTemplate设置值出现异常，异常原因为：", ex); }
		
		return result;
	}
	
	@SafeVarargs
	public static <K, V> Mono<Boolean> setForValues(final ReactiveRedisTemplate<K, V> reactiveRedisTemplate, final Duration timeout, final RedisEntry<K, V> ... redisEntrys)
	{
		Set<Mono<Boolean>> result = new HashSet<>();
		if (!ArrayUtil.isEmpty(redisEntrys))
		{
			for (RedisEntry<K, V> redisEntry : redisEntrys)
			{ result.add(setForValue(reactiveRedisTemplate, redisEntry, timeout)); }
		}
		
		if (!CollUtil.isEmpty(result))
		{
			if (result.size() == 1)
			{ return CollUtil.get(result, 0); }
			else
			{ return Mono.just(false); }
		}
		else
		{ return Mono.just(false); }
	}
	
	public static <K, V> Mono<Boolean> setForValue(final ReactiveRedisTemplate<K, V> reactiveRedisTemplate, final RedisEntry<K, V> redisEntry)
	{
		Mono<Boolean> result = Mono.just(false);
		
		try
		{
			result = reactiveRedisTemplate.opsForValue()
										  .set(redisEntry.getKey(), redisEntry.getValue());
		}
		catch (Exception ex)
		{ log.error("通过ReactiveRedisTemplate设置值出现异常，异常原因为：", ex); }
		
		return result;
	}
	
	@SafeVarargs
	public static <K, V> Mono<Boolean> setForValues(final ReactiveRedisTemplate<K, V> reactiveRedisTemplate, final RedisEntry<K, V> ... redisEntrys)
	{
		Set<Mono<Boolean>> result = new HashSet<>();
		if (!ArrayUtil.isEmpty(redisEntrys))
		{
			for (RedisEntry<K, V> redisEntry : redisEntrys)
			{ result.add(setForValue(reactiveRedisTemplate, redisEntry)); }
		}
		
		if (!CollUtil.isEmpty(result))
		{
			if (result.size() == 1)
			{ return CollUtil.get(result, 0); }
			else
			{ return Mono.just(false); }
		}
		else
		{ return Mono.just(false); }
	}
	
	/**
	 * 已过时，因不使用ReactiveRedisConnection
	 * @deprecated
	 * */
	@Deprecated(since="已过时，因不使用ReactiveRedisConnection")
	public static <K, V> boolean setForValue(final ReactiveRedisConnection reactiveRedisConnection, final RedisEntry<K, V> redisEntry)
	{
		boolean result = false;
		
		try
		{
			Boolean tmp = reactiveRedisConnection.stringCommands()
												 .set(ByteBuffer.wrap(ObjectUtil.serialize(redisEntry.getKey())), ByteBuffer.wrap(ObjectUtil.serialize(redisEntry.getValue())))
												 .block();
			result = Optional.ofNullable(tmp).orElse(false);
		}
		catch (Exception ex)
		{ log.error("通过ReactiveRedisConnection设置值出现异常，异常原因为：", ex); }
		
		return result;
	}
	
	/**
	 * 已过时，因不使用ReactiveRedisConnection
	 * @deprecated
	 * */
	@Deprecated(since="已过时，因不使用ReactiveRedisConnection")
	@SafeVarargs
	public static <K, V> boolean setForValues(final ReactiveRedisConnection reactiveRedisConnection, final RedisEntry<K, V> ... redisEntrys)
	{
		Set<Boolean> result = new HashSet<>();
		if (!ArrayUtil.isEmpty(redisEntrys))
		{
			for (RedisEntry<K, V> redisEntry : redisEntrys)
			{ result.add(setForValue(reactiveRedisConnection, redisEntry)); }
		}
		
		if (!CollUtil.isEmpty(result))
		{
			if (result.size() == 1)
			{ return CollUtil.get(result, 0); }
			else
			{ return false; }
		}
		else
		{ return false; }
	}
	
	public static <K, V> V getForValue(final RedisOperations<K, V> redisOperations, final K key)
	{
		V result = null;
		
		try
		{
			result = redisOperations.opsForValue()
								    .get(key);
		}
		catch (Exception ex)
		{ log.error("通过RedisTemplate获取值出现异常，异常原因为：", ex); }
		
		return result;
	}
	
	@SafeVarargs
	public static <K, V> Collection<V> getForValues(final RedisOperations<K, V> redisOperations, final K ... keys)
	{
		Collection<V> result = new ArrayList<>();
		if (!ArrayUtil.isEmpty(keys))
		{
			for (K key : keys)
			{ result.add(getForValue(redisOperations, key)); }
		}
		return result;
	}
	
	public static <K, V> Mono<V> getForValue(final ReactiveRedisTemplate<K, V> reactiveRedisTemplate, final K key)
	{
		Mono<V> result = null;
		
		try
		{
			result = reactiveRedisTemplate.opsForValue()
										  .get(key);
		}
		catch (Exception ex)
		{ log.error("通过ReactiveRedisTemplate获取值出现异常，异常原因为：", ex); }
		
		return result;
	}
	
	@SafeVarargs
	public static <K, V> Collection<Mono<V>> getForValues(final ReactiveRedisTemplate<K, V> reactiveRedisTemplate, final K ... keys)
	{
		Collection<Mono<V>> result = new ArrayList<>();
		if (!ArrayUtil.isEmpty(keys))
		{
			for (K key : keys)
			{ result.add(getForValue(reactiveRedisTemplate, key)); }
		}
		return result;
	}
	
	/**
	 * 已过时，因不使用ReactiveRedisConnection
	 * @deprecated
	 * */
	@Deprecated(since="已过时，因不使用ReactiveRedisConnection")
	public static <K, V> V getForValue(final ReactiveRedisConnection reactiveRedisConnection, final K key)
	{
		V result = null;
		
		try
		{
			ByteBuffer byteBuffer = reactiveRedisConnection.stringCommands()
					   									   .get(ByteBuffer.wrap(ObjectUtil.serialize(key)))
					   									   .block();
			result = ObjectUtil.deserialize(Optional.ofNullable(byteBuffer).orElseThrow().array());
		}
		catch (Exception ex)
		{ log.error("通过ReactiveRedisConnection获取值出现异常，异常原因为：", ex); }
		
		return result;
	}
	
	/**
	 * 已过时，因不使用ReactiveRedisConnection
	 * @deprecated
	 * */
	@Deprecated(since="已过时，因不使用ReactiveRedisConnection")
	@SafeVarargs
	public static <K, V> Collection<V> getForValues(final ReactiveRedisConnection reactiveRedisConnection, final K ... keys)
	{
		Collection<V> result = new ArrayList<>();
		if (!ArrayUtil.isEmpty(keys))
		{
			for (K key : keys)
			{ result.add(getForValue(reactiveRedisConnection, key)); }
		}
		return result;
	}
	
	@SafeVarargs
	public static <K, V> long deleteForValues(final RedisOperations<K, V> redisOperations, final K ... keys)
	{
		long result = 0;
		
		try
		{
			Long tmp = redisOperations.delete(CollUtil.newArrayList(keys));
			result = Optional.ofNullable(tmp).orElse(0L);
		}
		catch (Exception ex)
		{ log.error("通过RedisTemplate删除信息出现异常，异常原因为：", ex); }
		
		return result;
	}
	
	@SafeVarargs
	public static <K, V> Mono<Long> deleteForValues(final ReactiveRedisTemplate<K, V> reactiveRedisTemplate, final K... keys)
	{
		Mono<Long> result = Mono.just(0L);
		
		try
		{ result = reactiveRedisTemplate.delete(keys); }
		catch (Exception ex)
		{ log.error("通过ReactiveRedisTemplate删除信息出现异常，异常原因为：", ex); }
		
		return result;
	}
}