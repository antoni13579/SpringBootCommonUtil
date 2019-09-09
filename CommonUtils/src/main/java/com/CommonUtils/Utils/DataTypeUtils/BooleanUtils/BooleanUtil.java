package com.CommonUtils.Utils.DataTypeUtils.BooleanUtils;

public final class BooleanUtil 
{
	private BooleanUtil() {}
	
	public static <T> boolean getBoolean(final T obj) throws Exception
	{
		if (obj instanceof Boolean)
		{ return (boolean)obj; }
		else
		{ throw new Exception("无法转换为boolean类型"); }
	}
}