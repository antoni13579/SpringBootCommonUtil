package com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.CustomCollections;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class ArrayList<T>
{
	private List<T> list = null;
	
	public ArrayList()
	{ this.list = new java.util.ArrayList<>(); }
	
	public ArrayList<T> add(T value)
	{
		this.list.add(value);
		return this;
	}
}