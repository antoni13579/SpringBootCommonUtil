package com.CommonUtils.Utils.DataTypeUtils.StringUtils;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.tokenizer.TokenizerUtil;
import cn.hutool.extra.tokenizer.Word;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class StringUtil 
{
	private StringUtil() {}
	
	public static Map<String, Long> wordCount(final Collection<String> strings)
	{
		if (!CollUtil.isEmpty(strings))
		{ return wordCount(strings.toArray(new String[strings.size()])); }
		else
		{ return Collections.emptyMap(); }
	}
	
	public static Map<String, Long> wordCount(final String ... strings)
	{
		if (!cn.hutool.core.util.ArrayUtil.isEmpty(strings))
		{			
			return CollUtil.newArrayList(strings)
						 .stream()
						 .map(String::trim)
						 .filter(value -> !StrUtil.isEmpty(value))
						 .map(value -> value.split(StringContants.PATTERN_5))
						 .flatMap(Stream::of)
						 .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		}
		else
		{ return Collections.emptyMap(); }
	}
	
	public static int getNumberStartPos(final String str)
	{
		if (StrUtil.isEmpty(str))
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
	
	/**建议使用cn.hutool.core.convert.Convert.toStr
	 * @deprecated
	 * */ 
	@Deprecated(since="建议使用cn.hutool.core.convert.Convert.toStr")
	public static <T> String getString(final T obj) throws StringUtilException
	{
		if (obj instanceof String)
		{ return String.valueOf(obj); }
		else
		{ throw new StringUtilException("无法转换为String类型"); }
	}
	
	/**
	 * 检测字符串是否为空，true为空，false为非空，建议使用cn.hutool.core.util.StrUtil.isEmpty相关方法代替
	 * @deprecated
	 * */
	@Deprecated(since="检测字符串是否为空，true为空，false为非空，建议使用cn.hutool.core.util.StrUtil.isEmpty相关方法代替")
	public static boolean isStrEmpty(final String str)
	{ return null == str || "".equals(str.trim()) || str.length() == 0; }
	
	/**
	 * 检测两个字符串是否相等，相等则返回true，不相等则返回false，建议使用cn.hutool.core.util.StrUtil.equals
	 * @deprecated
	 * */
	@Deprecated(since="检测两个字符串是否相等，相等则返回true，不相等则返回false，建议使用cn.hutool.core.util.StrUtil.equals")
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
	
	/**建议使用cn.hutool.core.net.URLEncoder.encode
	 * @deprecated
	 * */
	@Deprecated(since="建议使用cn.hutool.core.net.URLEncoder.encode")
	public static String toUtf8String(final String str) throws UnsupportedEncodingException
	{
		StringBuilder sb = new StringBuilder();
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
	
	/**建议使用cn.hutool.core.util.ObjectUtil.toString
	 * @deprecated
	 * */
	@Deprecated(since="建议使用cn.hutool.core.util.ObjectUtil.toString")
	public static <T> String toString(final T value) throws StringUtilException
	{
		if (null == value)
		{ return ""; }
		else if (value instanceof java.util.Date || 
				 value instanceof java.sql.Date ||
				 value instanceof java.sql.Timestamp ||
				 value instanceof oracle.sql.TIMESTAMP)
		{ throw new StringUtilException("toString(final T value)不能处理时间类型，请使用toString(final oracle.sql.TIMESTAMP date, final DateFomat dateFomat)或toString(final java.util.Date date, final DateFomat dateFomat)"); }
		else if (value instanceof Boolean)
		{
			boolean val = Convert.toBool(value);
			return Boolean.toString(val);
		}
		else if (value instanceof Integer)
		{
			int val = Convert.toInt(value);
			return Integer.toString(val);
		}
		else if (value instanceof Short)
		{
			short val = Convert.toShort(value);
			return Short.toString(val);
		}
		else if (value instanceof Float)
		{
			float val = Convert.toFloat(value);
			return Float.toString(val);
		}
		else if (value instanceof Double)
		{
			double val = Convert.toDouble(value);
			return Double.toString(val);
		}
		else if (value instanceof Character)
		{
			char val = Convert.toChar(value);
			return Character.toString(val);
		}
		else if (value instanceof Long)
		{
			long val = Convert.toLong(value);
			return Long.toString(val);
		}
		else if (value instanceof Byte)
		{
			byte val = Convert.toByte(value);
			return Byte.toString(val);
		}
		else if (value instanceof String)
		{ return StringUtil.getString(value); }
		else if (value instanceof BigDecimal)
		{
			BigDecimal val = Convert.toBigDecimal(value);
			return val.toString();
		}
		else
		{
			log.warn("需进行ToString的数据类型为：{}", value.getClass());
			throw new StringUtilException("出现了新的数据类型，请及时处理");
		}
	}
	
	/**
	 * 建议使用cn.hutool.core.date.DateTime进行格式转换
	 * @deprecated
	 * */
	@Deprecated(since="建议使用cn.hutool.core.date.DateTime进行格式转换")
	public static String toString(final oracle.sql.TIMESTAMP date, final com.CommonUtils.Utils.DataTypeUtils.DateUtils.DateFormat dateFomat) throws SQLException
	{ return toString(date.timestampValue(), dateFomat); }
	
	/**
	 * 建议使用cn.hutool.core.date.DateTime进行格式转换
	 * @deprecated
	 * */
	@Deprecated(since="建议使用cn.hutool.core.date.DateTime进行格式转换")
	public static String toString(final java.util.Date date, final com.CommonUtils.Utils.DataTypeUtils.DateUtils.DateFormat dateFomat)
	{
		if (null == date)
		{ return ""; }
		
		switch (dateFomat)
		{
			case DATE:
				return DateUtil.format(date, DatePattern.NORM_DATETIME_PATTERN);
			case TIMESTAMP:
				return DateUtil.format(date, DatePattern.NORM_DATETIME_MS_PATTERN);
			default:
				return "";
		}
	}
	
	/**
	 * 建议使用cn.hutool.core.util.StrUtil.padPre
	 * @deprecated
	 * */
	@Deprecated(since="建议使用cn.hutool.core.util.StrUtil.padPre")
	public static String lpad(final String str, final int length, final char replaceStr)
	{
		int actuallyLength = length - str.length();
		StringBuilder sb = new StringBuilder();
		
		while (sb.length() < actuallyLength)
		{ sb.append(replaceStr); }
		
		sb.append(str);
		
		return sb.toString();
	}
	
	/**
	 * 建议使用cn.hutool.core.util.StrUtil.padAfter
	 * @deprecated
	 * */
	@Deprecated(since="建议使用cn.hutool.core.util.StrUtil.padAfter")
	public static String rpad(final String str, final int length, final char replaceStr)
	{
		StringBuilder sb = new StringBuilder(str);
		
		while (sb.length() < length)
		{ sb.append(replaceStr); }
		
		return sb.toString();
	}
	
	/**建议使用cn.hutool.core.util.ReUtil.get的相关方法
	 * @deprecated
	 * */ 
	@Deprecated(since="建议使用cn.hutool.core.util.ReUtil.get的相关方法")
	public static String searchStr(final String regex, final String string)
	{
		Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(string);
        
        String result = m.replaceAll("");
        if (isStrEmpty(result)) { return ""; }
        else { return result; }
	}
	
	/**
	 * 验证输入的字符串，是否符合正则表达式
	 * @deprecated
	 * @param string 需要判断的字符串；
	 * 		  regex 正则表达式
	 * @return 返回布尔值，true为验证通过，false为验证不通过
	 * */
	/**建议使用cn.hutool.core.util.ReUtil.isMatch的相关方法*/ 
	@Deprecated(since="建议使用cn.hutool.core.util.ReUtil.isMatch的相关方法")
	public static boolean validateRegular(final String string, final String regex)
	{ return Pattern.matches(regex, string); }
	
	/**
	 * 分词工具
	 * @deprecated
	 * */
	/**建议使用cn.hutool.extra.tokenizer.TokenizerUtil相关工具类
	 * 
	 * 解析文本并分词
	 //自动根据用户引入的分词库的jar来自动选择使用的引擎
	 TokenizerEngine engine = TokenizerUtil.createEngine();

	 //解析文本
	 String text = "这两个方法的区别在于返回值";
	 Result result = engine.parse(text);
	 //输出：这 两个 方法 的 区别 在于 返回 值
	 String resultStr = CollUtil.join((Iterator<Word>)result, " ");
	 
	 
	 
	 自定义模板引擎
	此处以HanLP为例：

	TokenizerEngine engine = new HanLPEngine();

	//解析文本
	String text = "这两个方法的区别在于返回值";
	Result result = engine.parse(text);
	//输出：这 两个 方法 的 区别 在于 返回 值
	String resultStr = CollUtil.join((Iterator<Word>)result, " ");
	 * */ 
	@Deprecated(since="建议使用cn.hutool.extra.tokenizer.TokenizerUtil相关工具类")
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
	
	/**
	 * 建议使用cn.hutool.core.util.StrUtil.subWithLength
	 * @deprecated
	 * */
	@Deprecated(since=" 建议使用cn.hutool.core.util.StrUtil.subWithLength")
	public static String substr(final String str, final int startPosition, final int length)
	{
		if (!isStrEmpty(str))
		{ return str.substring(startPosition, startPosition + length); }
		
		return "";
	}
	
	/**
	 * 数字验证
	 * @deprecated
	 * 
	 * @param val
	 * @return 提取的数字
	 * 
	 * 建议使用cn.hutool.core.util.ReUtil.isMatch
	 */
	@Deprecated(since="建议使用cn.hutool.core.util.ReUtil.isMatch")
	public static boolean isNum(final String val) 
	{ return val.matches(StringContants.PATTERN_7); }
	
	/**建议使用cn.hutool.core.util.StrUtil.similar
	 * @deprecated
	 * */
	@Deprecated(since="建议使用cn.hutool.core.util.StrUtil.similar")
	public static Optional<com.CommonUtils.Utils.DataTypeUtils.StringUtils.Bean.Calculation> statistics(final String text1, final String text2)
	{
		//计算类
		com.CommonUtils.Utils.DataTypeUtils.StringUtils.Bean.Calculation calculation = null;
        
		if (StringUtil.isStrEmpty(text1) || StringUtil.isStrEmpty(text2))
		{ return Optional.ofNullable(calculation); }
		
		Map<String,int[]> resultMap = new HashMap<>();
		
        statistics(resultMap, CollUtil.list(false, (Iterable<Word>)TokenizerUtil.createEngine().parse(text1)).stream().map(Word::getText).collect(Collectors.toList()), true);
        statistics(resultMap, CollUtil.list(false, (Iterable<Word>)TokenizerUtil.createEngine().parse(text2)).stream().map(Word::getText).collect(Collectors.toList()), false);
        
        calculation = new com.CommonUtils.Utils.DataTypeUtils.StringUtils.Bean.Calculation();
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
     @deprecated
     * @param words
     * @param direction
     * @return
     */
	/**建议使用cn.hutool.core.util.StrUtil.similar*/
	@Deprecated(since="建议使用cn.hutool.core.util.StrUtil.similar")
    private static void statistics(Map<String,int[]> map,List<String> words ,boolean direction)
    {
        if(CollUtil.isEmpty(words))
        { return; }
        
        int[] in = null;
        for (String word : words)
        {
            int[] wordD = map.get(word);
            
            if(ArrayUtil.isEmpty(wordD))
            {
                if(direction) { in = new int[]{1, 0}; }
                else { in = new int[]{0, 1}; }
                map.put(word,in);
            }
            
            else
            {
                if(direction) { wordD[0]++; }
                else { wordD[1]++; }
            }
        }
    }
	
	private static class StringUtilException extends Exception
	{
		private static final long serialVersionUID = 672110601709299801L;

		private StringUtilException(final String message)
		{ super(message); }
	}
}