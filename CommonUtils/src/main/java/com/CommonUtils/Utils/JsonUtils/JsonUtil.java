package com.CommonUtils.Utils.JsonUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**Object转换为json建议使用cn.hutool.json.JSONUtil.toJsonPrettyStr或toJsonStr
 * @deprecated
 * */
@Deprecated(since="Object转换为json建议使用cn.hutool.json.JSONUtil.toJsonPrettyStr或toJsonStr")
@Slf4j
public final class JsonUtil 
{
	private JsonUtil() {}
	
	public static String toJson(final Object obj)
	{
		String result = null;
		try
		{ result = getInstanceForObjectMapper().writeValueAsString(obj); }
		catch (Exception ex)
		{
			log.error("转换json出现异常，异常原因为：", ex);
			result = "";
		}
		return result;
	}
	
	private static class SingletonContainer
	{ private static ObjectMapper instance = new ObjectMapper(); }
	
	private static ObjectMapper getInstanceForObjectMapper()
	{ return SingletonContainer.instance; }
}