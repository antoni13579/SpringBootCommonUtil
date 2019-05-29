package com.CommonUtils.Utils.JsonUtils;

import com.alibaba.fastjson.JSON;

public final class JsonUtil 
{
	private JsonUtil() {}
	
	public static String toJsonByFastjson(final Object obj)
	{ return JSON.toJSONString(obj); }
}