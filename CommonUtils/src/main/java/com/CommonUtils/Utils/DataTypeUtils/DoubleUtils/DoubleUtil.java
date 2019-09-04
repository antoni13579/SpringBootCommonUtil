package com.CommonUtils.Utils.DataTypeUtils.DoubleUtils;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.ToDoubleFunction;

import com.CommonUtils.Utils.DataTypeUtils.ArrayUtils.ArrayUtil;
import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;
import com.CommonUtils.Utils.DataTypeUtils.StringUtils.StringUtil;

public final class DoubleUtil 
{
	private DoubleUtil() {}
	
	public static String formatDoubleToStr(final double number, final String format)
	{
		if (StringUtil.isStrEmpty(format))
		{ return ""; }
		
		return new DecimalFormat(format).format(number);
	}
	
	public static <T> double sum(final Double ... params)
	{ return sum((x) -> { return x; }, params); }
	
	public static <T> double sum(final Collection<Double> coll)
	{ return sum((x) -> { return x; }, coll); }
	
	public static <T> double sum(final ToDoubleFunction<? super T> mapper, final Collection<T> coll)
	{
		if (!JavaCollectionsUtil.isCollectionEmpty(coll))
		{ return coll.stream().mapToDouble(mapper).sum(); }
		else
		{ return 0D; }
	}
	
	@SafeVarargs
	public static <T> double sum(final ToDoubleFunction<? super T> mapper, final T ... params)
	{
		if (!ArrayUtil.isArrayEmpty(params))
		{ return sum(mapper, Arrays.asList(params)); }
		else
		{ return 0D; }
	}
	
	public static <T> double getDouble(final T obj) throws Exception
	{
		if (obj instanceof Double)
		{ return Double.parseDouble(obj.toString()); }
		else
		{ throw new Exception("无法转换为double类型"); }
	}
}