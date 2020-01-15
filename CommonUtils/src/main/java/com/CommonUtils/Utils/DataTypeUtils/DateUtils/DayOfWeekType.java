package com.CommonUtils.Utils.DataTypeUtils.DateUtils;

import lombok.Getter;
import lombok.ToString;

/**
 * 已过时
 * @deprecated
 * */
@Deprecated(since="已过时")
@ToString
@Getter
public enum DayOfWeekType 
{
	MONDAY("星期一"),
	TUESDAY("星期二"),
	WEDNESDAY("星期三"),
	THURSDAY("星期四"),
	FRIDAY("星期五"),
	SATURDAY("星期六"),
	SUNDAY("星期日");
	
	private final String type;
	
	private DayOfWeekType(final String type)
	{ this.type = type; }
}