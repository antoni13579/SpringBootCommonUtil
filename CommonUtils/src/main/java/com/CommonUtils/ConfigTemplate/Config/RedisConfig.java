package com.CommonUtils.ConfigTemplate.Config;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import com.CommonUtils.Config.NoSQL.Redis.Config.RedisMessageListenerContainerConfig;
import com.CommonUtils.Config.NoSQL.Redis.Config.RedisValueType;
import com.CommonUtils.Config.NoSQL.Redis.Config.Normal.RedisTemplateConfig;
import com.CommonUtils.Config.NoSQL.Redis.Config.Reactive.ReactiveRedisTemplateConfig;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableCaching
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 86400*30)
@Slf4j
public class RedisConfig 
{
	@Bean
	public RedisTemplate<String, Object> redisTemplate(final RedisConnectionFactory factory)
	{ return RedisTemplateConfig.getRedisTemplate(factory, RedisValueType.BINARY); }
	
	@Bean
	public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory)
	{ return ReactiveRedisTemplateConfig.getReactiveRedisTemplate(reactiveRedisConnectionFactory, RedisValueType.BINARY); }
	
	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(final RedisConnectionFactory connectionFactory)
	{
		Collection<Topic> topics = new ArrayList<>();
		topics.add(ChannelTopic.of("channel1"));
		
		Map<MessageListener, Collection<? extends Topic>> listeners = new HashMap<>();
		listeners.put
		(
				(Message message, byte[] pattern) -> 
				{
					String key = "";
					try 
					{ key = new String(message.getBody(), "utf8"); } 
					catch (UnsupportedEncodingException e) 
					{ log.error("配置Redis监听出现异常，异常原因为：", e); }
					
					String channel = new String(message.getChannel());
					String patternStr = new String(pattern);
					log.info("监视的通道为{}，对应的key为{}，前缀为{}", channel, key, patternStr);
				}, 
				topics
		);
		
		return RedisMessageListenerContainerConfig.getInstance
		(
				connectionFactory, 
				listeners, 
				true
		);
	}
}