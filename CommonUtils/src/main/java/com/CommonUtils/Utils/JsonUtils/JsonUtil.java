package com.CommonUtils.Utils.JsonUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	
	public static <T> Collection<T> jsonArrayToCollection(final org.json.JSONArray jsonArray, final HandlerForJsonArrayTransferToCollection<T> handlerForJsonArrayTransferToCollection)
	{
		if (null == jsonArray || null == handlerForJsonArrayTransferToCollection)
		{ return Collections.emptyList(); }
		
		Collection<T> collection = new ArrayList<>();
		jsonArray.forEach
		(
				obj -> 
				{ collection.add(handlerForJsonArrayTransferToCollection.process((org.json.JSONObject)obj)); }
		);
		return collection;
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
	
	@FunctionalInterface
	public interface HandlerForJsonArrayTransferToCollection<T>
	{ T process(final org.json.JSONObject jsonObject); }
}