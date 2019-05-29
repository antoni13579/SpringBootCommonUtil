package com.CommonUtils.Utils.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanMap;

import com.CommonUtils.Utils.ReflectUtils.ReflectUtil;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class BeanUtil 
{
	private static ObjectMapper OBJECT_MAPPER = null;
	
	private BeanUtil() {}
	
	/**
	 * bean转map
	 * */
	public static <T> Map<String, Object> beanToMap(final T bean, final boolean isGetAll) throws IllegalArgumentException, IllegalAccessException
	{
		if (isGetAll)
		{ return ReflectUtil.getBeanFieldValue(bean); }
		
		Map<String, Object> result = new HashMap<String, Object>();
		if (null != bean)
		{
			BeanMap beanMap = BeanMap.create(bean);
			for (Object key : beanMap.keySet())
			{ result.put(key + "", beanMap.get(key)); }
		}
		return result;
	}
	
	/**map转bean*/
	public static <T> T mapToBean(final Map<String, Object> map, final T bean)
	{
		BeanMap beanMap = BeanMap.create(bean);
		beanMap.putAll(map);
		return bean;
	}
	
	/**
	 * List<T>转换为List<Map<String, Object>> 
	 * */
	public static <T> List<Map<String, Object>> beanListToMapList(final List<T> objectList, final boolean isGetAll) throws IllegalArgumentException, IllegalAccessException
	{
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (null != objectList && !objectList.isEmpty())
		{
			for (T bean : objectList)
			{
				Map<String, Object> map = beanToMap(bean, isGetAll);
				result.add(map);
			}
		}
		return result;
	}
	
	/**List<Map<String,Object>>转换为List<T> 
	 * */
	public static <T> List<T> mapListToBeanList(final List<Map<String,Object>> mapList, final Class<T> clazz) throws InstantiationException, IllegalAccessException
	{
		List<T> result = new ArrayList<T>();
		if (null != mapList && !mapList.isEmpty())
		{
			for (Map<String,Object> map : mapList)
			{
				T bean = clazz.newInstance();
				mapToBean(map, bean);
				result.add(bean);
			}
		}
		return result;
	}
	
	/**
	 * Bean相互转化，如DTO与BEAN属性转化，适用于两个实体属性字段几乎完全相同
	 * */
	public static boolean convertBySpring(final Object src, final Object dst, final String ... ignoreProperties)
	{
		try
		{
			BeanUtils.copyProperties(src, dst, ignoreProperties);
			return true;
		}
		catch (Exception ex)
		{
			log.error("Bean相互转化失败，异常原因为：{}", ex);
			return false;
		}
	}
	
	/**
	 * Bean相互转化，如DTO与BEAN属性转化，适用于两个实体属性字段只有部分字段相同
	 * */
	public static boolean convertByJackson(final Object src, final Object dst)
	{
		try
		{
			//读入需要更新的目标实体
			ObjectReader objectReader = getInstanceForObjectMapper(Include.NON_DEFAULT).readerForUpdating(dst);
			
			//此代码把实体转换为json，但根据Include进行过滤
			//String srcJson = getInstanceForObjectMapper(JsonInclude.Include.NON_DEFAULT).writeValueAsString(src)
			String srcJson = JSON.toJSONString(src);
			
			//将源实体的值赋值到目标实体
			objectReader.readValue(srcJson);
			return true;
		}
		catch (Exception ex)
		{
			log.error("Bean相互转化失败，异常原因为：{}", ex);
			return false;
		}
	}
	
	private static ObjectMapper getInstanceForObjectMapper(final Include incl)
	{
		if (null == OBJECT_MAPPER)
		{
			synchronized (BeanUtil.class)
			{
				if (null == OBJECT_MAPPER)
				{
					OBJECT_MAPPER = new ObjectMapper();
					
					/*
					 * 配置该objectMapper在反序列化时，忽略目标对象没有的属性。
					 * 凡是使用该objectMapper反序列化时，都会拥有该特性
					 * */
					OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					
					/*
					 * 通过该方法对mapper对象进行设置
					 * 所有序列化的对象都将按照规则进行序列化
					 * Include.ALWAYS 默认
					 * Include.NON_DEFAULT 属性为默认值的不序列化
					 * Include.NON_EMPTY 属性为空（""）或者为NULL都不序列化
					 * Include.NON_NULL 属性为NULL不序列化
					 * */
					OBJECT_MAPPER.setSerializationInclusion(incl);
				}
			}
		}
		
		return OBJECT_MAPPER;
	}
}