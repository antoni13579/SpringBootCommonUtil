package com.CommonUtils.Redis.Config.Normal;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;

public final class RedisTemplateConfig 
{
	private RedisTemplateConfig() {}
	
	public static RedisTemplate<String, Object> getRedisTemplate(final RedisConnectionFactory factory, final RedisValueType redisValueType)
	{
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(factory);
		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
		
		switch (redisValueType)
		{
			case BINARY:
				//基于Jdk的Value解析器
		        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
		        
		        //Key解析器
		        template.setValueSerializer(jdkSerializationRedisSerializer);
		        template.setKeySerializer(stringRedisSerializer);
		        
		        template.setHashKeySerializer(stringRedisSerializer);
		        template.setHashValueSerializer(jdkSerializationRedisSerializer);
		        
		        template.setStringSerializer(stringRedisSerializer);
		        
				break;
				
			case JSON:
				// 设置序列化
				Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
				jackson2JsonRedisSerializer.setObjectMapper
				(
						new ObjectMapper()
							.setVisibility(PropertyAccessor.ALL, Visibility.ANY)
							.enableDefaultTyping(DefaultTyping.NON_FINAL)
				);
				
				// 配置redisTemplate
				template.setValueSerializer(jackson2JsonRedisSerializer);// value序列化
				template.setKeySerializer(stringRedisSerializer);// key序列化
				
				template.setHashKeySerializer(stringRedisSerializer);// Hash key序列化
				template.setHashValueSerializer(jackson2JsonRedisSerializer);// Hash value序列化
				
				template.setStringSerializer(stringRedisSerializer);
				
				break;
				
			default:
				break;
		}
		
		template.afterPropertiesSet();
        return template;
	}
	
	public enum RedisValueType 
	{
		BINARY,
		JSON
	}
}