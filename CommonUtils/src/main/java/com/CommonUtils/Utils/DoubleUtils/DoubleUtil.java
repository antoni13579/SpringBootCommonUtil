package com.CommonUtils.Utils.DoubleUtils;

import java.text.DecimalFormat;

import com.CommonUtils.Utils.StringUtils.StringUtil;

public final class DoubleUtil 
{
	private DoubleUtil() {}
	
	public static String formatDoubleToStr(final double number, final String format)
	{
		if (StringUtil.isStrEmpty(format))
		{ return ""; }
		
		DecimalFormat df = new DecimalFormat(format);
		return df.format(number);
	}
}