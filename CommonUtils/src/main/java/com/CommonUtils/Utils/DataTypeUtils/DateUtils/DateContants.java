package com.CommonUtils.Utils.DataTypeUtils.DateUtils;

@Deprecated
public final class DateContants 
{
	private DateContants() {}
	
	/**建议使用cn.hutool.core.date.DatePattern.PURE_DATE_PATTERN*/
	public static final String DATE_FORMAT_1 = "yyyyMMdd";
	
	public static final String DATE_FORMAT_2 = "yyyyMM";
	
	/**建议使用cn.hutool.core.date.DatePattern.NORM_DATETIME_PATTERN*/
	public static final String DATE_FORMAT_3 = "yyyy-MM-dd HH:mm:ss";
	
	/**建议使用cn.hutool.core.date.DatePattern.PURE_DATETIME_PATTERN*/
	public static final String DATE_FORMAT_4 = "yyyyMMddHHmmss";
	
	/**建议使用cn.hutool.core.date.DatePattern.NORM_DATE_PATTERN*/
	public static final String DATE_FORMAT_5 = "yyyy-MM-dd";
	
	/**建议使用cn.hutool.core.date.DatePattern.NORM_DATETIME_MS_PATTERN*/
	public static final String DATE_FORMAT_6 = "yyyy-MM-dd HH:mm:ss.SSS";
	
	/**建议使用cn.hutool.core.date.DatePattern.CHINESE_DATE_PATTERN*/
	public static final String DATE_FORMAT_7 = "yyyy年MM月dd日";
	
	public static final String DAY_NAMES[] = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五","星期六" };  
	
	public static final String YEAR_FORMAT = "yyyy";
	
	public static final String MONTH_FORMAT = "MM";
}