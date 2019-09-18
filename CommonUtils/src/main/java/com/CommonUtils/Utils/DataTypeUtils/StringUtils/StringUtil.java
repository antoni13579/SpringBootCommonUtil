package com.CommonUtils.Utils.DataTypeUtils.StringUtils;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import com.CommonUtils.Utils.DataTypeUtils.ArrayUtils.ArrayUtil;

import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;
import com.CommonUtils.Utils.DataTypeUtils.DateUtils.DateContants;
import com.CommonUtils.Utils.DataTypeUtils.DateUtils.DateFormat;
import com.CommonUtils.Utils.DataTypeUtils.DateUtils.DateUtil;
import com.CommonUtils.Utils.DataTypeUtils.StringUtils.Bean.Calculation;

import cn.hutool.core.convert.Convert;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class StringUtil 
{
	private StringUtil() {}
	
	/**建议使用cn.hutool.core.convert.Convert.toStr*/ 
	@Deprecated
	public static <T> String getString(final T obj) throws Exception
	{
		if (obj instanceof String)
		{ return String.valueOf(obj); }
		else
		{ throw new Exception("无法转换为String类型"); }
	}
	
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
			boolean val = Convert.toBool(value);
			String result = Boolean.toString(val);
			return result;
		}
		else if (value instanceof Integer)
		{
			int val = Convert.toInt(value);
			String result = Integer.toString(val);
			return result;
		}
		else if (value instanceof Short)
		{
			short val = Convert.toShort(value);
			String result = Short.toString(val);
			return result;
		}
		else if (value instanceof Float)
		{
			float val = Convert.toFloat(value);
			String result = Float.toString(val);
			return result;
		}
		else if (value instanceof Double)
		{
			double val = Convert.toDouble(value);
			String result = Double.toString(val);
			return result;
		}
		else if (value instanceof Character)
		{
			char val = Convert.toChar(value);
			String result = Character.toString(val);
			return result;
		}
		else if (value instanceof Long)
		{
			long val = Convert.toLong(value);
			String result = Long.toString(val);
			return result;
		}
		else if (value instanceof Byte)
		{
			byte val = Convert.toByte(value);
			String result = Byte.toString(val);
			return result;
		}
		else if (value instanceof String)
		{
			String val = StringUtil.getString(value);
			return val;
		}
		else if (value instanceof BigDecimal)
		{
			BigDecimal val = Convert.toBigDecimal(value);
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
		//StringBuilder sb = new StringBuilder();
		Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(string);
        /*
        while (m.find()) 
        {
            String g = m.group();
            sb.append(g);
        }
        return sb.toString();
        */
        
        String result = m.replaceAll("");
        if (isStrEmpty(result)) { return ""; }
        else { return result; }
	}
	
	/**
	 * 验证输入的字符串，是否符合正则表达式
	 * @param string 需要判断的字符串；
	 * 		  regex 正则表达式
	 * @return 返回布尔值，true为验证通过，false为验证不通过
	 * */
	public static boolean validateRegular(final String string, final String regex)
	{
		//Pattern p = Pattern.compile(regex);
        //Matcher m = p.matcher(string);
        //return m.find();
        
        return Pattern.matches(regex, string);
        //return m.matches();
	}
	
	public static int getNumberStartPos(final String str)
	{
		if (isStrEmpty(str))
		{ return -1; }
		
		int result = -1;
		for (int i = 0; i < str.length(); i++)
		{
			char c = str.charAt(i);
			if (c >='0' && c <='9')
			{
				result = i;
				break;
			}
		}
		
		return result;
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
	
	/**
	 * 数字验证
	 * 
	 * @param val
	 * @return 提取的数字。
	 */
	public static boolean isNum(final String val) 
	{ return isStrEmpty(val) ? false : val.matches(StringContants.PATTERN_7); }
	
	public static Optional<Calculation> statistics(final String text1, final String text2)
	{
		//计算类
        Calculation calculation = null;
        
		if (StringUtil.isStrEmpty(text1) || StringUtil.isStrEmpty(text2))
		{ return Optional.ofNullable(calculation); }
		
		Map<String,int[]> resultMap = new HashMap<>();
		
		//统计
        statistics(resultMap, StringUtil.divideText(text1),1);
        statistics(resultMap, StringUtil.divideText(text2),0);
        
        calculation = new Calculation();
        Iterator<Map.Entry<String, int[]>> entries = resultMap.entrySet().iterator();
		while (entries.hasNext())
		{
			Map.Entry<String, int[]> entry = entries.next();
			int[] value = entry.getValue();
			
			BigDecimal numerator = calculation.getNumerator();
			BigDecimal elementA = calculation.getElementA();
			BigDecimal elementB = calculation.getElementB();
			
			calculation.setNumerator(numerator.add(new BigDecimal(value[0] * value[1])));
            calculation.setElementA(elementA.add(new BigDecimal(value[0] * value[0])));
            calculation.setElementB(elementB.add(new BigDecimal(value[1] * value[1])));
		}
        
        return Optional.ofNullable(calculation); 
	}
	
	 /**
     * 组合词频向量
     
     * @param words
     * @param direction
     * @return
     */
    private static void statistics(Map<String,int[]> map,List<String> words ,int direction)
    {
        if(JavaCollectionsUtil.isCollectionEmpty(words))
        { return; }
        
        int[] in = null;
        boolean flag = direction == 1 ? true : false;     //判断不同句子
        for (String word : words)
        {
            int[] wordD = map.get(word);
            
            if(ArrayUtil.isArrayEmpty(wordD))
            {
                if(flag) { in = new int[]{1, 0}; }
                else { in = new int[]{0, 1}; }
                map.put(word,in);
            }
            
            else
            {
                if(flag) { wordD[0]++; }
                else { wordD[1]++; }
            }
        }
    }
}