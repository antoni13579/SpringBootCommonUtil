package com.CommonUtils.Utils.DataTypeUtils.ArrayUtils;

import com.CommonUtils.Utils.DataTypeUtils.StringUtils.StringUtil;

import cn.hutool.core.util.StrUtil;

@Deprecated
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
	{
		if (null == array || array.length == 0)
		{ return true; }
		
		return false;
	}
	
	/**
	 * 检测Array是否为空，true为空，false为非空
	 * */
	/**建议使用cn.hutool.core.util.ArrayUtil.isEmpty或isNotEmpty*/ 
	public static boolean isArrayEmpty(final int[] array)
	{
		if (null == array || array.length == 0)
		{ return true; }
		
		return false;
	}
	
	/**
	 * 检测Array是否为空，true为空，false为非空
	 * */
	/**建议使用cn.hutool.core.util.ArrayUtil.isEmpty或isNotEmpty*/ 
	public static boolean isArrayEmpty(final boolean[] array)
	{
		if (null == array || array.length == 0)
		{ return true; }
		
		return false;
	}
	
	/**
	 * 检测Array是否为空，true为空，false为非空
	 * */
	/**建议使用cn.hutool.core.util.ArrayUtil.isEmpty或isNotEmpty*/ 
	public static boolean isArrayEmpty(final byte[] array)
	{
		if (null == array || array.length == 0)
		{ return true; }
		
		return false;
	}
	
	/**
	 * 检测Array是否为空，true为空，false为非空
	 * */
	/**建议使用cn.hutool.core.util.ArrayUtil.isEmpty或isNotEmpty*/ 
	public static boolean isArrayEmpty(final char[] array)
	{
		if (null == array || array.length == 0)
		{ return true; }
		
		return false;
	}
	
	/**
	 * 检测Array是否为空，true为空，false为非空
	 * */
	/**建议使用cn.hutool.core.util.ArrayUtil.isEmpty或isNotEmpty*/ 
	public static boolean isArrayEmpty(final long[] array)
	{
		if (null == array || array.length == 0)
		{ return true; }
		
		return false;
	}
	
	/**建议使用cn.hutool.core.util.ArrayUtil.isEmpty或isNotEmpty*/ 
	public static boolean isArrayEmpty(final double[] array)
	{
		if (null == array || array.length == 0)
		{ return true; }
		
		return false;
	}
	
	public static boolean arrayEquals(final String[] array1, final String[] array2)
    {
    	//两个数组都是空，则为相等
    	if (isArrayEmpty(array1) && isArrayEmpty(array2))
    	{ return true; }
    	
    	//【一个不是空，另一个是，不是相等】 或 【 一个是空，另一个不是，不是相等】
    	else if ((!isArrayEmpty(array1) && isArrayEmpty(array2)) || 
    			 (isArrayEmpty(array1) && !isArrayEmpty(array2)))
    	{ return false; }
    	
    	//两个都不是空
    	else
    	{
    		if (array1.length != array2.length)
    		{ return false; }
    		
    		int matched = 0;
    		for (String val1 : array1)
    		{
    			for (String val2 : array2)
    			{
    				if (StrUtil.equals(val1, val2, false))
    				{ matched++; }
    			}
    		}
    		
    		if (matched == array1.length)
    		{ return true; }
    		
    		return false;
    	}
    }
	
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
	public static <T> String[] getStringArray(final T obj) throws Exception
	{
		if (obj instanceof String[])
		{ return (String[])obj; }
		else
		{ throw new Exception("无法转换为String[]类型"); }
	}
	
	/**建议使用cn.hutool.core.convert.Convert.toIntArray*/ 
	public static <T> Integer[] getIntegerArrayForWrapperClass(final T obj) throws Exception
	{
		if (obj instanceof Integer[])
		{ return (Integer[])obj; }
		else
		{ throw new Exception("无法转换为Integer[]类型"); }
	}
	
	/**建议使用cn.hutool.core.convert.Convert.convert(int[].class, obj)*/ 
	public static <T> int[] getIntegerArray(final T obj) throws Exception
	{
		if (obj instanceof int[])
		{ return (int[])obj; }
		else
		{ throw new Exception("无法转换为int[]类型"); }
	}
	
	/**建议使用cn.hutool.core.convert.Convert.toLongArray*/ 
	public static <T> Long[] getLongArrayForWrapperClass(final T obj) throws Exception
	{
		if (obj instanceof Long[])
		{ return (Long[])obj; }
		else
		{ throw new Exception("无法转换为Long[]类型"); }
	}
	
	/**建议使用cn.hutool.core.convert.Convert.convert(long[].class, obj)*/ 
	public static <T> long[] getLongArray(final T obj) throws Exception
	{
		if (obj instanceof long[])
		{ return (long[])obj; }
		else
		{ throw new Exception("无法转换为long[]类型"); }
	}
	
	@FunctionalInterface
	public interface ItemProcessor<T>
	{ void process(final T value, final int indx, final int length); }
}