package com.CommonUtils.Config.Kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.CustomCollections.HashMap;

/**使用此类，记得进行create bean动作，并加入@EnableKafka注解*/
public final class ProducerRelatedConfig 
{
	private ProducerRelatedConfig() {}
	
	public static KafkaTemplate<Object, Object> getKafkaTemplate(final String kafkaBroker) 
	{
		return new KafkaTemplate<Object, Object>
		(
				new DefaultKafkaProducerFactory<Object, Object>
		        (
		        		new HashMap<String, Object>().put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker)
		        									 .put(ProducerConfig.RECEIVE_BUFFER_CONFIG, 1000)
		        									 .put(ProducerConfig.RETRIES_CONFIG, 0)
		        									 .put(ProducerConfig.BATCH_SIZE_CONFIG, 4096)
		        									 .put(ProducerConfig.LINGER_MS_CONFIG, 1)
		        									 .put(ProducerConfig.BUFFER_MEMORY_CONFIG, 40960)
		        									 .put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
		        									 .put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class)
		        									 .getMap()
		        )
		);
	}
}