package com.CommonUtils.Kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import com.CommonUtils.Utils.CollectionUtils.CustomCollections.HashMap;

/**使用此类，记得进行create bean动作，并加入@EnableKafka注解*/
public final class ConsumerRelatedConfig 
{
	private ConsumerRelatedConfig() {}
	
	public static KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Object, Object>> getKafkaListenerContainerFactory(final String kafkaBroker, final String kafkaGroup)
	{
		ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<Object, Object>();
        factory.setConsumerFactory
        (
        		new DefaultKafkaConsumerFactory<Object, Object>
                (
                		new HashMap<String, Object>().put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker)
                									 .put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false)
                									 .put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100")
                									 .put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000")
                									 .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                									 .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class)
                									 .put(ConsumerConfig.GROUP_ID_CONFIG, kafkaGroup)
                									 .put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
                									 .getMap()
                )
        );
        
        factory.setConcurrency(4);
        factory.getContainerProperties().setPollTimeout(4000);

        return factory;
	}
}