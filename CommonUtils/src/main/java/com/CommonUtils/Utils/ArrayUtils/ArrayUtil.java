package com.CommonUtils.Utils.ArrayUtils;

import com.CommonUtils.Utils.StringUtils.StringUtil;

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
	public static <T> boolean isArrayEmpty(final T[] array)
	{
		if (null == array || array.length == 0)
		{ return true; }
		
		return false;
	}
	
	/**
	 * 检测Array是否为空，true为空，false为非空
	 * */
	public static boolean isArrayEmpty(final int[] array)
	{
		if (null == array || array.length == 0)
		{ return true; }
		
		return false;
	}
	
	/**
	 * 检测Array是否为空，true为空，false为非空
	 * */
	public static boolean isArrayEmpty(final boolean[] array)
	{
		if (null == array || array.length == 0)
		{ return true; }
		
		return false;
	}
	
	/**
	 * 检测Array是否为空，true为空，false为非空
	 * */
	public static boolean isArrayEmpty(final byte[] array)
	{
		if (null == array || array.length == 0)
		{ return true; }
		
		return false;
	}
	
	/**
	 * 检测Array是否为空，true为空，false为非空
	 * */
	public static boolean isArrayEmpty(final char[] array)
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
    				if (StringUtil.strEquals(val1, val2, false))
    				{ matched++; }
    			}
    		}
    		
    		if (matched == array1.length)
    		{ return true; }
    		
    		return false;
    	}
    }
	
	@SafeVarargs
	public static <T> boolean arrayProcessor(final T[] array, final ItemProcessor<T> ... itemProcessors)
	{
		if (!isArrayEmpty(array))
		{
			for (int i = 0; i < array.length; i++)
			{
				if (!isArrayEmpty(itemProcessors))
				{
					for (ItemProcessor<T> itemProcessor : itemProcessors)
					{ itemProcessor.process(array[i], i); }
				}
			}
			
			return true;
		}
		else
		{ return false; }
	}
	
	@SafeVarargs
	public static boolean arrayProcessor(final byte[] array, final ItemProcessor<Byte> ... itemProcessors)
	{
		if (!isArrayEmpty(array))
		{
			for (int i = 0; i < array.length; i++)
			{
				if (!isArrayEmpty(itemProcessors))
				{
					for (ItemProcessor<Byte> itemProcessor : itemProcessors)
					{ itemProcessor.process(array[i], i); }
				}
			}
			
			return true;
		}
		else
		{ return false; }
	}
	
	@FunctionalInterface
	public interface ItemProcessor<T>
	{ void process(final T value, final int indx); }
}