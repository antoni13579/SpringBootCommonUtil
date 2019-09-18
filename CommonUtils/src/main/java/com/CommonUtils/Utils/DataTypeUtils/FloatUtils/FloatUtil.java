package com.CommonUtils.Utils.DataTypeUtils.FloatUtils;

@Deprecated
public final class FloatUtil 
{
	private FloatUtil() {}
	
	/**建议使用cn.hutool.core.convert.Convert.toFloat*/ 
	public static <T> float getFloat(final T obj) throws Exception
	{
		if (obj instanceof Float)
		{ return Float.parseFloat(obj.toString()); }
		else
		{ throw new Exception("无法转换为float类型"); }
	}
}