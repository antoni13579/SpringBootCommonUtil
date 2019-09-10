package com.CommonUtils.Utils.DistributedLocksUtils;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;

import com.CommonUtils.Utils.DBUtils.Bean.RedisEntry;
import com.CommonUtils.Utils.DBUtils.RedisUtils.RedisUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DistributedLockRedis 
{
	private DistributedLockRedis() {}
	
	public synchronized static <K, V> boolean lock(final RedisTemplate<K, V> redisTemplate, final RedisEntry<K, V> redisEntry)
	{
		V val = RedisUtil.getForValue(redisTemplate, redisEntry.getKey());
		
		//如果value不为空，说明Redis还存在记录，无法上锁
		if (null != val)
		{
			log.warn("无法获取对应分布式锁记录，程序还在占用着分布式锁，程序结束"); 
			return false;
		}
		
		//否则可以上锁
		else
		{
			RedisUtil.setForValue(redisTemplate, redisEntry, 30, TimeUnit.MINUTES);
			return true;
		}
	}
	
	public synchronized static <K, V> boolean lock(final ReactiveRedisTemplate<K, V> reactiveRedisTemplate, final RedisEntry<K, V> redisEntry)
	{
		V val = RedisUtil.getForValue(reactiveRedisTemplate, redisEntry.getKey());
		
		//如果value不为空，说明Redis还存在记录，无法上锁
		if (null != val)
		{
			log.warn("无法获取对应分布式锁记录，程序还在占用着分布式锁，程序结束"); 
			return false;
		}
		
		//否则可以上锁
		else
		{
			Duration timeout = Duration.ofMinutes(30);
			RedisUtil.setForValue(reactiveRedisTemplate, redisEntry, timeout);
			return true;
		}
	}
	
	public synchronized static <K, V> void unlock(final RedisTemplate<K, V> redisTemplate, final K key)
	{ RedisUtil.deleteForValues(redisTemplate, key); }
	
	public synchronized static <K, V> void unlock(final ReactiveRedisTemplate<K, V> reactiveRedisTemplate, final K key)
	{ RedisUtil.deleteForValues(reactiveRedisTemplate, key); }
	
	public synchronized <K, V> boolean renewal(final RedisTemplate<K, V> redisTemplate, final RedisEntry<K, V> redisEntry)
	{ return RedisUtil.setExpireForValue(redisTemplate, redisEntry, 30, TimeUnit.MINUTES); }
	
	public synchronized <K, V> boolean renewal(final ReactiveRedisTemplate<K, V> reactiveRedisTemplate, final RedisEntry<K, V> redisEntry)
	{ return RedisUtil.setExpireForValue(reactiveRedisTemplate, redisEntry, Duration.ofMinutes(30)); }
}