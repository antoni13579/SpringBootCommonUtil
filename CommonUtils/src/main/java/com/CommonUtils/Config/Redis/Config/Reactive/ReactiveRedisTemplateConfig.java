package com.CommonUtils.Config.Redis.Config.Reactive;

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

/**需要使用create bean动作，并加入@EnableCaching*/
public final class ReactiveRedisTemplateConfig 
{
	private ReactiveRedisTemplateConfig() {}
	
	public static ReactiveRedisTemplate<Object, Object> getReactiveRedisTemplate(final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory)
	{ return new ReactiveRedisTemplate<Object, Object>(reactiveRedisConnectionFactory, RedisSerializationContext.java()); }
}