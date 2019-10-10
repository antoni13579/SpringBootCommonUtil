package com.CommonUtils.Utils.JsonUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**Object转换为json建议使用cn.hutool.json.JSONUtil.toJsonPrettyStr或toJsonStr*/
@Slf4j
@Deprecated
public final class JsonUtil 
{
	private JsonUtil() {}
	
	private static ObjectMapper OBJECT_MAPPER = null;
	
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
	
	private static ObjectMapper getInstanceForObjectMapper()
	{
		if (null == OBJECT_MAPPER)
		{
			synchronized (JsonUtil.class)
			{
				if (null == OBJECT_MAPPER)
				{ OBJECT_MAPPER = new ObjectMapper(); }
			}
		}
		
		return OBJECT_MAPPER;
	}
}