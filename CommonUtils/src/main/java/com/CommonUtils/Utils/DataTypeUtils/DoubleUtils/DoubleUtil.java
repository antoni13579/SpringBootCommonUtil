package com.CommonUtils.Utils.DataTypeUtils.DoubleUtils;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

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
	
	public static DoubleSummaryStatistics aggregation(final Double ... params)
	{
		Map<String, DoubleSummaryStatistics> map = groupBy((x) -> { return "ALL_AGGREGATION"; }, (x) -> { return x; }, params);
		if (!JavaCollectionsUtil.isMapEmpty(map))
		{ return map.get("ALL_AGGREGATION"); }
		else
		{ return new DoubleSummaryStatistics(); }
	}
	
	public static DoubleSummaryStatistics aggregation(final Collection<Double> coll)
	{
		Map<String, DoubleSummaryStatistics> map = groupBy((x) -> { return "ALL_AGGREGATION"; }, (x) -> { return x; }, coll);
		if (!JavaCollectionsUtil.isMapEmpty(map))
		{ return map.get("ALL_AGGREGATION"); }
		else
		{ return new DoubleSummaryStatistics(); }
	}
	
	@SafeVarargs
	public static <T> Map<String, DoubleSummaryStatistics> groupBy(final Function<? super T, String> classifier, 
																   final ToDoubleFunction<? super T> mapper, 
			   													   final T ... params)
	{
		if (!ArrayUtil.isArrayEmpty(params))
		{ return groupBy(classifier, mapper, Arrays.asList(params)); }
		else
		{ return Collections.emptyMap(); }
	}
	
	public static <T> Map<String, DoubleSummaryStatistics> groupBy(final Function<? super T, String> classifier, 
																   final ToDoubleFunction<? super T> mapper, 
																   final Collection<T> coll)
	{
		if (!JavaCollectionsUtil.isCollectionEmpty(coll))
		{ return coll.stream().collect(Collectors.groupingBy(classifier, Collectors.summarizingDouble(mapper))); }
		else
		{ return Collections.emptyMap(); }
	}
	
	public static <T> double getDouble(final T obj) throws Exception
	{
		if (obj instanceof Double)
		{ return Double.parseDouble(obj.toString()); }
		else
		{ throw new Exception("无法转换为double类型"); }
	}
}