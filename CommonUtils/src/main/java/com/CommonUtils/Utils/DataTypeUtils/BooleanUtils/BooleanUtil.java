package com.CommonUtils.Utils.DataTypeUtils.BooleanUtils;

@Deprecated
public final class BooleanUtil 
{
	private BooleanUtil() {}
	
	/**建议使用cn.hutool.core.convert.Convert.toBool*/ 
	public static boolean getBoolean(final Object obj) throws Exception
	{
		if (obj instanceof Boolean)
		{ return (boolean)obj; }
		else
		{ throw new Exception("无法转换为boolean类型"); }
	}
}