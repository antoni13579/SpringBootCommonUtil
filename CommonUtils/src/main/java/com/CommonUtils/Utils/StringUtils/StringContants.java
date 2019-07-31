package com.CommonUtils.Utils.StringUtils;

public final class StringContants 
{
	/**
	 * 正则表达式，意思是只允许大小写字母与横杠的存在
	 * */
	public static final String PATTERN_1 = "^[A-Za-z-]+$";
	
	/**
	 * 正则表达式，意思是只允许整型数与浮点数的存在
	 * */
	public static final String PATTERN_2 = "^[-\\+]?[.\\d]*$";
	
	/**
	 * 正则表达式，提取出日期格式为yyyy-MM-dd的数据
	 * */
	public static final String PATTERN_3 = "\\d{4}-\\d{2}-\\d{2}";
	
	/**
	 * 正则表达式，提取出日期格式为yyyyMMdd的数据
	 * */
	public static final String PATTERN_4 = "\\d{4}\\d{2}\\d{2}";
	
	/**
	 * 正则表达式中\s匹配任何空白字符，包括空格、制表符、换页符等等, 等价于[ \f\n\r\t\v]
\f -> 匹配一个换页
\n -> 匹配一个换行符
\r -> 匹配一个回车符
\t -> 匹配一个制表符
\v -> 匹配一个垂直制表符
	 * */
	public static final String PATTERN_5 = "\\s+";
	
	/**验证邮箱格式的正则表达式*/
	public static final String PATTERN_6 = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
	
	/**验证数字的正则表达式*/
	public static final String PATTERN_7 = "^[0-9]*$";
	
	/**提取数字的正则表达式*/
	public static final String PATTERN_8 = "[^0-9]";
	
	/**验证国内座机号*/
	public static final String PATTERN_9 = "[0-9-()（）]{7,18}";
	
	/**验证国内座机号*/
	public static final String PATTERN_10 = "\\d{3}-\\d{8}|\\d{4}-\\{7,8}";
	
	/**验证国内手机号*/
	public static final String PATTERN_11 = "0?(13|14|15|18)[0-9]{9}";
}