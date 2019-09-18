package com.CommonUtils.Utils.DataTypeUtils.ShortUtils;

@Deprecated
public final class ShortUtil 
{
	private ShortUtil() {}
	
	/**建议使用cn.hutool.core.convert.Convert.toShort*/ 
	public static <T> short getShort(final T obj) throws Exception
	{
		if (obj instanceof Short)
		{ return Short.parseShort(obj.toString()); }
		else
		{ throw new Exception("无法转换为short类型"); }
	}
}