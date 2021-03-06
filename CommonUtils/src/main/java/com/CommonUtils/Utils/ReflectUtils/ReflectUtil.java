package com.CommonUtils.Utils.ReflectUtils;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import lombok.extern.slf4j.Slf4j;

/**
 * 有hutool相关工具类使用
 * @deprecated
 * */
@Deprecated(since="有hutool相关工具类使用")
@Slf4j
public final class ReflectUtil 
{
	private ReflectUtil() {}
	
	/**建议使用cn.hutool.core.util.ClassLoaderUtil.loadClass*/ 
	public static Class<?> getClass(final String classPath)
	{
		Class<?> clazz = null;
		try { clazz = Class.forName(classPath); }
		catch (Exception ex) { log.error("根据类路径获取Class出现异常，异常原因为：", ex); }
		return clazz;
	}
	
	/**
	 * 利用反射，获取BEAN中指定字段的值
	 * */
	/**建议使用cn.hutool.core.util.ReflectUtil.getFieldValue*/ 
	public static Object getBeanFieldValue(final Object obj, final String fieldName)
	{
		Object result = null;
		
		try
		{
			//获取所有的public及继承的字段
			Field[] fields = obj.getClass().getFields();
			
			//获取所有的字段包括private/public，但不包括继承的字段
			Field[] declaredFields = obj.getClass().getDeclaredFields();
			
			Set<Field> fieldSets = new HashSet<>();
			fieldSets.addAll(CollUtil.newArrayList(fields));
			fieldSets.addAll(CollUtil.newArrayList(declaredFields));
			
			for (Field field : fieldSets)
			{			
				field.setAccessible(true);
				if (field.getName().equalsIgnoreCase(fieldName))
				{
					result = field.get(obj);
					return result;
				}
			}
		}
		catch (Exception e)
		{ log.error("利用反射，获取BEAN中指定字段的值出现异常，异常原因为：{}", e); }
		
		return result;
	}
	
	/**
	 * 利用反射，获取BEAN中所有字段的值
	 * */
	/**建议使用cn.hutool.core.util.ReflectUtil.getFieldValue*/ 
	public static Map<String, Object> getBeanFieldValue(final Object obj)
	{
		Map<String, Object> result = new HashMap<>();
		
		try
		{
			//获取所有的public及继承的字段
			Field[] fields = obj.getClass().getFields();
			
			//获取所有的字段包括private/public，但不包括继承的字段
			Field[] declaredFields = obj.getClass().getDeclaredFields();
			
			Set<Field> fieldSets = new HashSet<>();
			fieldSets.addAll(CollUtil.newArrayList(fields));
			fieldSets.addAll(CollUtil.newArrayList(declaredFields));
			
			for (Field field : fieldSets)
			{			
				field.setAccessible(true);
				result.put(field.getName(), field.get(obj));
			}
		}
		catch (Exception e)
		{ log.error("利用反射，获取BEAN中所有字段的值出现异常，异常原因为：{}", e); }
		
		return result;
	}
	
	/**建议使用cn.hutool.core.util.ClassUtil.isAllAssignableFrom*/
	public static <T> boolean getTypeCompareResult(final Class<T> srcClazz, final String targetClassPath)
	{ return getTypeCompareResult(srcClazz, getClass(targetClassPath)); }
	
	/**建议使用cn.hutool.core.util.ClassUtil.isAllAssignableFrom*/
	public static <T> boolean getTypeCompareResult(final String srcClassPath, final Class<T> targetClazz)
	{ return getTypeCompareResult(getClass(srcClassPath), targetClazz); }
	
	/**建议使用cn.hutool.core.util.ClassUtil.isAllAssignableFrom*/
	public static boolean getTypeCompareResult(final String srcClassPath, final String targetClassPath)
	{ return getTypeCompareResult(getClass(srcClassPath), getClass(targetClassPath)); }
	
	/**建议使用cn.hutool.core.util.ClassUtil.isAllAssignableFrom*/
	public static <T1, T2> boolean getTypeCompareResult(final Class<T1> srcClazz, final Class<T2> targetClazz)
	{
		if (null == srcClazz || null == targetClazz)
		{ return false; }
		
		Set<Boolean> results = new HashSet<>();
		results.add(targetClazz.isAssignableFrom(srcClazz));
		results.add(srcClazz.isAssignableFrom(targetClazz));
		
		if (results.size() == 1) { return CollUtil.get(results, 0); }
		else { return true; }
	}
	
	public static <T> Class<T> getCastResult(final String srcClassPath, final Class<T> targetClazz)
	{ return getCastResult(getClass(srcClassPath), targetClazz); }
	
	public static <T> Class<?> getCastResult(final Class<T> srcClazz, final String targetClassPath)
	{ return getCastResult(srcClazz, getClass(targetClassPath)); }
	
	public static Class<?> getCastResult(final String srcClassPath, final String targetClassPath)
	{ return getCastResult(getClass(srcClassPath), getClass(targetClassPath)); }
	
	public static <T1, T2> Class<T2> getCastResult(final Class<T1> srcClazz, final Class<T2> targetClazz)
	{
		if (getTypeCompareResult(srcClazz, targetClazz))
		{ return Convert.convert(new TypeReference<Class<T2>>() {}, srcClazz); }
		else
		{ return null; }
	}
	
	/**根据类名，反查对应的JAR包名称*/
	public static String getJarPath(final Class<?> clazz)
	{
		if (null != clazz)
		{
			ClassLoader classLoader = clazz.getClassLoader();
			String classPath = clazz.getName().replace('.', '/') + ".class";
			URL url = classLoader.getResource(classPath);
			return url.getPath();
		}
		
		return "";
	}
	
	/**
	 * 获取Spring Mvc中，请求地址与对应的java处理方法
	 * */
	public static Map<String, String> getRequestMapping(final RequestMappingHandlerMapping requestMappingHandlerMapping)
	{
		Map<RequestMappingInfo, HandlerMethod> map =  requestMappingHandlerMapping.getHandlerMethods();
		Map<String, String> result = new HashMap<>();
		map.entrySet()
		   .forEach
		   (
				   entry -> 
				   {
					   RequestMappingInfo info = entry.getKey();  
			           HandlerMethod method = entry.getValue(); 
			           result.put(info.toString(), method.toString());
				   }
		   );
		return result;
	}
}