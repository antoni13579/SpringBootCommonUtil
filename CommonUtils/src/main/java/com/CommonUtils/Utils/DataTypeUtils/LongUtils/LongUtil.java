package com.CommonUtils.Utils.DataTypeUtils.LongUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;

public final class LongUtil 
{
	private LongUtil() {}
	
	public static LongSummaryStatistics aggregation(final Long ... params)
	{
		Map<String, LongSummaryStatistics> map = groupBy((x) -> { return "ALL_AGGREGATION"; }, (x) -> { return x; }, params);
		if (!CollUtil.isEmpty(map))
		{ return map.get("ALL_AGGREGATION"); }
		else
		{ return new LongSummaryStatistics(); }
	}
	
	public static LongSummaryStatistics aggregation(final Collection<Long> coll)
	{
		Map<String, LongSummaryStatistics> map = groupBy((x) -> { return "ALL_AGGREGATION"; }, (x) -> { return x; }, coll);
		if (!CollUtil.isEmpty(map))
		{ return map.get("ALL_AGGREGATION"); }
		else
		{ return new LongSummaryStatistics(); }
	}
	
	@SafeVarargs
	public static <T> Map<String, LongSummaryStatistics> groupBy(final Function<? super T, String> classifier, 
																 final ToLongFunction<? super T> mapper, 
			   													 final T ... params)
	{
		if (!ArrayUtil.isEmpty(params))
		{ return groupBy(classifier, mapper, Arrays.asList(params)); }
		else
		{ return Collections.emptyMap(); }
	}
	
	public static <T> Map<String, LongSummaryStatistics> groupBy(final Function<? super T, String> classifier, 
																 final ToLongFunction<? super T> mapper, 
																 final Collection<T> coll)
	{
		if (!CollUtil.isEmpty(coll))
		{ return coll.stream().collect(Collectors.groupingBy(classifier, Collectors.summarizingLong(mapper))); }
		else
		{ return Collections.emptyMap(); }
	}
	
	/**建议使用cn.hutool.core.convert.Convert.toLong*/ 
	@Deprecated
	public static <T> long getLong(final T obj) throws Exception
	{
		if (obj instanceof Long)
		{ return Long.parseLong(obj.toString()); }
		else
		{ throw new Exception("无法转换为long类型"); }
	}
}