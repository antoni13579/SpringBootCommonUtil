package com.CommonUtils.Utils.CharacterUtils;

import java.util.Optional;

import com.CommonUtils.Utils.ArrayUtils.ArrayUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class CharacterUtil 
{
	private CharacterUtil() {}
	
	/**String转char, radix为进制数，转换为10进制、16进制等等*/
	public static char toChar(final String str, final int radix)
	{
		char result = Character.MIN_VALUE;
		
		try
		{
			String tmp = str.substring(str.indexOf("x", 0) + 1, str.length());
			result = (char)Integer.parseInt(tmp, radix);
		}
		catch (Exception ex)
		{ log.error("String转char出现异常，无法转换，异常原因为：", ex); }
		
		return result;
	}
	
	public static Optional<int[]> converCharToInt(final char[] ca) 
	{
		if (ArrayUtil.isArrayEmpty(ca))
		{ return Optional.ofNullable(null); }
		
		int len = ca.length;
		int[] iArr = new int[len];
		
		for (int i = 0; i < len; i++) 
		{ iArr[i] = Integer.parseInt(String.valueOf(ca[i])); }
		
		return Optional.ofNullable(iArr);
	}
}