package com.CommonUtils.Utils.KafkaUtils;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;

import com.CommonUtils.Utils.ArrayUtils.ArrayUtil;

public final class KafkaUtil 
{
	private KafkaUtil() {}
	
	public static <K, V> boolean delete(final KeyValueStore<K, V> kvStore)
	{
		boolean processorResult = KeyValueStoreProcessor(kvStore, (final KeyValue<K, V> entry, final KeyValueStore<K, V> tmpKVStore) -> { tmpKVStore.delete(entry.key); });
		if (processorResult)
		{
			kvStore.flush();
			return true;
		}
		else
		{ return false; }
	}
	
	public static <K, V> void close(final KeyValueStore<K, V> kvStore)
	{
		if (null != kvStore)
		{
			kvStore.flush();
			kvStore.close();
		}
	}
	
	@SafeVarargs
	public static <K, V> boolean KeyValueStoreProcessor(final KeyValueStore<K, V> kvStore, final ItemProcessor<K, V> ... itemProcessorForKeyValueStores)
	{
		if (null != kvStore)
		{
			KeyValueIterator<K, V> iter = kvStore.all();
			while (iter.hasNext())
			{
				KeyValue<K, V> entry = iter.next();
				ArrayUtil.arrayProcessor
				(
						itemProcessorForKeyValueStores, 
						(final ItemProcessor<K, V> itemProcessorForKeyValueStore, final int inx) -> 
						{ itemProcessorForKeyValueStore.process(entry, kvStore); }
				);
			}
			
			return true;
		}
		else
		{ return false; }
	}
	
	@FunctionalInterface
	public interface ItemProcessor<K, V>
	{ void process(final KeyValue<K, V> entry, final KeyValueStore<K, V> kvStore); }
}