package com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.CustomCollections;

import java.util.Set;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class HashSet<T>
{
	private Set<T> set = null;
	
	public HashSet()
	{ this.set = new java.util.HashSet<>(); }
	
	public HashSet<T> add(T value)
	{
		this.set.add(value);
		return this;
	}
}