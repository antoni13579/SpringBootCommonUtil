package com.CommonUtils.Utils.DataTypeUtils.DoubleUtils;

import java.text.DecimalFormat;

import java.util.Collection;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrSpliter;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

public final class DoubleUtil 
{
	private DoubleUtil() {}
	
	/**建议使用cn.hutool.core.util.NumberUtil.roundStr代替*/ 
	@Deprecated
	public static String formatDoubleToStr(final double number, final String format)
	{
		if (StrUtil.isEmptyIfStr(format))
		{ return ""; }
		
		return new DecimalFormat(format).format(number);
	}
	
	public static DoubleSummaryStatistics aggregation(final Double ... params)
	{
		Map<String, DoubleSummaryStatistics> map = groupBy((x) -> { return "ALL_AGGREGATION"; }, (x) -> { return x; }, params);
		if (!CollUtil.isEmpty(map))
		{ return map.get("ALL_AGGREGATION"); }
		else
		{ return new DoubleSummaryStatistics(); }
	}
	
	public static DoubleSummaryStatistics aggregation(final Collection<Double> coll)
	{
		Map<String, DoubleSummaryStatistics> map = groupBy((x) -> { return "ALL_AGGREGATION"; }, (x) -> { return x; }, coll);
		if (!CollUtil.isEmpty(map))
		{ return map.get("ALL_AGGREGATION"); }
		else
		{ return new DoubleSummaryStatistics(); }
	}
	
	@SafeVarargs
	public static <T> Map<String, DoubleSummaryStatistics> groupBy(final Function<? super T, String> classifier, 
																   final ToDoubleFunction<? super T> mapper, 
			   													   final T ... params)
	{
		if (!ArrayUtil.isEmpty(params))
		{ return groupBy(classifier, mapper, CollUtil.newArrayList(params)); }
		else
		{ return Collections.emptyMap(); }
	}
	
	public static <T> Map<String, DoubleSummaryStatistics> groupBy(final Function<? super T, String> classifier, 
																   final ToDoubleFunction<? super T> mapper, 
																   final Collection<T> coll)
	{
		if (!CollUtil.isEmpty(coll))
		{ return coll.stream().collect(Collectors.groupingBy(classifier, Collectors.summarizingDouble(mapper))); }
		else
		{ return Collections.emptyMap(); }
	}
	
	public static void processGroupByResult(final Map<String, DoubleSummaryStatistics> map, final String delimiter, final ItemProcessorForProcessGroupByResult ... processors)
	{
		CollUtil.forEach//return JavaCollectionsUtil.mapProcessor
		(
				map, 
				(final String groupByKey, final DoubleSummaryStatistics groupByResult, final int indx) -> 
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
	
	/**建议使用cn.hutool.core.convert.Convert.toDouble*/ 
	@Deprecated
	public static <T> double getDouble(final T obj) throws Exception
	{
		if (obj instanceof Double)
		{ return Double.parseDouble(obj.toString()); }
		else
		{ throw new Exception("无法转换为double类型"); }
	}
	
	@FunctionalInterface
	public interface ItemProcessorForProcessGroupByResult
	{ void process(final DoubleSummaryStatistics groupByResult, final String[] groupByFields); }
}