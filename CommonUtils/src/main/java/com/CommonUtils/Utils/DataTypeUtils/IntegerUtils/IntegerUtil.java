package com.CommonUtils.Utils.DataTypeUtils.IntegerUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrSpliter;
import cn.hutool.core.util.ArrayUtil;

public final class IntegerUtil 
{
	private IntegerUtil() {}
	
	public static IntSummaryStatistics aggregation(final Integer ... params)
	{
		Map<String, IntSummaryStatistics> map = groupBy((x) -> { return "ALL_AGGREGATION"; }, (x) -> { return x; }, params);
		if (!CollUtil.isEmpty(map))
		{ return map.get("ALL_AGGREGATION"); }
		else
		{ return new IntSummaryStatistics(); }
	}
	
	public static IntSummaryStatistics aggregation(final Collection<Integer> coll)
	{
		Map<String, IntSummaryStatistics> map = groupBy((x) -> { return "ALL_AGGREGATION"; }, (x) -> { return x; }, coll);
		if (!CollUtil.isEmpty(map))
		{ return map.get("ALL_AGGREGATION"); }
		else
		{ return new IntSummaryStatistics(); }
	}
	
	@SafeVarargs
	public static <T> Map<String, IntSummaryStatistics> groupBy(final Function<? super T, String> classifier, 
																final ToIntFunction<? super T> mapper, 
			   													final T ... params)
	{
		if (!ArrayUtil.isEmpty(params))
		{ return groupBy(classifier, mapper, CollUtil.newArrayList(params)); }
		else
		{ return Collections.emptyMap(); }
	}
	
	public static <T> Map<String, IntSummaryStatistics> groupBy(final Function<? super T, String> classifier, 
																final ToIntFunction<? super T> mapper, 
																final Collection<T> coll)
	{
		if (!CollUtil.isEmpty(coll))
		{ return coll.stream().collect(Collectors.groupingBy(classifier, Collectors.summarizingInt(mapper))); }
		else
		{ return Collections.emptyMap(); }
	}
	
	public static void processGroupByResult(final Map<String, IntSummaryStatistics> map, final String delimiter, final ItemProcessorForProcessGroupByResult ... processors)
	{
		CollUtil.forEach//return JavaCollectionsUtil.mapProcessor
		(
				map, 
				(final String groupByKey, final IntSummaryStatistics groupByResult, final int indx) -> 
				{
					List<String> tmp = StrSpliter.split(groupByKey, delimiter, false, false);
					//String[] groupByFields = StringUtils.splitPreserveAllTokens(groupByKey, delimiter);
					String[] groupByFields = tmp.toArray(new String[tmp.size()]);
					JavaCollectionsUtil.collectionProcessor
					(
							CollUtil.newArrayList(processors), 
							(final ItemProcessorForProcessGroupByResult val, final int inx, final int length) -> 
							{ val.process(groupByResult, groupByFields); }
					);
				}
		);
	}
	
	/**建议使用cn.hutool.core.convert.Convert.toInt*/ 
	@Deprecated
	public static <T> int getInteger(final T obj) throws Exception
	{
		if (obj instanceof Integer)
		{ return Integer.parseInt(obj.toString()); }
		else
		{ throw new Exception("无法转换为int类型"); }
	}
	
	@FunctionalInterface
	public interface ItemProcessorForProcessGroupByResult
	{ void process(final IntSummaryStatistics groupByResult, final String[] groupByFields); }
}