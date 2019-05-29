package com.CommonUtils.Utils.DateUtils;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public enum DayOfWeekType 
{
	MonDay("星期一"),
	TuesDay("星期二"),
	WednesDay("星期三"),
	ThursDay("星期四"),
	FriDay("星期五"),
	SaturDay("星期六"),
	SunDay("星期日");
	
	private final String dayOfWeekType;
	
	private DayOfWeekType(final String dayOfWeekType)
	{ this.dayOfWeekType = dayOfWeekType; }
}