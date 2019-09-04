package com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.CustomCollections;

import java.util.Map;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class ConcurrentHashMap<K, V>
{
	private Map<K, V> map = null;
	
	public ConcurrentHashMap()
	{ this.map = new java.util.concurrent.ConcurrentHashMap<>(); }
	
	public ConcurrentHashMap<K, V> put(final K key, final V value)
	{
		this.map.put(key, value);
		return this;
	}
}