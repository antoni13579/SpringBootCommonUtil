package com.CommonUtils.Config.Kafka.Config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties.AckMode;

import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.CustomCollections.HashMap;

/**使用此类，记得进行create bean动作，并加入@EnableKafka注解*/
public final class BaseConfig 
{
	private BaseConfig() {}
	
	/**
	 * 消费者这边概念相对多，记录一下
	 * 消费组就是group.id不同。kafka中，同一个topic下，消息会给下面每一个group发送消息（如果有十个，那个这十个group都会接受到这个消息）。但是分区每个消息只有一个分区获取。
	 * 
	 * @KafkaListener这个注解也是要重点注意，group，topic，partion，这个注解可以灵活进行消费，相关属性如下
	 * id：消费者的id，当GroupId没有被配置的时候，默认id为GroupId
containerFactory：上面提到了@KafkaListener区分单数据还是多数据消费只需要配置一下注解的containerFactory属性就可以了，这里面配置的是监听容器工厂，也就是ConcurrentKafkaListenerContainerFactory，配置BeanName
topics：需要监听的Topic，可监听多个
topicPartitions：可配置更加详细的监听信息，必须监听某个Topic中的指定分区，或者从offset为200的偏移量开始监听
errorHandler：监听异常处理器，配置BeanName
groupId：消费组ID
idIsGroup：id是否为GroupId
clientIdPrefix：消费者Id前缀
beanRef：真实监听容器的BeanName，需要在 BeanName前加 "__"



监听Topic中指定的分区
@KafkaListener(id = "batchWithPartition",clientIdPrefix = "bwp",containerFactory = "batchContainerFactory",
        topicPartitions = {
            @TopicPartition(topic = "topic.quick.batch.partition",partitions = {"1","3"}),
            @TopicPartition(topic = "topic.quick.batch.partition",partitions = {"0","4"},
                    partitionOffsets = @PartitionOffset(partition = "2",initialOffset = "100"))
        }
    )
    
    
    
    使用Ack机制确认消费
    设置ENABLE_AUTO_COMMIT_CONFIG=false，禁止自动提交
设置AckMode=MANUAL_IMMEDIATE
监听方法加入Acknowledgment ack 参数

怎么拒绝消息呢，只要在监听方法中不调用ack.acknowledge()即可
    
    data ： 对于data值的类型其实并没有限定，根据KafkaTemplate所定义的类型来决定。data为List集合的则是用作批量消费。
ConsumerRecord：具体消费数据类，包含Headers信息、分区信息、时间戳等
Acknowledgment：用作Ack机制的接口
Consumer：消费者类，使用该类我们可以手动提交偏移量、控制消费速率等功能






在这段章节开头之初我就讲解了Kafka机制会出现的一些情况，导致没办法重复消费未被Ack的消息，解决办法有如下：
重新将消息发送到队列中，这种方式比较简单而且可以使用Headers实现第几次消费的功能，用以下次判断
    @KafkaListener(id = "ack", topics = "topic.quick.ack", containerFactory = "ackContainerFactory")
    public void ackListener(ConsumerRecord record, Acknowledgment ack, Consumer consumer) {
        log.info("topic.quick.ack receive : " + record.value());

        //如果偏移量为偶数则确认消费，否则拒绝消费
        if (record.offset() % 2 == 0) {
            log.info(record.offset()+"--ack");
            ack.acknowledge();
        } else {
            log.info(record.offset()+"--nack");
            kafkaTemplate.send("topic.quick.ack", record.value());
        }
    }













使用Consumer.seek方法，重新回到该未ack消息偏移量的位置重新消费，这种可能会导致死循环，原因出现于业务一直没办法处理这条数据，但还是不停的重新定位到该数据的偏移量上。
    @KafkaListener(id = "ack", topics = "topic.quick.ack", containerFactory = "ackContainerFactory")
    public void ackListener(ConsumerRecord record, Acknowledgment ack, Consumer consumer) {
        log.info("topic.quick.ack receive : " + record.value());

        //如果偏移量为偶数则确认消费，否则拒绝消费
        if (record.offset() % 2 == 0) {
            log.info(record.offset()+"--ack");
            ack.acknowledge();
        } else {
            log.info(record.offset()+"--nack");
            consumer.seek(new TopicPartition("topic.quick.ack",record.partition()),record.offset() );
        }
    }
	 * */
	public static <V> KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, V>> getKafkaListenerContainerFactory(final String kafkaBrokers, 
																																    final int concurrency,
																																    final String kafkaGroup,
																																    final boolean useAutoCommit,
																																    final Deserializer<V> valueDeserializer)
	{
		HashMap<String, Object> configs = new HashMap<String, Object>()
												.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers)
				 								.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100")
				 								.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000")
				 								//.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
				 								//.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class)
				 								.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaGroup)
				 								.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
				 
				 								//设置批量消费每次最多消费多少条消息记录
				 								.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 50);
		
		if (useAutoCommit)
		{ configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true); }
		
		//使用Ack机制确认消费，禁止自动提交
		else
		{ configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); }
		
		ConcurrentKafkaListenerContainerFactory<String, V> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory
        (
        		new DefaultKafkaConsumerFactory<String, V>
                (
                		configs.getMap(),
                		new StringDeserializer(),
                		valueDeserializer
                )
        );
        
        /**
         * 并发消费设置
         * 如果topic有n个分区，为了加快消费将并发设置为n，也就是有n个KafkaMessageListenerContainer
         * */
        factory.setConcurrency(concurrency);
        factory.getContainerProperties().setPollTimeout(4000);
        
        //使用Ack机制确认消费，设置AckMode=MANUAL_IMMEDIATE
        if (!useAutoCommit)
        { factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE); }
        

        /**
         * 开启批量消费，但别设置为true，亲测有问题
         * */
        factory.setBatchListener(false);
        
        //通过开启并发消费与批量消费，可以有效提高消费性能
        return factory;
	}
	
	public static <V> DefaultKafkaProducerFactory<String, V> getDefaultKafkaProducerFactory(final String kafkaBrokers,
		  																					final Serializer<V> valueSerializer,
		  																					final String transactionIdPrefix)
	{
		DefaultKafkaProducerFactory<String, V> result =  new DefaultKafkaProducerFactory<>
        (
        		new HashMap<String, Object>().put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers)
        									 .put(ProducerConfig.RECEIVE_BUFFER_CONFIG, 1000)
        									 
        									 .put(ProducerConfig.BATCH_SIZE_CONFIG, 4096)
        									 .put(ProducerConfig.LINGER_MS_CONFIG, 1)
        									 .put(ProducerConfig.BUFFER_MEMORY_CONFIG, 40960)
        									 
        									 /**
        									  * 设置这里，主要是提示了Must set retries to non-zero when using the idempotent producer的报错
        									  * 百度了一下，需要设置幂等发送
        									  * */
        									 .put(ProducerConfig.RETRIES_CONFIG, 1)
        									 .put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true)
        									 
        									 //.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
        									 //.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class)
        									 .getMap(),
        		new StringSerializer(),
        		valueSerializer
        );
		result.transactionCapable();
		result.setTransactionIdPrefix(transactionIdPrefix);
		return result;
	}
}