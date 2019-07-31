package com.CommonUtils.Utils.JsonUtils;

import java.util.ArrayList;
import java.util.Collection;

public final class JsonUtil 
{
	private JsonUtil() {}
	
	public static String toJsonByFastjson(final Object obj)
	{ return com.alibaba.fastjson.JSON.toJSONString(obj); }
	
	public static <T> Collection<T> jsonArrayToCollection(final org.json.JSONArray jsonArray, final HandlerForJsonArrayTransferToCollection<T> handlerForJsonArrayTransferToCollection)
	{
		Collection<T> collection = new ArrayList<>();
		for (Object obj : jsonArray)
		{
			org.json.JSONObject tmpJsonObject = (org.json.JSONObject)obj;
			handlerForJsonArrayTransferToCollection.process(tmpJsonObject, collection);
		}
		return collection;
	}
	
	@FunctionalInterface
	public interface HandlerForJsonArrayTransferToCollection<T>
	{ void process(final org.json.JSONObject jsonObject, final Collection<T> collection); }
}