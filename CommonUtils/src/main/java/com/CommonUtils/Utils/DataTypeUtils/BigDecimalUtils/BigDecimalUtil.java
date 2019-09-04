package com.CommonUtils.Utils.DataTypeUtils.BigDecimalUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import com.CommonUtils.Utils.CommonUtils.CommonUtil;
import com.CommonUtils.Utils.DataTypeUtils.ArrayUtils.ArrayUtil;
import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class BigDecimalUtil 
{
	private BigDecimalUtil() {}
	
	public static <T> Optional<BigDecimal> sum(final BigDecimal ... params)
	{ return sum((x)-> { return x; }, params); }
	
	public static <T> Optional<BigDecimal> sum(final Collection<BigDecimal> coll)
	{ return sum((x)-> { return x; }, coll); }
	
	public static <T> Optional<BigDecimal> sum(final Function<? super T, BigDecimal> mapper, final Collection<T> coll)
	{
		if (!JavaCollectionsUtil.isCollectionEmpty(coll))
		{
			BigDecimal result = coll.stream().map(mapper).reduce(BigDecimal.ZERO, BigDecimal::add);
			return Optional.ofNullable(result);
		}
		else
		{ return Optional.ofNullable(null); }
	}
	
	@SafeVarargs
	public static <T> Optional<BigDecimal> sum(final Function<? super T, BigDecimal> mapper, final T ... params)
	{
		if (!ArrayUtil.isArrayEmpty(params))
		{ return sum(mapper, Arrays.asList(params)); }
		else
		{ return Optional.ofNullable(null); }
	}
	
	public static BigDecimal calculate(final BigDecimalCalculate bigDecimalCalculate, final BigDecimal ... params)
	{
		BigDecimal result = new BigDecimal(Double.toString(0));
		if (!ArrayUtil.isArrayEmpty(params))
		{
			for (int i = 0; i < params.length; i++)
			{
				BigDecimal param1 = null == params[i] ? new BigDecimal(Double.toString(0)) : params[i];
				
				BigDecimal param2;
				if (i != (params.length - 1))
				{ param2 = null == params[i + 1] ? new BigDecimal(Double.toString(0)) : params[i + 1]; }
				else
				{ param2 = null == params[i] ? new BigDecimal(Double.toString(0)) : params[i]; }
				
				switch (bigDecimalCalculate)
				{
					case ADD:
						if (i == 0)
						{ result = param1.add(param2); }
						else if (i > 1)
						{ result = result.add(param1); }
						
						break;
						
					case SUBTRACT:
						if (i == 0)
						{ result = param1.subtract(param2); }
						else if (i > 1)
						{ result = result.subtract(param1); }
						
						break;
						
					case MULTIPLY:
						if (i == 0)
						{ result = param1.multiply(param2); }
						else if (i > 1)
						{ result = result.multiply(param1); }
						
						break;
						
					case DIVIDE:
						if (i == 0)
						{ result = param1.divide(param2); }
						else if (i > 1)
						{ result = result.divide(param1); }
						
						break;
						
					default:
						break;
				}
			}
		}
		return result;
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
				{ result = CommonUtil.deepCopy((BigDecimal)t); }
				
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
	
	public static <T> BigDecimal getBigDecimal(final T obj) throws Exception
	{
		if (obj instanceof BigDecimal)
		{ return new BigDecimal(obj.toString()); }
		else
		{ throw new Exception("无法转换为BigDecimal类型"); }
	}
}