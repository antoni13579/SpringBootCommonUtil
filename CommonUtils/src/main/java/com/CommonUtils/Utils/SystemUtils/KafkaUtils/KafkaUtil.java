package com.CommonUtils.Utils.SystemUtils.KafkaUtils;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;

import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;

import cn.hutool.core.collection.CollUtil;

public final class KafkaUtil 
{
	private KafkaUtil() {}
	
	public static <K, V> boolean delete(final KeyValueStore<K, V> kvStore)
	{
		boolean processorResult = keyValueStoreProcessor(kvStore, (final KeyValue<K, V> entry, final KeyValueStore<K, V> tmpKVStore) -> tmpKVStore.delete(entry.key));
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
	public static <K, V> boolean keyValueStoreProcessor(final KeyValueStore<K, V> kvStore, final ItemProcessor<K, V> ... itemProcessorForKeyValueStores)
	{
		if (null != kvStore)
		{
			KeyValueIterator<K, V> iter = kvStore.all();
			while (iter.hasNext())
			{
				KeyValue<K, V> entry = iter.next();				
				JavaCollectionsUtil.collectionProcessor
				(
						CollUtil.newArrayList(itemProcessorForKeyValueStores), 
						(final ItemProcessor<K, V> itemProcessorForKeyValueStore, final int inx, final int length) -> itemProcessorForKeyValueStore.process(entry, kvStore)
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