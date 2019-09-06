package com.CommonUtils.Utils.DataTypeUtils.LongUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

import com.CommonUtils.Utils.DataTypeUtils.ArrayUtils.ArrayUtil;
import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;

public final class LongUtil 
{
	private LongUtil() {}
	
	public static LongSummaryStatistics aggregation(final Long ... params)
	{
		Map<String, LongSummaryStatistics> map = groupBy((x) -> { return "ALL_AGGREGATION"; }, (x) -> { return x; }, params);
		if (!JavaCollectionsUtil.isMapEmpty(map))
		{ return map.get("ALL_AGGREGATION"); }
		else
		{ return new LongSummaryStatistics(); }
	}
	
	public static LongSummaryStatistics aggregation(final Collection<Long> coll)
	{
		Map<String, LongSummaryStatistics> map = groupBy((x) -> { return "ALL_AGGREGATION"; }, (x) -> { return x; }, coll);
		if (!JavaCollectionsUtil.isMapEmpty(map))
		{ return map.get("ALL_AGGREGATION"); }
		else
		{ return new LongSummaryStatistics(); }
	}
	
	@SafeVarargs
	public static <T> Map<String, LongSummaryStatistics> groupBy(final Function<? super T, String> classifier, 
																 final ToLongFunction<? super T> mapper, 
			   													 final T ... params)
	{
		if (!ArrayUtil.isArrayEmpty(params))
		{ return groupBy(classifier, mapper, Arrays.asList(params)); }
		else
		{ return Collections.emptyMap(); }
	}
	
	public static <T> Map<String, LongSummaryStatistics> groupBy(final Function<? super T, String> classifier, 
																 final ToLongFunction<? super T> mapper, 
																 final Collection<T> coll)
	{
		if (!JavaCollectionsUtil.isCollectionEmpty(coll))
		{ return coll.stream().collect(Collectors.groupingBy(classifier, Collectors.summarizingLong(mapper))); }
		else
		{ return Collections.emptyMap(); }
	}
	
	public static <T> long getLong(final T obj) throws Exception
	{
		if (obj instanceof Long)
		{ return Long.parseLong(obj.toString()); }
		else
		{ throw new Exception("无法转换为long类型"); }
	}
}