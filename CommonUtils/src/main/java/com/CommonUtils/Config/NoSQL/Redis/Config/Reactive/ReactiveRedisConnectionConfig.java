package com.CommonUtils.Config.NoSQL.Redis.Config.Reactive;

import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;

/**需要使用create bean动作，并加入@EnableCaching*/
/**没有必要使用这个*/
@Deprecated
public final class ReactiveRedisConnectionConfig 
{
	private ReactiveRedisConnectionConfig() {}
	
	public static ReactiveRedisConnection getReactiveRedisConnection(final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory)
	{ return reactiveRedisConnectionFactory.getReactiveConnection(); }
}