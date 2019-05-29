package com.CommonUtils.Utils.CollectionUtils.CustomCollections;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class Properties 
{
	private java.util.Properties properties = null;
	
	public Properties()
	{ this.properties = new java.util.Properties(); }
	
	public Properties setProperty(final String key, final String value)
	{
		this.properties.setProperty(key, value);
		return this;
	}
	
	public Properties put(final Object key, final Object value)
	{
		this.properties.put(key, value);
		return this;
	}
}