package com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.CustomCollections;

import java.util.Map;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class HashMap<K, V>
{
	private Map<K, V> map = null;
	
	public HashMap()
	{ this.map = new java.util.HashMap<>(); }

	public HashMap<K, V> put(final K key, final V value)
	{
		this.map.put(key, value);
		return this;
	}
}