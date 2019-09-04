package com.CommonUtils.Utils.DataTypeUtils.DateUtils;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public enum RetailTimeIndicator 
{
	DTD("Daily"),
	WTD("今周初至今天"),
	MTD("今月初至今天"),
	QTD("今季度初至今天"),
	YTD("今年初至今天");
	
	private final String retailTimeIndicator;
	
	private RetailTimeIndicator(final String retailTimeIndicator)
	{ this.retailTimeIndicator = retailTimeIndicator; }
}