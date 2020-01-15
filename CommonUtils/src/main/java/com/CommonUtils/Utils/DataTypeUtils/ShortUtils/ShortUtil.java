package com.CommonUtils.Utils.DataTypeUtils.ShortUtils;

/**
 * 已过时
 * @deprecated
 * */
@Deprecated(since="已过时")
public final class ShortUtil 
{
	private ShortUtil() {}
	
	/**建议使用cn.hutool.core.convert.Convert.toShort*/ 
	public static <T> short getShort(final T obj) throws ShortUtilException
	{
		if (obj instanceof Short)
		{ return Short.parseShort(obj.toString()); }
		else
		{ throw new ShortUtilException("无法转换为short类型"); }
	}
	
	private static class ShortUtilException extends Exception
	{
		private static final long serialVersionUID = -5949807717167994307L;

		private ShortUtilException(final String message)
		{ super(message); }
	}
}