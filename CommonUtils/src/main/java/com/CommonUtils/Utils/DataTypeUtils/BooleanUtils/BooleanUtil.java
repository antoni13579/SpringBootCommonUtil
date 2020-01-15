package com.CommonUtils.Utils.DataTypeUtils.BooleanUtils;

/**
 * @deprecated
 * */
@Deprecated(since="")
public final class BooleanUtil 
{
	private BooleanUtil() {}
	
	/**建议使用cn.hutool.core.convert.Convert.toBool*/ 
	public static boolean getBoolean(final Object obj) throws BooleanUtilException
	{
		if (obj instanceof Boolean)
		{ return (boolean)obj; }
		else
		{ throw new BooleanUtilException("无法转换为boolean类型"); }
	}
	
	private static class BooleanUtilException extends Exception
	{
		private static final long serialVersionUID = -6580261465261349462L;

		private BooleanUtilException(final String message)
		{ super(message); }
	}
}