package com.CommonUtils.Utils.DataTypeUtils.DateUtils;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;
import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.CustomCollections.HashMap;
import com.CommonUtils.Utils.DataTypeUtils.StringUtils.StringUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DateUtil
{
	private DateUtil() {}
	
	/**建议使用cn.hutool.core.convert.Convert.convert(java.sql.Date.class, obj)*/ 
	@Deprecated
	public static <T> java.sql.Date getSqlDate(final T obj) throws Exception
	{
		if (obj instanceof java.sql.Date)
		{ return (java.sql.Date)obj; }
		else
		{ throw new Exception("无法转换为java.sql.Date类型"); }
	}
	
	/**建议使用cn.hutool.core.convert.Convert.toDate*/ 
	@Deprecated
	public static <T> java.util.Date getDate(final T obj) throws Exception
	{
		if (obj instanceof java.util.Date)
		{ return (java.util.Date)obj; }
		else
		{ throw new Exception("无法转换为java.util.Date类型"); }
	}
	
	public static Date getDate(final int year, final int month, final int day)
	{
		String monthStr = month < 10 ? "0" + month : String.valueOf(month);
		String dayStr = day < 10 ? "0" + day : String.valueOf(day);
		return cn.hutool.core.date.DateUtil.parse(String.valueOf(year) + monthStr + dayStr, DatePattern.PURE_DATE_PATTERN);
	}
	
	/**java.util.Date转换为java.sql.Date，建议使用cn.hutool.core.date.DateTime.toSqlDate*/
	@Deprecated
	public static java.sql.Date getDate(final Date date)
	{ return new java.sql.Date(date.getTime()); }
	
	/**java.sql.Date转换为java.util.Date，建议使用cn.hutool.core.date.DateTime.toJdkDate*/
	@Deprecated
	public static Date getDate(final java.sql.Date date)
	{ return new Date(date.getTime()); }
	
	/**java.sql.Timestamp转换为java.util.Date，建议使用cn.hutool.core.date.DateTime.toJdkDate*/
	@Deprecated
	public static Date getDate(final Timestamp timestamp)
	{ return new Date(timestamp.getTime()); }
	
	/**建议使用cn.hutool.core.date.DateTime.toJdkDate*/
	@Deprecated
	public static Date getDate(final oracle.sql.TIMESTAMP timestamp) throws SQLException
	{ return getDate(timestamp.timestampValue()); }
	
	/**建议使用cn.hutool.core.date.DateTime.toTimestamp*/
	@Deprecated
	public static Timestamp getTimestamp(final oracle.sql.TIMESTAMP timestamp) throws SQLException
	{ return timestamp.timestampValue(); }
	
	/**建议使用cn.hutool.core.convert.Convert.convert(java.sql.Timestamp.class, obj)*/ 
	@Deprecated
	public static <T> java.sql.Timestamp getTimestamp(final T obj) throws Exception
	{
		if (obj instanceof java.sql.Timestamp)
		{ return (java.sql.Timestamp)obj; }
		else
		{ throw new Exception("无法转换为java.sql.Timestamp类型"); }
	}
	
	/**建议使用cn.hutool.core.convert.Convert.convert(oracle.sql.TIMESTAMP.class, obj)*/ 
	@Deprecated
	public static <T> oracle.sql.TIMESTAMP getOracleTimestamp(final T obj) throws Exception
	{
		if (obj instanceof oracle.sql.TIMESTAMP)
		{ return (oracle.sql.TIMESTAMP)obj; }
		else
		{ throw new Exception("无法转换为oracle.sql.TIMESTAMP类型"); }
	}
	
	/**
	 * 指定的日期累加或累减，并返回计算后的日期, type对应于Calendar的常量值，如Calendar.DAY_OF_MONTH
	 * 
	 * 
	 * 如果需要计算业务日，cnt输入为52 * 7 * years， type输入为Calendar.DATE
	 * 
	 * 所谓的业务日，如2018-11-15日，是星期四，那么下年对应的星期四为2019-11-14，而不是2019-11-15
	 * 2019-11-14为业务日
	 * 2019-11-15为自然日
	 * 
	 * years参数是计算多少年后，或多少年前的业务日，
	 * 
	 * 建议使用cn.hutool.core.date.DateUtil.offset相关方法
	 * */
	@Deprecated
	public static Date getDate(final Date stlDate, final int cnt, final int type)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(stlDate);
		cal.add(type, cnt);
		Date date = cal.getTime();
		return date;
	}
	
	/**
	 * 获取日期指定信息，如年，月等
	 * @param type 如Calendar.DATE、Calendar.YEAR等，建议使用cn.hutool.core.date.DateTime的getField方法
	 * */
	@Deprecated
	public static int getField(final Date date, final int type)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(type);
	}
	
	/**
	 * 建议使用cn.hutool.core.date.DateUtil.range相关方法
	 * */
	/*
	@Deprecated
	public static Collection<Date> getRangeCondition(final Date start, final Date end, final String timeType, final String ... params)
	{
		Collection<Date> dates = DateUtil.getRange(start, end);
		
		//每天生成
		if ("DAILY".equalsIgnoreCase(timeType))
		{ return dates; }
		else
		{
			Collection<Date> result = new TreeSet<Date>();
			
			for (Date date : dates)
			{
				//每月的最后一天
				if ("MONTHLY_LAST_DAY".equalsIgnoreCase(timeType))
				{
					if (date.compareTo(DateUtil.getLastDate(date, Calendar.DAY_OF_MONTH)) == 0)
					{ result.add(date); }
				}
				
				//根据星期几生成
				else if ("DAY_OF_WEEK".equalsIgnoreCase(timeType) && !ArrayUtil.isArrayEmpty(params))
				{
					for (String param : params)
					{
						DayOfWeekType enumResult = getDateOfWeek(date);
						if (null != enumResult && enumResult.getDayOfWeekType().equalsIgnoreCase(param))
						{ result.add(date); }
					}
				}
				
				else
				{ log.warn("getRangeCondition出现了新的时间筛选类型，请及时排查"); }
			}
			
			return result;
		}
	}
	*/
	
	/**
	 * 根据开始日期与结束日期，获取其时间范围，建议使用cn.hutool.core.date.DateUtil.range相关方法
	 * */
	@Deprecated
	public static Collection<Date> getRange(final Date start, final Date end)
	{
		//开始日期为空，结束日期为空，开始时间大于结束时间都要返回空集合
		if (null == start || null == end || start.compareTo(end) == 1)
		{ return Collections.emptySet(); }
		
		Date tmpDate = start;
		Set<Date> result = new TreeSet<Date>();
		
		//开始时间 == 结束时间，开始时间 < 结束时间都可以纳入统计
		while (tmpDate.compareTo(end) == 0 || tmpDate.compareTo(end) == -1)
		{
			result.add(tmpDate);
			tmpDate = getDate(tmpDate, 1, Calendar.DATE);
		}
		
		return result;
	}
	
	/**
	 * 建议使用cn.hutool.core.date.DateUtil.range相关方法
	 * */
	@Deprecated
	public static Collection<String> getRangeForMonth(final Date start, final Date end)
	{
		Collection<Date> dateRange = getRange(start, end);
		if (!JavaCollectionsUtil.isCollectionEmpty(dateRange))
		{
			Set<String> result = new TreeSet<String>();
			for (Date date : dateRange)
			{
				String dateStr = formatDateToStr(date, DateContants.DATE_FORMAT_2);
				result.add(dateStr);
			}
			return result;
		}
		
		return Collections.emptySet();
	}
	
	/**
	 * 日期类型格式化输出为字符串，建议使用cn.hutool.core.date.DateUtil.format相关方法
	 * */
	@Deprecated
	public static String formatDateToStr(final Date date, final String format)
	{
		if (null == date || StringUtil.isStrEmpty(format))
		{ return ""; }
		
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String result = sdf.format(date);
		return result;
	}
	
	/**
	 * 字符串类型格式化输出为对应的日期，建议使用cn.hutool.core.date.DateUtil.parse相关方法
	 * */
	@Deprecated
	public static Optional<Date> formatStrToDate(final String str, final String format)
	{
		if (StringUtil.isStrEmpty(str) || StringUtil.isStrEmpty(format))
		{ return Optional.ofNullable(null); }
		
		Date date = null;
		try
		{ date = new SimpleDateFormat(format).parse(str); }
		catch (Exception ex)
		{ log.error("字符串转换为日期出现异常，需转换的字符串为{}，日期格式为{}，异常原因为：", str, format, ex); }
		
		return Optional.ofNullable(date);
	}
	
	/**模仿Oracle实现的MONTHS_BETWEEN函数，建议使用cn.hutool.core.date.DateUtil.betweenMonth相关方法*/
	@Deprecated
	public static double monthsBetween(final Date startDate, final Date endDate)
	{
		if (null == startDate || null == endDate || startDate.compareTo(endDate) == 1)
		{ return -1; }
		
		Calendar startCalendar = Calendar.getInstance();
		Calendar endCalendar = Calendar.getInstance();
		
		startCalendar.setTime(startDate);
		endCalendar.setTime(endDate);
		
		int startYear = startCalendar.get(Calendar.YEAR);
		int endYear = endCalendar.get(Calendar.YEAR);
		
		double month = 0;
		
		//在同一年只用处理月份和天数				
		//说明两个日期都是在当月的最后一天只用处理月份				
		if(startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) == startCalendar.get(Calendar.MONTH) && 
		   endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) == endCalendar.get(Calendar.MONTH))
		{ month = startCalendar.get(Calendar.MONTH) - endCalendar.get(Calendar.MONTH); }	
		
		//说明两个日期都是当月第一天只用处理月份				
		else if (startCalendar.getActualMinimum(Calendar.DAY_OF_MONTH) == startCalendar.get(Calendar.MONTH)	&& 
				 startCalendar.getActualMinimum(Calendar.DAY_OF_MONTH) == startCalendar.get(Calendar.MONTH))
		{ month = startCalendar.get(Calendar.MONTH) - endCalendar.get(Calendar.MONTH); }
		else
		{
			double day = startCalendar.get(Calendar.DAY_OF_MONTH) - endCalendar.get(Calendar.DAY_OF_MONTH);					
			month = startCalendar.get(Calendar.MONTH) - endCalendar.get(Calendar.MONTH) + day/31;				
		}
		
		if (startYear != endYear)
		{ month += ((startYear-endYear) * 12); }
		
		return Math.abs(month);
	}
	
	/**
	 * 获取最后一天
	 * type输入为Calendar.DAY_OF_MONTH，获取当月的最后一天
	 * type输入为Calendar.DAY_OF_YEAR，获取当年的最后一天，
	 * 
	 *  对于年，建议使用cn.hutool.core.date.DateUtil.endOfYear
	 * 对于月，建议使用cn.hutool.core.date.DateUtil.endOfMonth
	 * */
	@Deprecated
	public static Date getLastDate(final Date stlDate, final int type)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(stlDate);
		cal.set(type, cal.getActualMaximum(type));
		return cal.getTime();
	}
	
	/**
	 * 获取第一天，
	 * type输入为Calendar.DAY_OF_MONTH，获取当月的第一天
	 * type输入为Calendar.DAY_OF_YEAR，获取当年的第一天，
	 * 
	 * 对于年，建议使用cn.hutool.core.date.DateUtil.beginOfYear
	 * 对于月，建议使用cn.hutool.core.date.DateUtil.beginOfMonth
	 * */
	@Deprecated
	public static Date getFirstDate(final Date stlDate, final int type)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(stlDate);
		cal.set(type, 1);
		return cal.getTime();
	}
	
	/**建议使用cn.hutool.core.date.DateUtil.dayOfWeek相关方法*/
	@Deprecated
	public static DayOfWeekType getDateOfWeek(final Date stlDate)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(stlDate);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if(dayOfWeek < 0) { dayOfWeek = 0; }
		
		for (DayOfWeekType value : DayOfWeekType.values())
		{
			if (value.getDayOfWeekType().equalsIgnoreCase(DateContants.DAY_NAMES[dayOfWeek]))
			{ return value; }
		}
		
		return null;
	}
	
	/**建议使用cn.hutool.core.date.DateUtil.year*/ 
	@Deprecated
	public static int getYear(final Date date)
	{ return getField(date, Calendar.YEAR); }
	
	/**建议使用cn.hutool.core.date.DateUtil.month*/ 
	@Deprecated
	public static int getMonth(final Date date)
	{ return getField(date, Calendar.MONTH) + 1; }
	
	/**建议使用cn.hutool.core.date.DateUtil.dayOfMonth*/ 
	@Deprecated
	public static int getDay(final Date date)
	{ return getField(date, Calendar.DAY_OF_MONTH); }
	
	public static int getTotalDayForMonth(final Date date)
	{ return getTotalDayForMonth(getYear(date), getMonth(date)); }
	
	public static int getTotalDayForMonth(final int year, final int month)
	{
		Calendar c = Calendar.getInstance();
		c.set(year, month, 0);
		return c.get(Calendar.DAY_OF_MONTH);
	}
	
	public static int getTotalDayForYear(final int year)
	{ return year % 4 == 0 ? 366 : 365; }
	
	public static int getTotalDayForYear(final Date date)
	{ return getTotalDayForYear(getYear(date)); }
	
	public static Map<String, Date> getMaxAndMin(final Collection<Date> dates)
	{
		if (!CollUtil.isEmpty(dates))
		{ return getMaxAndMin(dates.toArray(new Date[dates.size()])); }
		else
		{ return Collections.emptyMap(); }
	}
	
	public static Map<String, Date> getMaxAndMin(final Date ... dates)
	{
		if (!cn.hutool.core.util.ArrayUtil.isEmpty(dates))
		{
			Date min = dates[0];
			Date max = min;			
			for (int i = 0; i < dates.length; i++)
			{
				if (min.compareTo(dates[i]) == 1) 
				{ min = dates[i]; }
				
	            if (max.compareTo(dates[i]) == -1) 
	            { max = dates[i]; }
			}
			
			return new HashMap<String, Date>().put("MAX", max).put("MIN", min).getMap();
		}
		else
		{ return Collections.emptyMap(); }
	}
}