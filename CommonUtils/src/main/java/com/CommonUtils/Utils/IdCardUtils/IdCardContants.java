package com.CommonUtils.Utils.IdCardUtils;

import java.util.HashMap;
import java.util.Map;

/**已废弃，请使用cn.hutool.core.util.IdcardUtil
 * @deprecated
 * */
@Deprecated(since="已废弃，请使用cn.hutool.core.util.IdcardUtil")
public final class IdCardContants 
{
	private IdCardContants() {}
	
	/** 中国公民身份证号码最小长度。 */
	public static final int CHINA_ID_MIN_LENGTH = 15;

	/** 中国公民身份证号码最大长度。 */
	public static final int CHINA_ID_MAX_LENGTH = 18;
	
	/** 每位加权因子 */
	protected static final int[] POWER = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
	
	public static final Map<Integer, String> CITY_CODES = new HashMap<>();
	
	static
	{
		CITY_CODES.put(11, "北京");
		CITY_CODES.put(12, "天津");
		CITY_CODES.put(13, "河北");
		CITY_CODES.put(14, "山西");
		CITY_CODES.put(15, "内蒙古");
		CITY_CODES.put(21, "辽宁");
		CITY_CODES.put(22, "吉林");
		CITY_CODES.put(23, "黑龙江");
		CITY_CODES.put(31, "上海");
		CITY_CODES.put(32, "江苏");
		CITY_CODES.put(33, "浙江");
		CITY_CODES.put(34, "安徽");
		CITY_CODES.put(35, "福建");
		CITY_CODES.put(36, "江西");
		CITY_CODES.put(37, "山东");
		CITY_CODES.put(41, "河南");
		CITY_CODES.put(42, "湖北");
		CITY_CODES.put(43, "湖南");
		CITY_CODES.put(44, "广东");
		CITY_CODES.put(45, "广西");
		CITY_CODES.put(46, "海南");
		CITY_CODES.put(50, "重庆");
		CITY_CODES.put(51, "四川");
		CITY_CODES.put(52, "贵州");
		CITY_CODES.put(53, "云南");
		CITY_CODES.put(54, "西藏");
		CITY_CODES.put(61, "陕西");
		CITY_CODES.put(62, "甘肃");
		CITY_CODES.put(63, "青海");
		CITY_CODES.put(64, "宁夏");
		CITY_CODES.put(65, "新疆");
		CITY_CODES.put(71, "台湾");
		CITY_CODES.put(81, "香港");
		CITY_CODES.put(82, "澳门");
		CITY_CODES.put(91, "国外");
	}
}