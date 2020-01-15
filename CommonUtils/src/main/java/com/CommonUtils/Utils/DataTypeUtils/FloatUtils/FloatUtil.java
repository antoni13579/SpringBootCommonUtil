package com.CommonUtils.Utils.DataTypeUtils.FloatUtils;

/**
 * 已过时
 * @deprecated
 * */
@Deprecated(since="已过时")
public final class FloatUtil 
{
	private FloatUtil() {}
	
	/**建议使用cn.hutool.core.convert.Convert.toFloat*/ 
	public static <T> float getFloat(final T obj) throws FloatUtilException
	{
		if (obj instanceof Float)
		{ return Float.parseFloat(obj.toString()); }
		else
		{ throw new FloatUtilException("无法转换为float类型"); }
	}
	
	private static class FloatUtilException extends Exception
	{
		private static final long serialVersionUID = -274319824983057878L;

		private FloatUtilException(final String message)
		{ super(message); }
	}
}