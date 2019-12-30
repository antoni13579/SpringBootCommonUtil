package com.CommonUtils.Config.NoSQL.Redis.Config;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public final class RedisMessageListenerContainerConfig 
{
	private RedisMessageListenerContainerConfig() {}
	
	public static RedisMessageListenerContainer getInstance(final RedisConnectionFactory connectionFactory, 
															final Map<? extends MessageListener, Collection<? extends Topic>> listeners,
															final boolean useUniqueThread)
	{
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        //container.addMessageListener(new MessageListenerAdapter(beanName, beanMethodName), new PatternTopic(topicName));
        container.setMessageListeners(listeners);
        
        if (useUniqueThread)
        {
        	/**
             * * 如果不定义线程池，每一次消费都会创建一个线程，如果业务层面不做限制，就会导致秒杀超卖。
             * * 此处感谢网友 DIscord
             */
            container.setTaskExecutor
            (
            		new ThreadPoolExecutor
            		(
            				1, 
            				1, 
            				5L, 
            				TimeUnit.SECONDS, 
            				new LinkedBlockingQueue<>(1000), 
            				new ThreadFactoryBuilder()
            					.setNameFormat("redis-listener-pool-%d")
            					.build()
            		)
            );
        }
        
        return container;
	}
}