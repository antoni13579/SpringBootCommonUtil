package com.CommonUtils.Utils.DataTypeUtils.FloatUtils;

public final class FloatUtil 
{
	private FloatUtil() {}
	
	public static <T> float getFloat(final T obj) throws Exception
	{
		if (obj instanceof Float)
		{ return Float.parseFloat(obj.toString()); }
		else
		{ throw new Exception("无法转换为float类型"); }
	}
}