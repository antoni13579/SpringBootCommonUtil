package com.CommonUtils.ConfigTemplate.Config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.transaction.KafkaTransactionManager;

@Configuration
@EnableKafka
public class KafkaConfig 
{
	@Bean
	public NewTopic myTopic()
	{ return com.CommonUtils.Config.Kafka.BaseConfig.createTopic("myTopic", 12, (short)1); }
	
	@Bean
	public DefaultKafkaProducerFactory<String, byte[]> defaultKafkaProducerFactory()
	{ return com.CommonUtils.Config.Kafka.BaseConfig.getDefaultKafkaProducerFactory("127.0.0.1:9092", new ByteArraySerializer(), "myTransactionId-"); }
	
	@Bean
	public KafkaTransactionManager<String, byte[]> kafkaTransactionManager(@Qualifier("defaultKafkaProducerFactory")DefaultKafkaProducerFactory<String, byte[]> defaultKafkaProducerFactory)
	{ return com.CommonUtils.Config.Kafka.BaseConfig.getKafkaTransactionManager(defaultKafkaProducerFactory); }
	
	@Bean
	public KafkaTemplate<String, byte[]> kafkaTemplate(@Qualifier("defaultKafkaProducerFactory")DefaultKafkaProducerFactory<String, byte[]> defaultKafkaProducerFactory)
	{ return com.CommonUtils.Config.Kafka.BaseConfig.getKafkaTemplate(defaultKafkaProducerFactory); }
	
	@Bean
	public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, byte[]>> kafkaListenerContainerFactory()
	{ return com.CommonUtils.Config.Kafka.BaseConfig.getKafkaListenerContainerFactory("127.0.0.1:9092", 4, "myTopicGroup1", false, new ByteArrayDeserializer()); }
}