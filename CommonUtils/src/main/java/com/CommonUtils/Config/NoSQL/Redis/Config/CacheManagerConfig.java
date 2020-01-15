package com.CommonUtils.Config.NoSQL.Redis.Config;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;

import com.CommonUtils.Utils.Annotations.CacheableDuration;
import com.CommonUtils.Utils.DataTypeUtils.DateUtils.DateUtil;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;

public final class CacheManagerConfig 
{
	private CacheManagerConfig() {}
	
	/**
	 * 
	 * @Cacheable(value="00002006_ims_getSauGroupList", key="T(String).valueOf(#comCode).concat('-').concat(#pageNo).concat('-').concat(#pageSize)")
	 * */
	public static CacheManager getInstance(final ApplicationContext applicationContext, 
										   final RedisConnectionFactory factory, 
										   final RedisValueType redisValueType, 
										   final Duration defaultDuration)
	{
		RedisCacheConfiguration defaultRedisCacheConfiguration = getRedisCacheConfiguration(defaultDuration, redisValueType);
		Map<String, RedisCacheConfiguration> initialCacheConfigurations = new HashMap<>();
		String[] beanNames = applicationContext.getBeanNamesForType(Object.class);
		
		Optional.ofNullable(beanNames).ifPresent
		(
				action -> 
				{
					for (String beanName : action)
					{
						final Class<?> clazz = applicationContext.getType(beanName);
						ReflectionUtils.doWithMethods
						(
								Optional.ofNullable(clazz).orElseThrow(),
								new MethodCallbackImpl(initialCacheConfigurations, defaultRedisCacheConfiguration, clazz, redisValueType),
								method -> null != AnnotationUtils.findAnnotation(method, Cacheable.class) || 
										  null != AnnotationUtils.findAnnotation(Optional.ofNullable(clazz).orElseThrow(), CacheConfig.class)
						);
					}
				}
		);
		
		return new RedisCacheManager(RedisCacheWriter.nonLockingRedisCacheWriter(factory), defaultRedisCacheConfiguration, initialCacheConfigurations);
	}
	
	/**
     * CacheDuration标注的有效期，优先使用方法上标注的有效期
	 * @param <A>
     * @param clazz
     * @param method
     * @return
     */
	private static <T> Optional<CacheableDuration> findCacheableDuration(final Class<T> clazz, final Method method)
	{
		CacheableDuration methodCacheDuration = AnnotationUtils.findAnnotation(method, CacheableDuration.class);
        if (null != methodCacheDuration) 
        { return Optional.of(methodCacheDuration); }

        CacheableDuration classCacheDuration = AnnotationUtils.findAnnotation(clazz, CacheableDuration.class);
        if (null != classCacheDuration) 
        { return Optional.of(classCacheDuration); }
        
        return Optional.ofNullable(null);
	}
	
	private static RedisCacheConfiguration getRedisCacheConfiguration(final Duration expireDuration, final RedisValueType redisValueType)
	{
		switch (redisValueType)
		{
			case BINARY:
				return RedisCacheConfiguration.defaultCacheConfig()
											  .entryTtl(expireDuration)
											  .serializeValuesWith(SerializationPair.fromSerializer(new JdkSerializationRedisSerializer()))
											  .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()));
				
			case JSON:
				// 设置序列化
				ObjectMapper objectMapper = new ObjectMapper().setVisibility(PropertyAccessor.ALL, Visibility.ANY);
				objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);
				
				Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
				jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
				
				return RedisCacheConfiguration.defaultCacheConfig()
						  					  .entryTtl(expireDuration)
						  					  .serializeValuesWith(SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
						  					  .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()));
				
			default:
				return null;
		}
	}
	
	private static class MethodCallbackImpl implements MethodCallback
	{
		private Map<String, RedisCacheConfiguration> initialCacheConfigurations;
		private RedisCacheConfiguration defaultRedisCacheConfiguration;
		private Class<?> clazz;
		private RedisValueType redisValueType;
		
		private MethodCallbackImpl(final Map<String, RedisCacheConfiguration> initialCacheConfigurations,
								   final RedisCacheConfiguration defaultRedisCacheConfiguration,
								   final Class<?> clazz,
								   final RedisValueType redisValueType)
		{
			this.initialCacheConfigurations = initialCacheConfigurations;
			this.defaultRedisCacheConfiguration = defaultRedisCacheConfiguration;
			this.clazz = clazz;
			this.redisValueType = redisValueType;
		}
		
		@Override
		public void doWith(Method method) throws IllegalAccessException 
		{
			ReflectionUtils.makeAccessible(method);
			Optional<CacheableDuration> cacheableDuration = findCacheableDuration(Optional.ofNullable(this.clazz).orElseThrow(), method);
			Optional<Cacheable> cacheable = Optional.ofNullable(AnnotationUtils.findAnnotation(method, Cacheable.class));
			cacheable.ifPresent
			(
					consumer -> 
					{
						String[] cacheableValues = consumer.value();
	                	if (null != cacheableValues && cacheableValues.length > 0)
	                	{
	                		for (String cacheableValue : cacheableValues)
	                		{
	                			if (cacheableDuration.isPresent())
	                			{
	                				long expireTime = cacheableDuration.get().expireTime();
	                				TimeUnit unit = cacheableDuration.get().unit();
	                				Duration duration = DateUtil.getDuration(expireTime, unit);
	                				this.initialCacheConfigurations.put(cacheableValue, getRedisCacheConfiguration(duration, this.redisValueType));
	                			}
	                			else
	                			{ this.initialCacheConfigurations.put(cacheableValue, this.defaultRedisCacheConfiguration); }
	                		}
	                	}
					}
			);
		}
	}
}