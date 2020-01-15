package com.CommonUtils.Utils.DataTypeUtils.ArrayUtils;

import com.CommonUtils.Utils.DataTypeUtils.StringUtils.StringUtil;

/**
 * 这个数组工具类不用了，交由hutool处理
 * @deprecated
 * */
@Deprecated(since="这个数组工具类不用了，交由hutool处理")
public final class ArrayUtil 
{
	private ArrayUtil() {}
	
	public static String toString(final String[] values, final String separator)
	{
		if (null != values && values.length > 0 && !StringUtil.isStrEmpty(separator))
		{
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < values.length; i++)
			{
				sb.append(values[i]);
				
				if (i != values.length - 1)
				{ sb.append(separator); }
			}
			
			return sb.toString();
		}
		
		return "";
	}
	
	/**
	 * 检测Array是否为空，true为空，false为非空
	 * */
	/**建议使用cn.hutool.core.util.ArrayUtil.isEmpty或isNotEmpty*/ 
	public static <T> boolean isArrayEmpty(final T[] array)
	{ return null == array || array.length == 0; }
	
	/**
	 * 检测Array是否为空，true为空，false为非空
	 * */
	/**建议使用cn.hutool.core.util.ArrayUtil.isEmpty或isNotEmpty*/ 
	public static boolean isArrayEmpty(final int[] array)
	{ return null == array || array.length == 0; }
	
	/**
	 * 检测Array是否为空，true为空，false为非空
	 * */
	/**建议使用cn.hutool.core.util.ArrayUtil.isEmpty或isNotEmpty*/ 
	public static boolean isArrayEmpty(final boolean[] array)
	{ return null == array || array.length == 0; }
	
	/**
	 * 检测Array是否为空，true为空，false为非空
	 * */
	/**建议使用cn.hutool.core.util.ArrayUtil.isEmpty或isNotEmpty*/ 
	public static boolean isArrayEmpty(final byte[] array)
	{ return null == array || array.length == 0; }
	
	/**
	 * 检测Array是否为空，true为空，false为非空
	 * */
	/**建议使用cn.hutool.core.util.ArrayUtil.isEmpty或isNotEmpty*/ 
	public static boolean isArrayEmpty(final char[] array)
	{ return null == array || array.length == 0; }
	
	/**
	 * 检测Array是否为空，true为空，false为非空
	 * */
	/**建议使用cn.hutool.core.util.ArrayUtil.isEmpty或isNotEmpty*/ 
	public static boolean isArrayEmpty(final long[] array)
	{ return null == array || array.length == 0; }
	
	/**建议使用cn.hutool.core.util.ArrayUtil.isEmpty或isNotEmpty*/ 
	public static boolean isArrayEmpty(final double[] array)
	{ return null == array || array.length == 0; }
	
	/**建议用CollUtil.newArrayList转换为集合，再使用JavaCollectionsUtil.collectionProcessor*/
	@SafeVarargs
	public static <T> boolean arrayProcessor(final T[] array, final ItemProcessor<T> ... itemProcessors)
	{
		if (!isArrayEmpty(array))
		{
			int length = array.length;
			for (int i = 0; i < length; i++)
			{
				if (!isArrayEmpty(itemProcessors))
				{
					for (ItemProcessor<T> itemProcessor : itemProcessors)
					{ itemProcessor.process(array[i], i, length); }
				}
			}
			
			return true;
		}
		else
		{ return false; }
	}
	
	/**建议用CollUtil.newArrayList转换为集合，再使用JavaCollectionsUtil.collectionProcessor*/
	@SafeVarargs
	public static boolean arrayProcessor(final byte[] array, final ItemProcessor<Byte> ... itemProcessors)
	{
		if (!isArrayEmpty(array))
		{
			int length = array.length;
			for (int i = 0; i < length; i++)
			{
				if (!isArrayEmpty(itemProcessors))
				{
					for (ItemProcessor<Byte> itemProcessor : itemProcessors)
					{ itemProcessor.process(array[i], i, length); }
				}
			}
			
			return true;
		}
		else
		{ return false; }
	}
	
	/**建议使用cn.hutool.core.convert.Convert.toStrArray*/ 
	public static <T> String[] getStringArray(final T obj) throws ArrayUtilException
	{
		if (obj instanceof String[])
		{ return (String[])obj; }
		else
		{ throw new ArrayUtilException("无法转换为String[]类型"); }
	}
	
	/**建议使用cn.hutool.core.convert.Convert.toIntArray*/ 
	public static <T> Integer[] getIntegerArrayForWrapperClass(final T obj) throws ArrayUtilException
	{
		if (obj instanceof Integer[])
		{ return (Integer[])obj; }
		else
		{ throw new ArrayUtilException("无法转换为Integer[]类型"); }
	}
	
	/**建议使用cn.hutool.core.convert.Convert.convert(int[].class, obj)*/ 
	public static <T> int[] getIntegerArray(final T obj) throws ArrayUtilException
	{
		if (obj instanceof int[])
		{ return (int[])obj; }
		else
		{ throw new ArrayUtilException("无法转换为int[]类型"); }
	}
	
	/**建议使用cn.hutool.core.convert.Convert.toLongArray*/ 
	public static <T> Long[] getLongArrayForWrapperClass(final T obj) throws ArrayUtilException
	{
		if (obj instanceof Long[])
		{ return (Long[])obj; }
		else
		{ throw new ArrayUtilException("无法转换为Long[]类型"); }
	}
	
	/**建议使用cn.hutool.core.convert.Convert.convert(long[].class, obj)*/ 
	public static <T> long[] getLongArray(final T obj) throws ArrayUtilException
	{
		if (obj instanceof long[])
		{ return (long[])obj; }
		else
		{ throw new ArrayUtilException("无法转换为long[]类型"); }
	}
	
	@FunctionalInterface
	public interface ItemProcessor<T>
	{ void process(final T value, final int indx, final int length); }
	
	private static class ArrayUtilException extends Exception
	{
		private static final long serialVersionUID = 4357883017610463370L;

		private ArrayUtilException(final String message)
		{ super(message); }
	}
}