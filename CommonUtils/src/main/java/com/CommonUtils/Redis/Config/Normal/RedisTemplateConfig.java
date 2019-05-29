package com.CommonUtils.Redis.Config.Normal;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public final class RedisTemplateConfig 
{
	private RedisTemplateConfig() {}
	
	public static RedisTemplate<Object, Object> getRedisTemplate(final RedisConnectionFactory factory)
	{
		RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
		template.setConnectionFactory(factory);
		
		//基于Jdk的Value解析器
        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
        
        //Key解析器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        
        template.setValueSerializer(jdkSerializationRedisSerializer);
        template.setKeySerializer(stringRedisSerializer);
        
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(jdkSerializationRedisSerializer);
        
        template.setStringSerializer(stringRedisSerializer);
        
        template.afterPropertiesSet();
        return template;
	}
}