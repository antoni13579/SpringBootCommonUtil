package com.CommonUtils.Redis.Config.Reactive;

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

/**需要使用create bean动作，并加入@EnableCaching*/
public final class ReactiveRedisTemplateConfig 
{
	private ReactiveRedisTemplateConfig() {}
	
	public static ReactiveRedisTemplate<byte[], byte[]> getReactiveRedisTemplate(final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory)
	{ return new ReactiveRedisTemplate<byte[], byte[]>(reactiveRedisConnectionFactory, RedisSerializationContext.raw()); }
}