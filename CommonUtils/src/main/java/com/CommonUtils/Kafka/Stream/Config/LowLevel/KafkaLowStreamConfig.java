package com.CommonUtils.Kafka.Stream.Config.LowLevel;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.Punctuator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;

import com.CommonUtils.Utils.ArrayUtils.ArrayUtil;
import com.CommonUtils.Utils.BytesUtils.BytesUtil;
import com.CommonUtils.Utils.CollectionUtils.CustomCollections.Properties;
import com.CommonUtils.Utils.CommonUtils.CommonUtil;
import com.CommonUtils.Utils.KafkaUtils.KafkaUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class KafkaLowStreamConfig 
{
	private KafkaLowStreamConfig() {}
	
	public static KafkaStreams getInstance(final String kafkaStreamJobName, 
										   final String kafkaBroker, 
										   final String kafkaTopicName, 
										   final EStoreBuilderType eStoreBuilderType,
										   final EKeyValueStoreType eKeyValueStoreType,
									
										   //可以设置为1000
										   final long jobProcessorScheduleOfIntervalMs,
									
										   //可以设置为PunctuationType.STREAM_TIME
										   final PunctuationType punctuationType,
										   final ItemProcessor ... itemProcessors)
	{
		Properties props = new Properties()
				.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaStreamJobName)
				.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker)
        		.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 2048)
        		.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass())
        		.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass())
        		// setting offset reset to earliest so that we can re-run the demo code with the same pre-loaded data
        		.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Topology builder = new Topology()
        		.addSource("Source", kafkaTopicName)
        		.addProcessor("Process", new JobProcessorSupplier(jobProcessorScheduleOfIntervalMs, punctuationType, itemProcessors), "Source")
        		.addSink("Sink", kafkaTopicName, "Process");
        
        switch (eStoreBuilderType)
        {
        	case WindowStoreBuilder:
        		log.warn("未实现WindowStoreBuilder");
        		break;
        		
        	case KeyValueStoreBuilder:
        		switch (eKeyValueStoreType)
        		{
        			case Persistent:
        				builder.addStateStore
        				(
        						Stores.keyValueStoreBuilder
        						(
        								Stores.persistentKeyValueStore("Counts"), 
                						Serdes.String(), 
                						Serdes.ByteArray()
        						), 
        						"Process"
        				);
        				break;
        				
        			case InMemory:
        				builder.addStateStore
        				(
        						Stores.keyValueStoreBuilder
        						(
        								Stores.inMemoryKeyValueStore("Counts"), 
                						Serdes.String(), 
                						Serdes.ByteArray()
        						), 
        						"Process"
        				);
        				break;
        				
        			case LruMap:
        				builder.addStateStore
        				(
        						Stores.keyValueStoreBuilder
        						(
        								Stores.lruMap("Counts", 1024), 
                						Serdes.String(), 
                						Serdes.ByteArray()
        						), 
        						"Process"
        				);
        				break;
        				
        			default:
        				log.warn("没有指定对应的KeyValueStore，请及时排查");
        				break;
        		}
        		break;
        	
        	case SessionStoreBuilder:
        		log.warn("未实现SessionStoreBuilder");
        		break;
        		
        	default:
        		log.warn("没有指定对应的StoreBuilder，请及时排查");
        		break;
        }

        return new KafkaStreams(builder, props.getProperties());
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
						JobProcessor.this.intervalMs, 
						JobProcessor.this.punctuationType, 
						new Punctuator() 
						{
							//基于时间的流逝周期性地执行
							//这个函数一般不需要添加任何代码，如果加了，很有可能会与process方法有冲突，导致消息数据不对，具体测试过，里面写了代码，process方法的遍历kvStore的值就有问题了
							@Override
							public void punctuate(long timestamp) 
							{								
								boolean processorResult = KafkaUtil.KeyValueStoreProcessor
								(
										JobProcessor.this.kvStore, 
										(final KeyValue<String, byte[]> entry, final KeyValueStore<String, byte[]> kvStore) -> 
										{
											//这句不能加，加入就会影响到原来的Kafka消息，导致程序报错
											//TestJobProcessor.this.processorContext.forward(entry.key, entry.value);
										}
								);
								
								if (processorResult)
								{ JobProcessor.this.kvStore.flush(); }
							}
						}
				);
				
				JobProcessor.this.kvStore = CommonUtil.cast(context.getStateStore("Counts"));
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
				
				if (!ArrayUtil.isArrayEmpty(value))
				{
					Object tmpValue = BytesUtil.fromBytes(value);
					if (null != tmpValue)
					{
						ArrayUtil.arrayProcessor
						(
								JobProcessor.this.itemProcessors, 
								(final ItemProcessor itemProcessor, final int indx) -> 
								{ itemProcessor.process(tmpValue, JobProcessor.this.processorContext, JobProcessor.this.kvStore); }
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
}