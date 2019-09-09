package com.CommonUtils.Utils.DataTypeUtils.ShortUtils;

public final class ShortUtil 
{
	private ShortUtil() {}
	
	public static <T> short getShort(final T obj) throws Exception
	{
		if (obj instanceof Short)
		{ return Short.parseShort(obj.toString()); }
		else
		{ throw new Exception("无法转换为short类型"); }
	}
}