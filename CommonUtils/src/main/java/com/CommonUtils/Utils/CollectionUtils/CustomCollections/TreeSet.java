package com.CommonUtils.Utils.CollectionUtils.CustomCollections;

import java.util.Set;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class TreeSet<T>
{
	private Set<T> set = null;
	
	public TreeSet()
	{ this.set = new java.util.TreeSet<>(); }
	
	public TreeSet<T> add(T value)
	{
		this.set.add(value);
		return this;
	}
}