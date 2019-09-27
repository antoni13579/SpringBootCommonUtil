package com.CommonUtils.Utils.DataTypeUtils.BigDecimalUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.collections.impl.collector.BigDecimalSummaryStatistics;
import org.eclipse.collections.impl.collector.Collectors2;

import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrSpliter;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class BigDecimalUtil 
{
	private BigDecimalUtil() {}
	
	public static BigDecimalSummaryStatistics aggregation(final BigDecimal ... params)
	{
		Map<String, BigDecimalSummaryStatistics> map = groupBy((x) -> { return "ALL_AGGREGATION"; }, (x) -> { return x; }, params);
		if (!CollUtil.isEmpty(map))
		{ return map.get("ALL_AGGREGATION"); }
		else
		{ return new BigDecimalSummaryStatistics(); }
	}
	
	public static BigDecimalSummaryStatistics aggregation(final Collection<BigDecimal> coll)
	{
		Map<String, BigDecimalSummaryStatistics> map = groupBy((x) -> { return "ALL_AGGREGATION"; }, (x) -> { return x; }, coll);
		if (!CollUtil.isEmpty(map))
		{ return map.get("ALL_AGGREGATION"); }
		else
		{ return new BigDecimalSummaryStatistics(); }
	}
	
	@SafeVarargs
	public static <T> Map<String, BigDecimalSummaryStatistics> groupBy(final Function<? super T, String> classifier, 
			   														   final org.eclipse.collections.api.block.function.Function<? super T, BigDecimal> mapper, 
			   														   final T ... params)
	{
		if (!ArrayUtil.isEmpty(params))
		{ return groupBy(classifier, mapper, Arrays.asList(params)); }
		else
		{ return Collections.emptyMap(); }
	}
	
	public static <T> Map<String, BigDecimalSummaryStatistics> groupBy(final Function<? super T, String> classifier, 
																	   final org.eclipse.collections.api.block.function.Function<? super T, BigDecimal> mapper, 
																	   final Collection<T> coll)
	{
		if (!CollUtil.isEmpty(coll))
		{ return coll.stream().collect(java.util.stream.Collectors.groupingBy(classifier, Collectors2.summarizingBigDecimal(mapper))); }
		else
		{ return Collections.emptyMap(); }
	}
	
	public static boolean processGroupByResult(final Map<String, BigDecimalSummaryStatistics> map, final String delimiter, final ItemProcessorForProcessGroupByResult ... processors)
	{
		return JavaCollectionsUtil.mapProcessor
		(
				map, 
				(final String groupByKey, final BigDecimalSummaryStatistics groupByResult, final int indx) -> 
				{
					List<String> tmp = StrSpliter.split(groupByKey, delimiter, false, false);
					//String[] groupByFields = StringUtils.splitPreserveAllTokens(groupByKey, groupByKey);
					String[] groupByFields = tmp.toArray(new String[tmp.size()]);
					com.CommonUtils.Utils.DataTypeUtils.ArrayUtils.ArrayUtil.arrayProcessor
					(
							processors, 
							(final ItemProcessorForProcessGroupByResult val, final int inx, final int length) -> 
							{ val.process(groupByResult, groupByFields); }
					);
				}
		);
	}
	
	public static <T> BigDecimal ifNull(final T t)
	{ return ifNull(t, 0); }
	
	public static <T> BigDecimal ifNull(final T t, final int defaultValue)
	{ return nvl(t, defaultValue); }
	
	public static <T> BigDecimal nvl(final T t)
	{ return nvl(t, 0); }
	
	public static <T> BigDecimal nvl(final T t, final int defaultValue)
	{
		BigDecimal result = null;
		try
		{
			if (null != t)
			{
				if (t instanceof BigDecimal)
				{ result = ObjectUtil.cloneByStream((BigDecimal)t); }
				
				else if (t instanceof char[])
				{ result = new BigDecimal((char[])t); }
				
				else if (t instanceof String)
				{ result = new BigDecimal((String)t); }
				
				else if (t instanceof Double)
				{ result = new BigDecimal((double)t); }
				
				else if (t instanceof BigInteger)
				{ result = new BigDecimal((BigInteger)t); }
				
				else if (t instanceof Integer)
				{ result = new BigDecimal((int)t); }
				
				else if (t instanceof Long)
				{ result = new BigDecimal((long)t); }
				
				else
				{ throw new Exception("执行BigDecimal的NVL函数出现问题，主要是无法生成BigDecimal"); }
			}
			else
			{ result = new BigDecimal(defaultValue); }
		}
		catch (Exception ex)
		{ log.error("执行BigDecimal的NVL函数出现异常，异常原因为：", ex); }
		return result;
	}
	
	/**建议使用cn.hutool.core.convert.Convert.toBigDecimal*/ 
	@Deprecated
	public static <T> BigDecimal getBigDecimal(final T obj) throws Exception
	{
		if (obj instanceof BigDecimal)
		{ return new BigDecimal(obj.toString()); }
		else
		{ throw new Exception("无法转换为BigDecimal类型"); }
	}
	
	@FunctionalInterface
	public interface ItemProcessorForProcessGroupByResult
	{ void process(final BigDecimalSummaryStatistics groupByResult, final String[] groupByFields); }
}