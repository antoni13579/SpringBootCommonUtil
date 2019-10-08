package com.CommonUtils.Utils.DataTypeUtils.CollectionUtils;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;

import com.CommonUtils.Utils.DBUtils.Bean.DBTable.Column;
import com.CommonUtils.Utils.DBUtils.Bean.DBTable.Table;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class JavaCollectionsUtil 
{
	private JavaCollectionsUtil() {}
	
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
		
		if (!cn.hutool.core.util.ArrayUtil.isEmpty(keys))
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
	/**建议使用cn.hutool.core.map.MapUtil.isEmpty或isNotEmpty*/ 
	@Deprecated
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
	/**建议使用cn.hutool.core.collection.CollUtil.isEmpty或isNotEmpty*/ 
	@Deprecated
	public static <T> boolean isCollectionEmpty(final Collection<T> collection)
	{
		if (null == collection || collection.isEmpty())
		{ return true; }
		
		return false;
	}
	
	/*
	@Deprecated
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
    */
	
	/**建议使用cn.hutool.core.collection.CollUtil.get相关函数*/ 
	@Deprecated
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
	
	/**
	 * dateFomat参数建议使用cn.hutool.core.date.DatePattern来提供日期格式
	 * */
	public static <K, V> Collection<String> getMapValues(final Collection<Map<K, V>> records, 
														 final ResultSetMetaData resultSetMetaData, 
														 final String delimiter,
														 final FastDateFormat dateFomat,
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
						{ value = DateTime.of(Convert.toDate(columnValue)).toString(dateFomat); }
						else if (columnValue instanceof java.sql.Date)
						{ value = DateTime.of(new java.util.Date(Convert.convert(java.sql.Date.class, columnValue).getTime())).toString(dateFomat); }
						else if (columnValue instanceof java.sql.Timestamp)
						{ value = DateTime.of(new java.util.Date(Convert.convert(java.sql.Timestamp.class, columnValue).getTime())).toString(dateFomat); }
						else if (columnValue instanceof oracle.sql.TIMESTAMP)
						{ value = DateTime.of(new java.util.Date(Convert.convert(oracle.sql.TIMESTAMP.class, columnValue).timestampValue().getTime())).toString(dateFomat); }
						else
						{ value = ObjectUtil.toString(columnValue); }

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
	
	/**建议使用cn.hutool.core.collection.CollUtil.values*/ 
	@Deprecated
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
	 * 针对Map做的通用型迭代处理，建议使用cn.hutool.core.collection.CollUtil.forEach
	 * */
	@Deprecated
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
				if (!cn.hutool.core.util.ArrayUtil.isEmpty(itemProcessorForMaps)) { for (ItemProcessorForMap<K, V> itemProcessorForMap : itemProcessorForMaps) { itemProcessorForMap.process(entry.getKey(), entry.getValue(), indx); } }
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
				if (!cn.hutool.core.util.ArrayUtil.isEmpty(itemProcessorForMultiKeyMaps)) 
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
				if (!cn.hutool.core.util.ArrayUtil.isEmpty(itemProcessorForCollections)) 
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
	
	/*
	@SafeVarargs
	@Deprecated
	public static <T> boolean collectionProcessor(final Collection<T[]> records, final ItemProcessor<T> ... itemProcessors)
	{ return collectionProcessor(records, (final T[] values, final int indx, final int length) -> { ArrayUtil.arrayProcessor(values, itemProcessors); }); }
	*/
	
	public static <T> Collection<T> collectionOperation(final Set<T> a, final Set<T> b, final OperationType operationType)
	{		
		switch (operationType)
		{
			case INTER_SECTION:
				return CollUtil.intersection(a, b);
				
			case LEFT_SUBTRACT:
				//return CollectionUtils.subtract(a, b);
				return CollUtil.disjunction(a, b).stream().filter((val) -> { return a.contains(val); }).collect(Collectors.toList());
				
			case RIGHT_SUBTRACT:
				//return CollectionUtils.subtract(b, a);
				return CollUtil.disjunction(a, b).stream().filter((val) -> { return b.contains(val); }).collect(Collectors.toList());
			
			case UNION:
				return CollUtil.union(a, b);
				
			case DISJUNCTION:
				return CollUtil.disjunction(a, b);
				
			default:
				return Collections.emptyList();
		}
	}
	
	/**
	 * List转Set
	 * */
	@Deprecated
	public static <T> Set<T> toSet(final List<T> list, final CollectionType collectionType)
	{
		if (isCollectionEmpty(list))
		{ return Collections.emptySet(); }
		
		switch (collectionType)
		{
			case HashSet:
				return new HashSet<>(list);
			case TreeSet:
				return new TreeSet<>(list);
			case LinkedHashSet:
				return new LinkedHashSet<>(list);
			default:
				return new HashSet<>(list);
		}
	}
	
	/**
	 * Set转List
	 * */
	@Deprecated
	public static <T> List<T> toList(final Set<T> set, final CollectionType collectionType)
	{
		if (isCollectionEmpty(set))
		{ return Collections.emptyList(); }
		
		switch (collectionType)
		{
			case ArrayList:
				return new ArrayList<>(set);
			case LinkedList:
				return new LinkedList<>(set);
			default:
				return new ArrayList<>(set);
		}
	}
	
	@Deprecated
	public static <K, V> Map<K, V> toMap(final MultiKeyMap<K, V> map, final int keyIndex, final MapType mapType)
	{
		if (isMapEmpty(map))
		{ return Collections.emptyMap(); }
		
		Map<K, V> temp = null;
		switch (mapType)
		{
			case HashMap:
				temp = new HashMap<>();
				break;
			case TreeMap:
				temp = new TreeMap<>();
				break;
			case LinkedHashMap:
				temp = new LinkedHashMap<>();
				break;
			default:
				temp = new HashMap<>();
				break;
		}
		
		Map<K, V> result = temp;
		mapProcessor
		(
				map, 
				(final MultiKey<? extends K> key, final V value, final int indx) -> 
				{ result.put(key.getKey(keyIndex), value); }
		);
		return result;
	}
	
	@Deprecated
	public static Collection<Map<String, Column>> toMaps(final Table table, final int keyIndex, final MapType mapType, final CollectionType collectionType)
	{
		if (null == table || isCollectionEmpty(table.getColumns()))
		{ return Collections.emptyList(); }
		
		Collection<Map<String, Column>> temp = null;
		switch (collectionType)
		{
			case HashSet:
				temp = new HashSet<>();
				break;
			case TreeSet:
				temp = new TreeSet<>();
				break;
			case LinkedHashSet:
				temp = new LinkedHashSet<>();
				break;
			case ArrayList:
				temp = new ArrayList<>();
				break;
			case LinkedList:
				temp = new LinkedList<>();
				break;
			default:
				temp = new ArrayList<>();
				break;
		}
		
		Collection<Map<String, Column>> result = temp;
		collectionProcessor
		(
				table.getColumns(),
				(final MultiKeyMap<String, Column> value, final int indx, final int length) ->
				{ result.add(toMap(value, keyIndex, mapType)); }
		);
		return result;
	}
	
	/**建议使用cn.hutool.core.util.ObjectUtil.defaultIfNull*/ 
	@Deprecated
	public static <T> Collection<T> ifNullForCollection(final T val)
	{ return nvlForCollection(val); }
	
	/**建议使用cn.hutool.core.util.ObjectUtil.defaultIfNull*/ 
	@Deprecated
	public static <T> Collection<T> nvlForCollection(final T val)
	{
		Collection<T> result = null;
		try
		{
			if (null != val)
			{
				if (val instanceof HashSet)
				{
					HashSet<T> v = Convert.convert(new TypeReference<HashSet<T>>() {}, val);
					result = ObjectUtil.cloneByStream(v);
				}
				else if (val instanceof TreeSet)
				{
					TreeSet<T> v = Convert.convert(new TypeReference<TreeSet<T>>() {}, val);
					result = ObjectUtil.cloneByStream(v);
				}
				else if (val instanceof LinkedHashSet)
				{
					LinkedHashSet<T> v = Convert.convert(new TypeReference<LinkedHashSet<T>>() {}, val);
					result = ObjectUtil.cloneByStream(v);
				}
				else if (val instanceof ArrayList)
				{
					ArrayList<T> v = Convert.convert(new TypeReference<ArrayList<T>>() {}, val);
					result = ObjectUtil.cloneByStream(v);
				}
				else if (val instanceof LinkedList)
				{
					LinkedList<T> v = Convert.convert(new TypeReference<LinkedList<T>>() {}, val);
					result = ObjectUtil.cloneByStream(v);
				}
				else
				{ throw new Exception("执行Collection的NVL函数出现问题，主要是无法生成Collection"); }
			}
			else
			{ result = Collections.emptyList(); }
		}
		catch (Exception ex)
		{ log.error("执行Collection的NVL函数出现异常，异常原因为：", ex); }
		
		return result;
	}
	
	/**建议使用cn.hutool.core.util.ObjectUtil.defaultIfNull*/ 
	@Deprecated
	public static <K, T, V> Map<K, V> ifNullForMap(final T val)
	{ return nvlForMap(val); }
	
	/**建议使用cn.hutool.core.util.ObjectUtil.defaultIfNull*/ 
	@Deprecated
	public static <K, T, V> Map<K, V> nvlForMap(final T val)
	{
		Map<K, V> result = null;
		try
		{
			if (null != val)
			{
				if (val instanceof HashMap)
				{
					HashMap<K, V> v = Convert.convert(new TypeReference<HashMap<K, V>>() {}, val);
					result = ObjectUtil.cloneByStream(v);
				}
				else if (val instanceof TreeMap)
				{
					TreeMap<K, V> v = Convert.convert(new TypeReference<TreeMap<K, V>>() {}, val);
					result = ObjectUtil.cloneByStream(v);
				}
				else if (val instanceof LinkedHashMap)
				{
					LinkedHashMap<K, V> v = Convert.convert(new TypeReference<LinkedHashMap<K, V>>() {}, val);
					result = ObjectUtil.cloneByStream(v);
				}
				else
				{ throw new Exception("执行Map的NVL函数出现问题，主要是无法生成Map"); }
			}
			else
			{ result = Collections.emptyMap(); }
		}
		catch (Exception ex)
		{ log.error("执行Map的NVL函数出现异常，异常原因为：", ex); }
		return result;
	}
	
	@Deprecated
	public enum CollectionType
	{
		HashSet,
		TreeSet,
		LinkedHashSet,
		ArrayList,
		LinkedList
	}
	
	@Deprecated
	public enum MapType
	{
		HashMap,
		TreeMap,
		LinkedHashMap
	}
	
	public enum OperationType 
	{
		INTER_SECTION, 		//交集
	    LEFT_SUBTRACT, 		//以左边集合为准的差集
	    RIGHT_SUBTRACT,		//以右边集合为准的差集
	    UNION,
	    DISJUNCTION;
	}
	
	@Deprecated
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