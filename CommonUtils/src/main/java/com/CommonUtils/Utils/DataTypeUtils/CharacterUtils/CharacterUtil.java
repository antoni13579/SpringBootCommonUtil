package com.CommonUtils.Utils.DataTypeUtils.CharacterUtils;

import java.util.Optional;

import cn.hutool.core.util.ArrayUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 用hutool代替
 * @deprecated
 * */
@Slf4j
@Deprecated(since="用hutool代替")
public final class CharacterUtil 
{
	private CharacterUtil() {}
	
	/**String转char, radix为进制数，转换为10进制、16进制等等，建议使用(char)cn.hutool.core.util.NumberUtil.parseInt("0x1b")代替*/
	public static char toChar(final String str, final int radix)
	{
		char result = Character.MIN_VALUE;
		
		try
		{
			String tmp = str.substring(str.indexOf('x', 0) + 1, str.length());
			result = (char)Integer.parseInt(tmp, radix);
		}
		catch (Exception ex)
		{ log.error("String转char出现异常，无法转换，异常原因为：", ex); }
		
		return result;
	}
	
	public static Optional<int[]> converCharToInt(final char[] ca) 
	{
		if (ArrayUtil.isEmpty(ca))
		{ return Optional.ofNullable(null); }
		
		int len = ca.length;
		int[] iArr = new int[len];
		
		for (int i = 0; i < len; i++) 
		{ iArr[i] = Integer.parseInt(String.valueOf(ca[i])); }
		
		return Optional.ofNullable(iArr);
	}
	
	/**建议使用cn.hutool.core.convert.Convert.toChar*/ 
	public static char getChar(final Object obj) throws CharacterUtilException
	{
		if (obj instanceof Character)
		{ return (char)obj; }
		else
		{ throw new CharacterUtilException("无法转换为char类型"); }
	}
	
	private static class CharacterUtilException extends Exception
	{
		private static final long serialVersionUID = -4115460077798060091L;
		
		private CharacterUtilException(final String message)
		{ super(message); }
	}
}