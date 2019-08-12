package com.CommonUtils.Utils.CollectionUtils;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;

import com.CommonUtils.Jdbc.Bean.DBTable.Column;
import com.CommonUtils.Jdbc.Bean.DBTable.Table;
import com.CommonUtils.Utils.ArrayUtils.ArrayUtil;
import com.CommonUtils.Utils.CommonUtils.CommonUtil;
import com.CommonUtils.Utils.DateUtils.DateFormat;
import com.CommonUtils.Utils.StringUtils.StringContants;
import com.CommonUtils.Utils.StringUtils.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class JavaCollectionsUtil 
{
	private JavaCollectionsUtil() {}
	
	public static Map<String, Long> wordCount(final Collection<String> strings)
	{
		if (!isCollectionEmpty(strings))
		{ return wordCount(strings.toArray(new String[strings.size()])); }
		else
		{ return Collections.emptyMap(); }
	}
	
	public static Map<String, Long> wordCount(final String ... strings)
	{
		if (!ArrayUtil.isArrayEmpty(strings))
		{
			return Arrays.asList(strings)
						 .stream()
						 .map(String::trim)
						 .filter(value -> !StringUtil.isStrEmpty(value))
						 .map(value -> value.split(StringContants.PATTERN_5))
						 .flatMap(x -> Stream.of(x))
						 .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		}
		else
		{ return Collections.emptyMap(); }
	}
	
	/**
	 * 如果map中存在键key,则返回键key的值。否则向map中新增映射关系key -> value并返回value
	 * */
	public static <K, V> V getOrElseUpdate(final Map<K, V> map, final K key, final V value)
	{
		if (map.containsKey(key))
		{ return map.get(key); }
		else
		{
			map.put(key, value);
			return value;
		}
	}
	
	@SafeVarargs
	public static <K, V> Optional<V> getOrElseUpdate(final MultiKeyMap<K, V> map, final V value, final K ... keys)
	{
		V result = null;
		
		if (!ArrayUtil.isArrayEmpty(keys))
		{
			if (keys.length == 1)
			{ log.warn("使用getOrElseUpdate，key的数量必须2个以上"); }
			else if (keys.length == 2)
			{
				if (map.containsKey(keys[0], keys[1]))
				{ result = map.get(keys[0], keys[1]); }
				else
				{
					map.put(keys[0], keys[1], value);
					result = value;
				}
			}
			else if (keys.length == 3)
			{
				if (map.containsKey(keys[0], keys[1], keys[2]))
				{ result = map.get(keys[0], keys[1], keys[2]); }
				else
				{
					map.put(keys[0], keys[1], keys[2], value);
					result = value;
				}
			}
			else if (keys.length == 4)
			{
				if (map.containsKey(keys[0], keys[1], keys[2], keys[3]))
				{ result = map.get(keys[0], keys[1], keys[2], keys[3]); }
				else
				{
					map.put(keys[0], keys[1], keys[2], keys[3], value);
					result = value;
				}
			}
			else if (keys.length == 5)
			{
				if (map.containsKey(keys[0], keys[1], keys[2], keys[3], keys[4]))
				{ result = map.get(keys[0], keys[1], keys[2], keys[3], keys[4]); }
				else
				{
					map.put(keys[0], keys[1], keys[2], keys[3], keys[4], value);
					result = value;
				}
			}
			else
			{ log.warn("使用getOrElseUpdate，key的数量超过5个，请注意处理"); }
		}
		
		return Optional.ofNullable(result);
	}
	
	/**
	 * 检测Map是否为空，true为空，false为非空
	 * */
	public static <K, V> boolean isMapEmpty(final Map<K, V> map)
	{
		if (null == map || map.isEmpty())
		{ return true; }
		
		return false;
	}
	
	public static <K, V> boolean isMapEmpty(final MultiKeyMap<K, V> map)
	{
		if (null == map || map.isEmpty())
		{ return true; }
		
		return false;
	}
	
	/**
	 * 检测实现了Collection接口，如List，Set等是否为空，true为空，false为非空
	 * */
	public static <T> boolean isCollectionEmpty(final Collection<T> collection)
	{
		if (null == collection || collection.isEmpty())
		{ return true; }
		
		return false;
	}
	
	public static boolean collectionEquals(final Collection<String[]> records1, final Collection<String[]> records2)
    {
    	//两个都是空，则为相等
    	if (JavaCollectionsUtil.isCollectionEmpty(records1) && JavaCollectionsUtil.isCollectionEmpty(records2))
    	{ return true; }
    	
    	//【一个不是空，另一个是，不是相等】 或 【 一个是空，另一个不是，不是相等】
    	else if ((!JavaCollectionsUtil.isCollectionEmpty(records1) && JavaCollectionsUtil.isCollectionEmpty(records2)) || 
    			 (JavaCollectionsUtil.isCollectionEmpty(records1) && !JavaCollectionsUtil.isCollectionEmpty(records2)))
    	{ return false; }
    	
    	//两个都不是空
    	else
    	{
    		if (records1.size() != records2.size())
    		{ return false; }
    		
    		int matched = 0;
    		for (String[] fileRecord : records1)
    		{
    			for (String[] needUpdateRecord : records2)
    			{
    				if (ArrayUtil.arrayEquals(fileRecord, needUpdateRecord))
    				{ matched++; }
    			}
    		}
    		
    		if (matched == records1.size())
    		{ return true; }
    		
    		return false;
    	}
    }
	
	public static <T> T getItem(final Collection<T> items, final int dstIndx)
	{
		List<T> result = new ArrayList<T>();
		collectionProcessor(items, (T item, int indx, int length) -> { if (dstIndx == indx) { result.add(item);} });
		return result.get(0);
	}
	
	/**
	 * 判断业务流程布尔集合的最终结果，一般用于存放各个业务流程的执行结果，区分好情况，别乱用
	 * */
	public static boolean getOperationFlowResult(final Set<Boolean> items)
	{
		//不为空才能进入下一步判断
		if (!isCollectionEmpty(items))
		{
			//true与false同时存在，说明有其中一个流程失败，直接返回false
			if (items.size() > 1)
			{ return false; }
			
			//只有唯一结果，返回是false还是true
			else
			{ return getItem(items, 0); }
		}
		
		//为空直接是false
		else
		{ return false; }
	}
	
	public static <K, V> Collection<String> getMapValues(final Collection<Map<K, V>> records, 
														 final ResultSetMetaData resultSetMetaData, 
														 final String delimiter,
														 final DateFormat dateFomat,
														 final boolean useColumnName) throws Exception
	{
		if (!isCollectionEmpty(records))
		{
			Collection<String> lines = new ArrayList<>();
			//迭代每一行数据
			for (Map<K, V> record : records)
			{
				if (!isMapEmpty(record))
				{
					StringBuilder line = new StringBuilder();
					//根据ResultSetMetaData提供的表结构，获取变量record对应的值
					for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) 
					{
						String columnName = null;
						if (useColumnName)
						{ columnName = resultSetMetaData.getColumnName(i); }
						else
						{ columnName = resultSetMetaData.getColumnLabel(i); }
						
						Object columnValue = record.get(columnName);
						
						String value = null;
						if (columnValue instanceof java.util.Date)
						{
							java.util.Date date = CommonUtil.getDate(columnValue);
							value = StringUtil.toString(date, dateFomat); 
						}
						else if (columnValue instanceof java.sql.Date)
						{
							java.sql.Date date = CommonUtil.getSqlDate(columnValue);
							value = StringUtil.toString(date, dateFomat); 
						}
						else if (columnValue instanceof java.sql.Timestamp)
						{
							java.sql.Timestamp date = CommonUtil.getTimestamp(columnValue);
							value = StringUtil.toString(date, dateFomat); 
						}
						else if (columnValue instanceof oracle.sql.TIMESTAMP)
						{
							oracle.sql.TIMESTAMP date = CommonUtil.getOracleTimestamp(columnValue);
							value = StringUtil.toString(date, dateFomat); 
						}
						else
						{ value = StringUtil.toString(columnValue); }

						line.append(value);
						
						if (i < resultSetMetaData.getColumnCount())
						{ line.append(delimiter); }
					}
					lines.add(line.toString());
				}
				else
				{ return Collections.emptyList(); }
			}
			return lines;
		}
		else
		{ return Collections.emptyList(); }
	}
	
	public static <K, V> Collection<V> getMapValues(final Map<K, V> map)
	{
		Collection<V> records = new ArrayList<V>();
		mapProcessor(map, (K key, V value, int indx) -> { records.add(value); });
		return records;
	}
	
	public static <K, V> Collection<V> getMapValues(final MultiKeyMap<K, V> map)
	{
		Collection<V> records = new ArrayList<>();
		mapProcessor(map, (final MultiKey<? extends K> key, final V value, final int indx) -> { records.add(value); });
		return records;
	}
	
	/**
	 * 针对Map做的通用型迭代处理
	 * */
	@SafeVarargs
	public static <K, V> boolean mapProcessor(final Map<K, V> map, final ItemProcessorForMap<K, V> ... itemProcessorForMaps)
	{		
		if (!isMapEmpty(map))
		{
			Iterator<Map.Entry<K, V>> entries = map.entrySet().iterator();
			int indx = 0;
			while (entries.hasNext())
			{
				Map.Entry<K, V> entry = entries.next();
				if (!ArrayUtil.isArrayEmpty(itemProcessorForMaps)) { for (ItemProcessorForMap<K, V> itemProcessorForMap : itemProcessorForMaps) { itemProcessorForMap.process(entry.getKey(), entry.getValue(), indx); } }
				indx++;
			}
			
			return true;
		}
		else
		{ return false; }
	}
	
	@SafeVarargs
	public static <K, V> boolean mapProcessor(final MultiKeyMap<K, V> map, final ItemProcessorForMultiKeyMap<K, V> ... itemProcessorForMultiKeyMaps)
	{
		if (!isMapEmpty(map))
		{
			Iterator<Entry<MultiKey<? extends K>, V>> entries = map.entrySet().iterator();
			int indx = 0;
			while (entries.hasNext())
			{
				Entry<MultiKey<? extends K>, V> entry = entries.next();
				if (!ArrayUtil.isArrayEmpty(itemProcessorForMultiKeyMaps)) 
				{
					for (ItemProcessorForMultiKeyMap<K, V> itemProcessorForMultiKeyMap : itemProcessorForMultiKeyMaps) 
					{ itemProcessorForMultiKeyMap.process(entry.getKey(), entry.getValue(), indx); }
				}
				indx++;
			}
			
			return true;
		}
		else
		{ return false; }
	}
	
	/**
	 * 针对Collection做的通用型迭代处理
	 * */
	@SafeVarargs
	public static <T> boolean collectionProcessor(final Collection<T> values, final ItemProcessorForCollection<T> ... itemProcessorForCollections)
	{
		if (!isCollectionEmpty(values))
		{
			int indx = 0;
			int length = values.size();
			for (T value : values)
			{
				if (!ArrayUtil.isArrayEmpty(itemProcessorForCollections)) 
				{
					for (ItemProcessorForCollection<T> itemProcessorForCollection : itemProcessorForCollections) 
					{ itemProcessorForCollection.process(value, indx, length); }
				}
				indx++;
			}
			
			return true;
		}
		else
		{ return false; }
	}
	
	@SafeVarargs
	public static <K, V> boolean collectionProcessor(final Collection<Map<K, V>> records, final ItemProcessorForMap<K, V> ... itemProcessorForMaps)
	{
		return collectionProcessor
		(
				records, 
				(final Map<K, V> record, final int indx, final int length) -> 
				{ mapProcessor(record, itemProcessorForMaps); }
		);
	}
	
	@SafeVarargs
	public static <K, V> boolean collectionProcessor(final Collection<MultiKeyMap<K, V>> records, final ItemProcessorForMultiKeyMap<K, V> ... itemProcessorForMultiKeyMaps)
	{
		return collectionProcessor
		(
				records,
				(final MultiKeyMap<K, V> record, final int indx, final int length) -> 
				{ mapProcessor(record, itemProcessorForMultiKeyMaps); }
		);
	}
	
	public static <T> Collection<T> collectionOperation(final Collection<T> a, final Collection<T> b, final OperationType operationType)
	{
		if (isCollectionEmpty(a) || isCollectionEmpty(b) || null == operationType)
		{ return Collections.emptyList(); }
		
		switch (operationType)
		{
			case INTER_SECTION:
				return CollectionUtils.intersection(a, b);
				
			case LEFT_SUBTRACT:
				return CollectionUtils.subtract(a, b);
				
			case RIGHT_SUBTRACT:
				return CollectionUtils.subtract(b, a);
				
			case UNION:
				return CollectionUtils.union(a, b);
				
			default:
				return Collections.emptyList();
		}
	}
	
	/**
	 * List转Set
	 * */
	public static <T> Set<T> toSet(final List<T> list)
	{
		if (isCollectionEmpty(list))
		{ return Collections.emptySet(); }
		
		return new HashSet<>(list);
	}
	
	/**
	 * Set转List
	 * */
	public static <T> List<T> toList(final Set<T> set)
	{
		if (isCollectionEmpty(set))
		{ return Collections.emptyList(); }
		
		return new ArrayList<>(set);
	}
	
	public static <K, V> Map<K, V> toMap(final MultiKeyMap<K, V> map, final int keyIndex)
	{
		if (isMapEmpty(map))
		{ return Collections.emptyMap(); }
		
		Map<K, V> result = new HashMap<>();
		mapProcessor
		(
				map, 
				(final MultiKey<? extends K> key, final V value, final int indx) -> 
				{ result.put(key.getKey(keyIndex), value); }
		);
		return result;
	}
	
	public static Collection<Map<String, Column>> toMaps(final Table table, final int keyIndex)
	{
		if (null == table || isCollectionEmpty(table.getColumns()))
		{ return Collections.emptyList(); }
		
		List<Map<String, Column>> result = new ArrayList<>();
		collectionProcessor
		(
				table.getColumns(),
				(final MultiKeyMap<String, Column> value, final int indx, final int length) ->
				{ result.add(toMap(value, keyIndex)); }
		);
		return result;
	}
	
	public static <V> Map<String, V> mapKeyToUpperCase(final Map<String, V> map)
	{
		Map<String, V> result = new HashMap<>();
		mapProcessor
		(
				map, 
				(final String key, final V value, final int indx) -> 
				{ result.put(key.toUpperCase(), value); }
		);
		return result;
	}
	
	public static <V> Map<String, V> mapKeyToLowerCase(final Map<String, V> map)
	{
		Map<String, V> result = new HashMap<>();
		mapProcessor
		(
				map, 
				(final String key, final V value, final int indx) -> 
				{ result.put(key.toLowerCase(), value); }
		);
		return result;
	}
	
	public enum OperationType 
	{
		INTER_SECTION, 		//交集
	    LEFT_SUBTRACT, 		//以左边集合为准的差集
	    RIGHT_SUBTRACT,		//以右边集合为准的差集
	    UNION;
	}
	
	@FunctionalInterface
	public interface ItemProcessorForMap<K, V>
	{ void process(final K key, final V value, final int indx); }
	
	@FunctionalInterface
	public interface ItemProcessorForMultiKeyMap<K, V>
	{ void process(final MultiKey<? extends K> key, final V value, final int indx); }
	
	@FunctionalInterface
	public interface ItemProcessorForCollection<T>
	{ void process(final T value, final int indx, final int length); }
}