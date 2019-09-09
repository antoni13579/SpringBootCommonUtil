package com.CommonUtils.Config.Redis.Config.Reactive;

import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;

/**需要使用create bean动作，并加入@EnableCaching*/
public final class ReactiveRedisConnectionConfig 
{
	private ReactiveRedisConnectionConfig() {}
	
	public static ReactiveRedisConnection getReactiveRedisConnection(final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory)
	{ return reactiveRedisConnectionFactory.getReactiveConnection(); }
}