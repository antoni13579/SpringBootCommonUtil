package com.CommonUtils.Utils.DataTypeUtils.LongUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.ToLongFunction;

import com.CommonUtils.Utils.DataTypeUtils.ArrayUtils.ArrayUtil;
import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;

public final class LongUtil 
{
	private LongUtil() {}
	
	public static <T> long sum(final Long ... params)
	{ return sum((x) -> { return x; }, params); }
	
	public static <T> long sum(final Collection<Long> coll)
	{ return sum((x) -> { return x; }, coll); }
	
	public static <T> long sum(final ToLongFunction<? super T> mapper, final Collection<T> coll)
	{
		if (!JavaCollectionsUtil.isCollectionEmpty(coll))
		{ return coll.stream().mapToLong(mapper).sum(); }
		else
		{ return 0L; }
	}
	
	@SafeVarargs
	public static <T> long sum(final ToLongFunction<? super T> mapper, final T ... params)
	{
		if (!ArrayUtil.isArrayEmpty(params))
		{ return sum(mapper, Arrays.asList(params)); }
		else
		{ return 0L; }
	}
	
	public static <T> long getLong(final T obj) throws Exception
	{
		if (obj instanceof Long)
		{ return Long.parseLong(obj.toString()); }
		else
		{ throw new Exception("无法转换为long类型"); }
	}
}