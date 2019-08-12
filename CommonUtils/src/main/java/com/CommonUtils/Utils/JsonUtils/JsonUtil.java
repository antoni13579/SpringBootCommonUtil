package com.CommonUtils.Utils.JsonUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public final class JsonUtil 
{
	private JsonUtil() {}
	
	public static String toJsonByFastjson(final Object obj)
	{ return com.alibaba.fastjson.JSON.toJSONString(obj); }
	
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
	
	@FunctionalInterface
	public interface HandlerForJsonArrayTransferToCollection<T>
	{ T process(final org.json.JSONObject jsonObject); }
}