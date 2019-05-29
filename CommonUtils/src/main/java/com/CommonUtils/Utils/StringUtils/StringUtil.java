package com.CommonUtils.Utils.StringUtils;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import com.CommonUtils.Utils.CommonUtils.CommonUtil;
import com.CommonUtils.Utils.DateUtils.DateContants;
import com.CommonUtils.Utils.DateUtils.DateFormat;
import com.CommonUtils.Utils.DateUtils.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class StringUtil 
{
	private StringUtil() {}
	
	/**
	 * 检测字符串是否为空，true为空，false为非空
	 * */
	public static boolean isStrEmpty(final String str)
	{
		if(null == str || "".equals(str.trim()) || str.length() == 0)
		{ return true; }
		
		return false;
	}
	
	/**
	 * 检测两个字符串是否相等，相等则返回true，不相等则返回false
	 * */
	public static boolean strEquals(final String str1, final String str2, final boolean ignoreCase)
	{
		//两个都是空串，则为相等
		if (isStrEmpty(str1) && isStrEmpty(str2))
		{ return true; }
		
		//【一个不是空串，另一个是，不是相等】 或 【 一个是空串，另一个不是，不是相等】
		else if ((!isStrEmpty(str1) && isStrEmpty(str2)) || 
				 (isStrEmpty(str1) && !isStrEmpty(str2)))
		{ return false; }
		
		//两个都不是空串
		else
		{
			if (ignoreCase)
			{ return str1.equalsIgnoreCase(str2); }
			else
			{ return str1.equals(str2); }
		}
	}
	
	public static String toUtf8String(final String str) throws UnsupportedEncodingException
	{
		StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) 
        {
        	char c = str.charAt(i);
            if (c >= 0 && c <= 255) 
            { sb.append(c); } 
            else 
            {
                byte[] b = Character.toString(c).getBytes("utf-8");                
                for (int j = 0; j < b.length; j++) 
                {
                    int k = b[j];
                    if (k < 0) { k += 256; }
                    sb.append("%" + Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return sb.toString();
	}
	
	public static <T> String toString(final T value) throws Exception
	{
		if (null == value)
		{ return ""; }
		else if (value instanceof java.util.Date || 
				 value instanceof java.sql.Date ||
				 value instanceof java.sql.Timestamp ||
				 value instanceof oracle.sql.TIMESTAMP)
		{ throw new Exception("toString(final T value)不能处理时间类型，请使用toString(final oracle.sql.TIMESTAMP date, final DateFomat dateFomat)或toString(final java.util.Date date, final DateFomat dateFomat)"); }
		else if (value instanceof Boolean)
		{
			boolean val = CommonUtil.getBoolean(value);
			String result = Boolean.toString(val);
			return result;
		}
		else if (value instanceof Integer)
		{
			int val = CommonUtil.getInteger(value);
			String result = Integer.toString(val);
			return result;
		}
		else if (value instanceof Short)
		{
			short val = CommonUtil.getShort(value);
			String result = Short.toString(val);
			return result;
		}
		else if (value instanceof Float)
		{
			float val = CommonUtil.getFloat(value);
			String result = Float.toString(val);
			return result;
		}
		else if (value instanceof Double)
		{
			double val = CommonUtil.getDouble(value);
			String result = Double.toString(val);
			return result;
		}
		else if (value instanceof Character)
		{
			char val = CommonUtil.getChar(value);
			String result = Character.toString(val);
			return result;
		}
		else if (value instanceof Long)
		{
			long val = CommonUtil.getLong(value);
			String result = Long.toString(val);
			return result;
		}
		else if (value instanceof Byte)
		{
			byte val = CommonUtil.getByte(value);
			String result = Byte.toString(val);
			return result;
		}
		else if (value instanceof String)
		{
			String val = CommonUtil.getString(value);
			return val;
		}
		else if (value instanceof BigDecimal)
		{
			BigDecimal val = CommonUtil.getBigDecimal(value);
			String result = val.toString();
			return result;
		}
		else
		{
			log.warn("需进行ToString的数据类型为：{}", value.getClass());
			throw new Exception("出现了新的数据类型，请及时处理");
		}
	}
	
	public static String toString(final oracle.sql.TIMESTAMP date, final DateFormat dateFomat) throws SQLException
	{ return toString(date.timestampValue(), dateFomat); }
	
	public static String toString(final java.util.Date date, final DateFormat dateFomat)
	{
		if (null == date)
		{ return ""; }
		
		switch (dateFomat)
		{
			case DATE:
				return DateUtil.formatDateToStr(date, DateContants.DATE_FORMAT_3);
			case TIMESTAMP:
				return DateUtil.formatDateToStr(date, DateContants.DATE_FORMAT_6);
			default:
				return "";
		}
	}
	
	public static String lpad(final String str, final int length, final char replaceStr)
	{
		int actuallyLength = length - str.length();
		StringBuilder sb = new StringBuilder();
		
		while (sb.length() < actuallyLength)
		{ sb.append(replaceStr); }
		
		sb.append(str);
		
		return sb.toString();
	}
	
	public static String rpad(final String str, final int length, final char replaceStr)
	{
		StringBuilder sb = new StringBuilder(str);
		
		while (sb.length() < length)
		{ sb.append(replaceStr); }
		
		return sb.toString();
	}
	
	public static String searchStr(final String regex, final String string)
	{
		StringBuilder sb = new StringBuilder();
		Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(string);
        while (m.find()) 
        {
            String g = m.group();
            sb.append(g);
        }
        return sb.toString();
	}
	
	/**
	 * 验证输入的字符串，是否符合正则表达式
	 * @param string 需要判断的字符串；
	 * 		  regex 正则表达式
	 * @return 返回布尔值，true为验证通过，false为验证不通过
	 * */
	public static boolean validateRegular(final String string, final String regex)
	{
		Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(string);
        return m.find();
	}
	
	/**
	 * 分词工具
	 * */
	public static List<String> divideText(final String text)
	{
		if (isStrEmpty(text))
		{ return Collections.emptyList(); }
		
		List<String> resultList = new ArrayList<>();
        StringReader re = new StringReader(text);
        IKSegmenter ik = new IKSegmenter(re, true);
        Lexeme lex = null;
        
        try
        {
        	while ((lex = ik.next()) != null) 
        	{ resultList.add(lex.getLexemeText()); }
        }
        catch (Exception ex)
        { log.error("分词出现异常，需要进行分词的为【{}】，异常原因为：", text, ex); }
        
        return resultList;
	}
	
	public static String substr(final String str, final int startPosition, final int length)
	{
		if (!isStrEmpty(str))
		{ return str.substring(startPosition, startPosition + length); }
		
		return "";
	}
}