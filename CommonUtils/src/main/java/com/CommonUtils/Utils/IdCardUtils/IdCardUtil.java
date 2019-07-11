package com.CommonUtils.Utils.IdCardUtils;

import java.util.Date;

import com.CommonUtils.Utils.ArrayUtils.ArrayUtil;
import com.CommonUtils.Utils.CharacterUtils.CharacterUtil;
import com.CommonUtils.Utils.DateUtils.DateContants;
import com.CommonUtils.Utils.DateUtils.DateUtil;
import com.CommonUtils.Utils.StringUtils.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class IdCardUtil 
{
	private IdCardUtil() {}
	
	/**
	 * 将15位身份证号码转换为18位
	 * 
	 * @param idCard
	 *            15位身份编码
	 * @return 18位身份编码
	 * @throws Exception 
	 */
	public static String conver15CardTo18(final String idCard)
	{
		if (StringUtil.isStrEmpty(idCard) || idCard.length() != IdCardContants.CHINA_ID_MIN_LENGTH)
		{ return ""; }	
		
		String idCard18 = null;
		
		try
		{
			if (StringUtil.isNum(idCard))
			{
				// 获取出生年月日
				String birthday = idCard.substring(6, 12);
				Date birthDate = DateUtil.formatStrToDate(birthday, DateContants.DATE_FORMAT_1)
										 .map(date -> date)
										 .orElseThrow(() -> new Exception("将15位身份证号码转换为18位，获取15位身份证出生年月日出现异常！"));
				idCard18 = idCard.substring(0, 6) + String.valueOf(DateUtil.getYear(birthDate)) + idCard.substring(8);
				
				// 转换字符数组
				char[] cArr = idCard18.toCharArray();
				if (!ArrayUtil.isArrayEmpty(cArr))
				{
					int[] iCard = CharacterUtil.converCharToInt(cArr)
											   .map(arr -> arr)
											   .orElseThrow(() -> new Exception("将15位身份证号码转换为18位，将18位身份证的字符数组转换为int[]出现异常！"));
					int iSum17 = getPowerSum(iCard);
					// 获取校验位
					String sVal = getCheckCode18(iSum17);
					if (sVal.length() > 0) { idCard18 += sVal; }
				}
			}
			else
			{ idCard18 = ""; }
		}
		catch (Exception ex)
		{
			log.error("将15位身份证号码转换为18位出现异常，异常原因为：", ex);
			idCard18 = "";
		}
		
		return idCard18;
	}
	
	/**
	 * 将身份证的每位和对应位的加权因子相乘之后，再得到和值
	 * 
	 * @param iArr
	 * @return 身份证编码。
	 */
	public static int getPowerSum(final int[] iArr) 
	{
		int iSum = 0;
		if (IdCardContants.POWER.length == iArr.length) 
		{
			for (int i = 0; i < iArr.length; i++) 
			{
				for (int j = 0; j < IdCardContants.POWER.length; j++) 
				{
					if (i == j) 
					{ iSum = iSum + iArr[i] * IdCardContants.POWER[j]; }
				}
			}
		}
		return iSum;
	}
	
	/**
	 * 将power和值与11取模获得余数进行校验码判断
	 * 
	 * @param iSum
	 * @return 校验位
	 */
	public static String getCheckCode18(int iSum) 
	{
		String sCode = "";
		switch (iSum % 11) 
		{
			case 10:
				sCode = "2";
				break;
			case 9:
				sCode = "3";
				break;
			case 8:
				sCode = "4";
				break;
			case 7:
				sCode = "5";
				break;
			case 6:
				sCode = "6";
				break;
			case 5:
				sCode = "7";
				break;
			case 4:
				sCode = "8";
				break;
			case 3:
				sCode = "9";
				break;
			case 2:
				sCode = "x";
				break;
			case 1:
				sCode = "0";
				break;
			case 0:
				sCode = "1";
				break;
		}
		return sCode;
	}
}