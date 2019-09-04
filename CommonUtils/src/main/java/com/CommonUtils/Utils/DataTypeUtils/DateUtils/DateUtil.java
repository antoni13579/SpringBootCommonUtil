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
import java.util.TreeMap;
import java.util.TreeSet;

import com.CommonUtils.Utils.DataTypeUtils.ArrayUtils.ArrayUtil;
import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;
import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.CustomCollections.ArrayList;
import com.CommonUtils.Utils.DataTypeUtils.DateUtils.Bean.RetailTimeIndicatorEntry;
import com.CommonUtils.Utils.DataTypeUtils.StringUtils.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DateUtil
{
	private DateUtil() {}
	
	public static <T> java.sql.Date getSqlDate(final T obj) throws Exception
	{
		if (obj instanceof java.sql.Date)
		{ return (java.sql.Date)obj; }
		else
		{ throw new Exception("无法转换为java.sql.Date类型"); }
	}
	
	public static <T> java.util.Date getDate(final T obj) throws Exception
	{
		if (obj instanceof java.util.Date)
		{ return (java.util.Date)obj; }
		else
		{ throw new Exception("无法转换为java.util.Date类型"); }
	}
	
	/**java.util.Date转换为java.sql.Date*/
	public static java.sql.Date getDate(final Date date)
	{ return new java.sql.Date(date.getTime()); }
	
	/**java.sql.Date转换为java.util.Date*/
	public static Date getDate(final java.sql.Date date)
	{ return new Date(date.getTime()); }
	
	/**java.sql.Timestamp转换为java.util.Date*/
	public static Date getDate(final Timestamp timestamp)
	{ return new Date(timestamp.getTime()); }
	
	public static Date getDate(final oracle.sql.TIMESTAMP timestamp) throws SQLException
	{ return getDate(timestamp.timestampValue()); }
	
	public static Timestamp getTimestamp(final oracle.sql.TIMESTAMP timestamp) throws SQLException
	{ return timestamp.timestampValue(); }
	
	public static <T> java.sql.Timestamp getTimestamp(final T obj) throws Exception
	{
		if (obj instanceof java.sql.Timestamp)
		{ return (java.sql.Timestamp)obj; }
		else
		{ throw new Exception("无法转换为java.sql.Timestamp类型"); }
	}
	
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
	 * years参数是计算多少年后，或多少年前的业务日
	 * */
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
	 * @param type 如Calendar.DATE、Calendar.YEAR等
	 * */
	public static int getField(final Date date, final int type)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(type);
	}
	
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
	
	/**
	 * 根据开始日期与结束日期，获取其时间范围
	 * */
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
	 * 日期类型格式化输出为字符串
	 * */
	public static String formatDateToStr(final Date date, final String format)
	{
		if (null == date || StringUtil.isStrEmpty(format))
		{ return ""; }
		
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String result = sdf.format(date);
		return result;
	}
	
	/**
	 * 字符串类型格式化输出为对应的日期
	 * */
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
	
	/**模仿Oracle实现的MONTHS_BETWEEN函数*/
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
	 * type输入为Calendar.DAY_OF_YEAR，获取当年的最后一天
	 * */
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
	 * type输入为Calendar.DAY_OF_YEAR，获取当年的第一天
	 * 
	 * */
	public static Date getFirstDate(final Date stlDate, final int type)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(stlDate);
		cal.set(type, 1);
		return cal.getTime();
	}
	
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
	
	public static RetailTimeIndicatorEntry getRetailTimeIndicatorEntry(final RetailTimeIndicator retailTimeIndicator, final Date stlDate)
	{
		switch (retailTimeIndicator)
		{
			case DTD:
				Date dtdTyBussinessDate = stlDate;
				Date dtdLyBussinessDate = getDate(dtdTyBussinessDate, 52 * 7 * -1, Calendar.DATE);
				
				Date dtdTyStartDate = dtdTyBussinessDate;
				Date dtdTyEndDate = dtdTyBussinessDate;
				
				Date dtdLyStartDate = dtdLyBussinessDate;
				Date dtdLyEndDate = dtdLyBussinessDate;
				return new RetailTimeIndicatorEntry().setRetailTimeIndicator(RetailTimeIndicator.DTD)
						  							 .setTyBussinessDate(dtdTyBussinessDate)
						  							 .setLyBussinessDate(dtdLyBussinessDate)
						  							 .setTyStartDate(dtdTyStartDate)
						  							 .setTyEndDate(dtdTyEndDate)
						  							 .setLyStartDate(dtdLyStartDate)
						  							 .setLyEndDate(dtdLyEndDate);
				
			case WTD:
				Date wtdTyBussinessDate = stlDate;
				Date wtdLyBussinessDate = getDate(wtdTyBussinessDate, 52 * 7 * -1, Calendar.DATE);
				
				Date wtdTyStartDate = getDate(getFirstDate(wtdTyBussinessDate, Calendar.DAY_OF_WEEK), -1, Calendar.DATE);
				Date wtdTyEndDate = wtdTyBussinessDate;
				
				Date wtdLyStartDate = getDate(getFirstDate(wtdLyBussinessDate, Calendar.DAY_OF_WEEK), -1, Calendar.DATE);
				Date wtdLyEndDate = wtdLyBussinessDate;
				return new RetailTimeIndicatorEntry().setRetailTimeIndicator(RetailTimeIndicator.WTD)
						  							 .setTyBussinessDate(wtdTyBussinessDate)
						  							 .setLyBussinessDate(wtdLyBussinessDate)
						  							 .setTyStartDate(wtdTyStartDate)
						  							 .setTyEndDate(wtdTyEndDate)
						  							 .setLyStartDate(wtdLyStartDate)
						  							 .setLyEndDate(wtdLyEndDate);
				
			//case MTD:
				//break;
				
			//case QTD:
				//break;
				
			case YTD:
				Date ytdTyBussinessDate = stlDate;
				Date ytdLyBussinessDate = getDate(ytdTyBussinessDate, -1, Calendar.YEAR);
				
				Date ytdTyStartDate = getFirstDate(ytdTyBussinessDate, Calendar.DAY_OF_YEAR);
				Date ytdTyEndDate = ytdTyBussinessDate;
				
				Date ytdLyStartDate = getFirstDate(ytdLyBussinessDate, Calendar.DAY_OF_YEAR);
				Date ytdLyEndDate = ytdLyBussinessDate;
				return new RetailTimeIndicatorEntry().setRetailTimeIndicator(RetailTimeIndicator.YTD)
													 .setTyBussinessDate(ytdTyBussinessDate)
													 .setLyBussinessDate(ytdLyBussinessDate)
													 .setTyStartDate(ytdTyStartDate)
													 .setTyEndDate(ytdTyEndDate)
													 .setLyStartDate(ytdLyStartDate)
													 .setLyEndDate(ytdLyEndDate);
				
			default:
				return new RetailTimeIndicatorEntry();
		}
	}
	
	/**
	 * 此方法用于零售行业生成时间指标数据，目前是LOTUS用上
	 * */
	public static Map<Date, Collection<RetailTimeIndicatorEntry>> getRetailTimeIndicatorEntrys(final Date startDate, final Date endDate)
	{
		Map<Date, Collection<RetailTimeIndicatorEntry>> result = new TreeMap<>();
		JavaCollectionsUtil.collectionProcessor
		(
				getRange(startDate, endDate), 
				(final Date dateRecord, final int indx, final int length) -> 
				{
					result.put
					(
							dateRecord, 
							new ArrayList<RetailTimeIndicatorEntry>().add(getRetailTimeIndicatorEntry(RetailTimeIndicator.DTD, dateRecord))
																	 .add(getRetailTimeIndicatorEntry(RetailTimeIndicator.WTD, dateRecord))
																	 .add(getRetailTimeIndicatorEntry(RetailTimeIndicator.YTD, dateRecord))
																	 .getList()
					);
				}
		);
		return result;
	}
	
	public static int getYear(final Date date)
	{ return getField(date, Calendar.YEAR); }
	
	public static int getMonth(final Date date)
	{ return getField(date, Calendar.MONTH) + 1; }
	
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
	{
		if (year % 4 == 0)
		{ return 366; }
		
		return 365;
	}
	
	public static int getTotalDayForYear(final Date date)
	{ return getTotalDayForYear(getYear(date)); }
}