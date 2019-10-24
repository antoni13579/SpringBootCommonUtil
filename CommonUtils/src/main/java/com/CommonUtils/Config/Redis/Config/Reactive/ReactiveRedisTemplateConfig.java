package com.CommonUtils.Config.Redis.Config.Reactive;

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializationContext.RedisSerializationContextBuilder;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.CommonUtils.Config.Redis.Config.RedisValueType;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;

/**需要使用create bean动作，并加入@EnableCaching*/
public final class ReactiveRedisTemplateConfig 
{
	private ReactiveRedisTemplateConfig() {}
	
	public static ReactiveRedisTemplate<String, Object> getReactiveRedisTemplate(final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory, final RedisValueType redisValueType)
	{
		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
		RedisSerializationContextBuilder<String, Object> redisSerializationContextBuilder = RedisSerializationContext.<String, Object>newSerializationContext();
		switch (redisValueType)
		{
			case BINARY:
				//基于Jdk的Value解析器
		        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
		        redisSerializationContextBuilder
		        	.hashKey(SerializationPair.fromSerializer(stringRedisSerializer))
		        	.hashValue(SerializationPair.fromSerializer(jdkSerializationRedisSerializer))
		        	.key(SerializationPair.fromSerializer(stringRedisSerializer))
		        	.value(SerializationPair.fromSerializer(jdkSerializationRedisSerializer))
		        	.string(SerializationPair.fromSerializer(stringRedisSerializer));
				break;
				
			case JSON:
				// 设置序列化
				ObjectMapper objectMapper = new ObjectMapper().setVisibility(PropertyAccessor.ALL, Visibility.ANY);
				objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);
				
				Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
				jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
				
				redisSerializationContextBuilder
					.hashKey(SerializationPair.fromSerializer(stringRedisSerializer))
					.hashValue(SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
					.key(SerializationPair.fromSerializer(stringRedisSerializer))
					.value(SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
					.string(SerializationPair.fromSerializer(stringRedisSerializer));
				break;
				
			default:
				break;
		}
		
		return new ReactiveRedisTemplate<String, Object>(reactiveRedisConnectionFactory, redisSerializationContextBuilder.build());
	}
}