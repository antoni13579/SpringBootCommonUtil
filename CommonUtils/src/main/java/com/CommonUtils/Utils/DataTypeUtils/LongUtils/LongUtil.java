package com.CommonUtils.Utils.DataTypeUtils.LongUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrSpliter;
import cn.hutool.core.util.ArrayUtil;

public final class LongUtil 
{
	private LongUtil() {}
	
	private static final String AGGREGATION_KEY = "ALL_AGGREGATION";
	
	public static LongSummaryStatistics aggregation(final Long ... params)
	{
		Map<String, LongSummaryStatistics> map = groupBy(x -> AGGREGATION_KEY, x -> x, params);
		if (!CollUtil.isEmpty(map))
		{ return map.get(AGGREGATION_KEY); }
		else
		{ return new LongSummaryStatistics(); }
	}
	
	public static LongSummaryStatistics aggregation(final Collection<Long> coll)
	{
		Map<String, LongSummaryStatistics> map = groupBy(x -> AGGREGATION_KEY, x -> x, coll);
		if (!CollUtil.isEmpty(map))
		{ return map.get(AGGREGATION_KEY); }
		else
		{ return new LongSummaryStatistics(); }
	}
	
	@SafeVarargs
	public static <T> Map<String, LongSummaryStatistics> groupBy(final Function<? super T, String> classifier, 
																 final ToLongFunction<? super T> mapper, 
			   													 final T ... params)
	{
		if (!ArrayUtil.isEmpty(params))
		{ return groupBy(classifier, mapper, CollUtil.newArrayList(params)); }
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
	
	public static void processGroupByResult(final Map<String, LongSummaryStatistics> map, final String delimiter, final ItemProcessorForProcessGroupByResult ... processors)
	{
		CollUtil.forEach//return JavaCollectionsUtil.mapProcessor
		(
				map, 
				(final String groupByKey, final LongSummaryStatistics groupByResult, final int indx) -> 
				{
					List<String> tmp = StrSpliter.split(groupByKey, delimiter, false, false);
					String[] groupByFields = tmp.toArray(new String[tmp.size()]);					
					JavaCollectionsUtil.collectionProcessor
					(
							CollUtil.newArrayList(processors), 
							(final ItemProcessorForProcessGroupByResult val, final int inx, final int length) -> val.process(groupByResult, groupByFields)
					);
				}
		);
	}
	
	/**建议使用cn.hutool.core.convert.Convert.toLong
	 * @deprecated
	 * */ 
	@Deprecated(since="建议使用cn.hutool.core.convert.Convert.toLong")
	public static <T> long getLong(final T obj) throws LongUtilException
	{
		if (obj instanceof Long)
		{ return Long.parseLong(obj.toString()); }
		else
		{ throw new LongUtilException("无法转换为long类型"); }
	}
	
	private static class LongUtilException extends Exception
	{
		private static final long serialVersionUID = -4865248675526149971L;

		private LongUtilException(final String message)
		{ super(message); }
	}
	
	@FunctionalInterface
	public interface ItemProcessorForProcessGroupByResult
	{ void process(final LongSummaryStatistics groupByResult, final String[] groupByFields); }
}