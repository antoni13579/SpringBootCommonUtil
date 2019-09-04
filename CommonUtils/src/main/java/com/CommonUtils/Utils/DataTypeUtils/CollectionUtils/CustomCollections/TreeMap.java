package com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.CustomCollections;

import java.util.Map;

public final class TreeMap<K, V>
{
	private Map<K, V> map = null;
	
	public TreeMap()
	{ this.map = new java.util.TreeMap<>(); }
	
	public TreeMap<K, V> put(final K key, final V value)
	{
		this.map.put(key, value);
		return this;
	}
}