package com.CommonUtils.Utils.DataTypeUtils.IntegerUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.ToIntFunction;

import com.CommonUtils.Utils.DataTypeUtils.ArrayUtils.ArrayUtil;
import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;

public final class IntegerUtil 
{
	private IntegerUtil() {}
	
	public static <T> int sum(final Integer ... params)
	{ return sum((x) -> { return x; }, params); }
	
	public static <T> int sum(final Collection<Integer> coll)
	{ return sum((x) -> { return x; }, coll); }
	
	public static <T> int sum(final ToIntFunction<? super T> mapper, final Collection<T> coll)
	{
		if (!JavaCollectionsUtil.isCollectionEmpty(coll))
		{ return coll.stream().mapToInt(mapper).sum(); }
		else
		{ return 0; }
	}
	
	@SafeVarargs
	public static <T> int sum(final ToIntFunction<? super T> mapper, final T ... params)
	{
		if (!ArrayUtil.isArrayEmpty(params))
		{ return sum(mapper, Arrays.asList(params)); }
		else
		{ return 0; }
	}
	
	public static <T> int getInteger(final T obj) throws Exception
	{
		if (obj instanceof Integer)
		{ return Integer.parseInt(obj.toString()); }
		else
		{ throw new Exception("无法转换为int类型"); }
	}
}