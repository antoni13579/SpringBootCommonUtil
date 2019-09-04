package com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.CustomCollections;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class CopyOnWriteArrayList<T>
{
	private List<T> list = null;
	
	public CopyOnWriteArrayList()
	{ this.list = new java.util.concurrent.CopyOnWriteArrayList<>(); }
	
	public CopyOnWriteArrayList<T> add(T value)
	{
		this.list.add(value);
		return this;
	}
}