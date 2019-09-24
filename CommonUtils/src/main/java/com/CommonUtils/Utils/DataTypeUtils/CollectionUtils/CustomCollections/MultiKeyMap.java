package com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.CustomCollections;

import cn.hutool.core.util.ArrayUtil;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@ToString
@Slf4j
public class MultiKeyMap<K, V>
{
	private org.apache.commons.collections4.map.MultiKeyMap<K, V> map = null;
	
	public MultiKeyMap()
	{ this.map = new org.apache.commons.collections4.map.MultiKeyMap<>(); }
	
	public MultiKeyMap<K, V> put(final V value, @SuppressWarnings("unchecked") final K ... keys)
	{
		if (!ArrayUtil.isEmpty(keys))
		{
			if (keys.length == 1)
			{ log.warn("设置MultiKeyMap的值，key至少要两个才行，请注意处理"); }
			else if (keys.length == 2)
			{ this.map.put(keys[0], keys[1], value); }
			else if (keys.length == 3)
			{ this.map.put(keys[0], keys[1], keys[2], value); }
			else if (keys.length == 4)
			{ this.map.put(keys[0], keys[1], keys[2], keys[3], value); }
			else if (keys.length == 5)
			{ this.map.put(keys[0], keys[1], keys[2], keys[3], keys[4], value); }
			else
			{ log.warn("设置MultiKeyMap的值，key超过5个，请注意处理"); }
		}
		
		return this;
	}
}