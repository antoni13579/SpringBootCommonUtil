package com.CommonUtils.Config.Kafka.Stream.Config.LowLevel;

import java.time.Duration;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;

import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;
import com.CommonUtils.Utils.SystemUtils.KafkaUtils.KafkaUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

/**这个是模板，推荐自主实现
 * @deprecated
 * */
@Deprecated(since="这个是模板，推荐自主实现")
@Slf4j
public final class KafkaLowStreamConfig 
{
	private KafkaLowStreamConfig() {}
	
	public static <T> KafkaStreams getInstance(final String kafkaStreamJobName, 
										       final String kafkaBroker, 
										       final String kafkaTopicName, 
										       final EStoreBuilderType eStoreBuilderType,
										       final EKeyValueStoreType eKeyValueStoreType,
									
										       //可以设置为1000
										       final long jobProcessorScheduleOfIntervalMs,
									
										       //可以设置为PunctuationType.STREAM_TIME
										       final PunctuationType punctuationType,
										   
										       final Serde<T> valueSerde,
										       final ItemProcessor ... itemProcessors)
	{
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaStreamJobName);
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker);
		props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 2048);
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, valueSerde.getClass());
        // setting offset reset to earliest so that we can re-run the demo code with the same pre-loaded data
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Topology builder = new Topology()
        		.addSource("Source", kafkaTopicName)
        		.addProcessor("Process", new JobProcessorSupplier(jobProcessorScheduleOfIntervalMs, punctuationType, itemProcessors), "Source")
        		.addSink("Sink", kafkaTopicName, "Process");
        
        switch (eStoreBuilderType)
        {
        	case WINDOW_STORE_BUILDER:
        		log.warn("未实现WindowStoreBuilder");
        		break;
        		
        	case KEY_VALUE_STORE_BUILDER:
        		switch (eKeyValueStoreType)
        		{
        			case PERSISTENT:
        				builder.addStateStore
        				(
        						Stores.keyValueStoreBuilder
        						(
        								Stores.persistentKeyValueStore("Counts"), 
                						Serdes.String(), 
                						valueSerde
        						), 
        						"Process"
        				);
        				break;
        				
        			case IN_MEMORY:
        				builder.addStateStore
        				(
        						Stores.keyValueStoreBuilder
        						(
        								Stores.inMemoryKeyValueStore("Counts"), 
                						Serdes.String(), 
                						valueSerde
        						), 
        						"Process"
        				);
        				break;
        				
        			case LRU_MAP:
        				builder.addStateStore
        				(
        						Stores.keyValueStoreBuilder
        						(
        								Stores.lruMap("Counts", 1024), 
                						Serdes.String(), 
                						valueSerde
        						), 
        						"Process"
        				);
        				break;
        				
        			default:
        				log.warn("没有指定对应的KeyValueStore，请及时排查");
        				break;
        		}
        		break;
        	
        	case SESSION_STORE_BUILDER:
        		log.warn("未实现SessionStoreBuilder");
        		break;
        		
        	default:
        		log.warn("没有指定对应的StoreBuilder，请及时排查");
        		break;
        }

        return new KafkaStreams(builder, props);
	}
	
	public static class JobProcessorSupplier implements ProcessorSupplier<String, byte[]>
	{
		private long intervalMs;
		private PunctuationType punctuationType;
		private boolean firstRun = true;
		private ItemProcessor[] itemProcessors;
		
		public JobProcessorSupplier(final long intervalMs, final PunctuationType punctuationType, final ItemProcessor[] itemProcessors)
		{
			JobProcessorSupplier.this.intervalMs = intervalMs;
			JobProcessorSupplier.this.punctuationType = punctuationType;
			JobProcessorSupplier.this.itemProcessors = itemProcessors;
		}
		
		@Override
		public Processor<String, byte[]> get() 
		{ return new JobProcessor(JobProcessorSupplier.this.intervalMs, JobProcessorSupplier.this.punctuationType, JobProcessorSupplier.this.itemProcessors); }
		
		public class JobProcessor implements Processor<String, byte[]>
		{
			private ProcessorContext processorContext;
		    private KeyValueStore<String, byte[]> kvStore;
		    private long intervalMs;
		    private PunctuationType punctuationType;
		    private ItemProcessor[] itemProcessors;
		    
		    public JobProcessor(final long intervalMs, final PunctuationType punctuationType, final ItemProcessor[] itemProcessors)
		    {
		    	JobProcessor.this.intervalMs = intervalMs;
		    	JobProcessor.this.punctuationType = punctuationType;
		    	JobProcessor.this.itemProcessors = itemProcessors;
		    }
		    
		    @Override
			public void close() 
			{ KafkaUtil.close(JobProcessor.this.kvStore);}
			
			@Override
			public void init(ProcessorContext context) 
			{
				JobProcessor.this.processorContext = context;
				JobProcessor.this.processorContext.schedule
				(
						Duration.ofMillis(JobProcessor.this.intervalMs), 
						JobProcessor.this.punctuationType, 
						
						//基于时间的流逝周期性地执行
						//这个函数一般不需要添加任何代码，如果加了，很有可能会与process方法有冲突，导致消息数据不对，具体测试过，里面写了代码，process方法的遍历kvStore的值就有问题了
						timestamp -> 
						{
							boolean processorResult = KafkaUtil.keyValueStoreProcessor
							(
									JobProcessor.this.kvStore, 
									(final KeyValue<String, byte[]> entry, final KeyValueStore<String, byte[]> internalKvStore) -> 
									{
										//这句不能加，加入就会影响到原来的Kafka消息，导致程序报错
										//TestJobProcessor.this.processorContext.forward(entry.key, entry.value);
									}
							);
									
							if (processorResult)
							{ JobProcessor.this.kvStore.flush(); }
						}
				);
				
				JobProcessor.this.kvStore = Convert.convert(new TypeReference<KeyValueStore<String, byte[]>>() {}, context.getStateStore("Counts"));
			}

			//会被作用于每条收到的记录
			@Override
			public void process(String key, byte[] value) 
			{
				if (JobProcessorSupplier.this.firstRun)
				{
					KafkaUtil.delete(JobProcessor.this.kvStore);
					JobProcessorSupplier.this.firstRun = false;
				}
				
				if (!cn.hutool.core.util.ArrayUtil.isEmpty(value))
				{
					Object tmpValue = ObjectUtil.deserialize(value);
					if (null != tmpValue)
					{
						JavaCollectionsUtil.collectionProcessor
						(
								CollUtil.newArrayList(JobProcessor.this.itemProcessors), 
								(final ItemProcessor itemProcessor, final int indx, final int length) -> itemProcessor.process(tmpValue, JobProcessor.this.processorContext, JobProcessor.this.kvStore)
						);
					}
				}
				
				//这句不建议加入，因为Topic消息的提交不应该由KafkaStream来完成
				//TestJobProcessor.this.processorContext.commit();
			}
		}
	}
	
	@FunctionalInterface
	public interface ItemProcessor
	{ void process(final Object value, final ProcessorContext processorContext, final KeyValueStore<String, byte[]> kvStore); }
	
	public enum EKeyValueStoreType
	{
		PERSISTENT,
		IN_MEMORY,
		LRU_MAP
	}
	
	public enum EStoreBuilderType 
	{
		WINDOW_STORE_BUILDER,
		KEY_VALUE_STORE_BUILDER,
		SESSION_STORE_BUILDER;
	}
}