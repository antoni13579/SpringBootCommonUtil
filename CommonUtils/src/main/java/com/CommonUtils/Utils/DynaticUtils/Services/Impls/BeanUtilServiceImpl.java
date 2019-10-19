package com.CommonUtils.Utils.DynaticUtils.Services.Impls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.CommonUtils.Utils.NetworkUtils.HttpUtils.HttpUtil;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

@Service("beanUtilService")
@Slf4j
public final class BeanUtilServiceImpl implements ApplicationContextAware
{
	private static ApplicationContext APPLICATION_CONTEXT = null;
	private static Binder BINDER = null;
	private static ObjectMapper OBJECT_MAPPER = null;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException 
	{
		APPLICATION_CONTEXT = applicationContext;
		BINDER = Binder.get(APPLICATION_CONTEXT.getEnvironment());
	}
	
	/**
	 * bean转map，请使用cn.hutool.core.bean.BeanUtil.beanToMap
	 * */
	@Deprecated
	public static <T> Map<String, Object> beanToMap(final T bean, final boolean isGetAll) throws IllegalArgumentException, IllegalAccessException
	{
		if (isGetAll)
		{ return com.CommonUtils.Utils.ReflectUtils.ReflectUtil.getBeanFieldValue(bean); }
		
		Map<String, Object> result = new HashMap<String, Object>();
		if (null != bean)
		{
			BeanMap beanMap = BeanMap.create(bean);
			for (Object key : beanMap.keySet())
			{ result.put(key + "", beanMap.get(key)); }
		}
		return result;
	}
	
	/**map转bean，请使用cn.hutool.core.bean.BeanUtil.mapToBean*/
	@Deprecated
	public static <T> T mapToBean(final Map<String, Object> map, final T bean)
	{
		BeanMap beanMap = BeanMap.create(bean);
		beanMap.putAll(map);
		return bean;
	}
	
	/**
	 * List<T>转换为List<Map<String, Object>> ，请使用cn.hutool.core.bean.BeanUtil.beanToMap
	 * */
	@Deprecated
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
	
	/**List<Map<String,Object>>转换为List<T> ，请使用cn.hutool.core.bean.BeanUtil.mapToBean
	 * */
	@Deprecated
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
	
	public static <T, R> List<R> transfer(final Function<? super T, ? extends R> mapper, final Collection<T> beans)
	{
		if (!CollUtil.isEmpty(beans))
		{ return beans.stream().map(mapper).collect(Collectors.toList()); }
		else
		{ return Collections.emptyList(); }
	}
	
	@SafeVarargs
	public static <T, R> List<R> transfer(final Function<? super T, ? extends R> mapper, final T ... beans)
	{
		if (!ArrayUtil.isEmpty(beans))
		{ return transfer(mapper, CollUtil.newArrayList(beans)); }
		else
		{ return Collections.emptyList(); }
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
			String srcJson = getInstanceForObjectMapper(Include.NON_DEFAULT).writeValueAsString(src);
			//String srcJson = JSON.toJSONString(src);
			
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
			synchronized (BeanUtilServiceImpl.class)
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
	
	/**
	 * 获取@Scope("session")的bean
	 * */
	@Deprecated
	public static Object getBean(final boolean allowCreateSession, final String beanName)
	{ return HttpUtil.getHttpSession(allowCreateSession).getAttribute(beanName); }
	
	public static Object getBean(final HttpServletRequest httpServletRequest, final boolean allowCreateSession, final String beanName)
	{ return httpServletRequest.getSession(allowCreateSession).getAttribute(beanName); }
	
	/**
	 * 注入@Scope("session")的bean
	 * */
	@Deprecated
	public static void setBean(final boolean allowCreateSession, final String beanName, final Object beanInstance)
	{ HttpUtil.getHttpSession(allowCreateSession).setAttribute(beanName, beanInstance); }
	
	public static void setBean(final HttpServletRequest httpServletRequest, final boolean allowCreateSession, final String beanName, final Object beanInstance)
	{ httpServletRequest.getSession(allowCreateSession).setAttribute(beanName, beanInstance); }
	
	public static <T> Optional<T> getBean(final Class<T> clazz)
	{
		T result = null;
		try { result = APPLICATION_CONTEXT.getBean(clazz); }
		catch (Exception ex) { log.error("获取Bean异常，异常原因为：", ex); }
		return Optional.ofNullable(result);
	}
	
	public static <T> Optional<T> getBean(final String name, final Class<T> clazz)
	{
		T result = null;
		try { result = APPLICATION_CONTEXT.getBean(name, clazz); }
		catch (Exception ex) { log.error("获取Bean异常，异常原因为：", ex); }
		return Optional.ofNullable(result);
	}
	
	public static Optional<Object> getBean(final String name)
	{
		Object result = null;
		try { result = APPLICATION_CONTEXT.getBean(name); }
		catch (Exception ex) { log.error("获取Bean异常，异常原因为：", ex); }
		return Optional.ofNullable(result);
	}
	
	public static <T> void addBean(final Class<T> beanClazz, final String beanName)
	{ addBean(beanClazz, beanName, null, null); }
	
	public static <T> void addBean(final Class<T> beanClazz, final String beanName, final String initMethodName, final String destroyMethodName)
	{
		try
		{
			GenericBeanDefinition bean = new GenericBeanDefinition();
			bean.setBeanClass(beanClazz);
			bean.setAutowireCandidate(true);
			
			if (!StrUtil.isEmptyIfStr(initMethodName))
			{ bean.setInitMethodName(initMethodName); }
			
			if (!StrUtil.isEmptyIfStr(destroyMethodName))
			{ bean.setDestroyMethodName(destroyMethodName); }
			
			DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) APPLICATION_CONTEXT.getAutowireCapableBeanFactory();
			defaultListableBeanFactory.registerBeanDefinition(beanName, bean);
			defaultListableBeanFactory.initializeBean(bean, beanName);
		}
		catch (Exception ex) { log.error("新增Bean异常，异常原因为：", ex); }
	}
	
	public static <T> void addBean(final Class<T> beanClazz, final String beanName, final String initMethodName, final String destroyMethodName, final Map<String, Object> fieldInfo)
	{
		try
		{
			//创建bean信息.  
			BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClazz);
			
			//设置bean字段信息
			CollUtil.forEach//JavaCollectionsUtil.mapProcessor
			(
					fieldInfo, 
					(final String key, final Object value, final int indx) -> 
					{ beanDefinitionBuilder.addPropertyValue(key, value); }
			);
			
			if (!StrUtil.isEmptyIfStr(initMethodName))
			{ beanDefinitionBuilder.setInitMethodName(initMethodName); }
			
			if (!StrUtil.isEmptyIfStr(destroyMethodName))
			{ beanDefinitionBuilder.setDestroyMethodName(destroyMethodName); }
			
			//获取BeanFactory  
			DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) APPLICATION_CONTEXT.getAutowireCapableBeanFactory();
			defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
		}
		catch (Exception ex) { log.error("新增Bean异常，异常原因为：", ex); }
	}
	
	public static void removeBean(final String beanName)
	{
		//获取BeanFactory  
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) APPLICATION_CONTEXT.getAutowireCapableBeanFactory();
		//删除bean.  
		defaultListableBeanFactory.removeBeanDefinition(beanName);
	}
	
	/**
	 * 显示所有Bean的基础信息
	 * */
	public static Map<String, Class<?>> showBeansBaseInfo(final boolean isPrintInfo)
	{
		Map<String, Class<?>> result = new HashMap<String, Class<?>>();
		
		if (isPrintInfo)
		{ log.info("============================================显示已加载的BEAN----开始==============================================="); }
		
		String[] beans = APPLICATION_CONTEXT.getBeanDefinitionNames();
		CollUtil.newArrayList(beans).forEach
		(
				bean -> 
				{
					result.put(bean, APPLICATION_CONTEXT.getType(bean));
					
					if (isPrintInfo)
					{ log.info("Bean名称为：{}，类型为：{}", bean, APPLICATION_CONTEXT.getType(bean)); }
				}
		);
		
		if (isPrintInfo)
		{ log.info("============================================显示已加载的BEAN----结束==============================================="); }
		
		return result;
	}
	
	public static int shutdownApplication()
	{ return SpringApplication.exit(APPLICATION_CONTEXT); }
	
	/**用于在application.properties获取属性，
	 * 
	 * 
	 * 假设在propertes配置中有这样一个配置：com.didispace.foo=bar
	 * 
	 * @Data
		@ConfigurationProperties(prefix = "com.didispace")
		public class FooProperties {

    	private String foo;

}


		FooProperties foo = binder.bind("com.didispace", Bindable.of(FooProperties.class)).get();
	 * 
	 * */
	public static <T> T getProperties(final String propertiesKey, final Class<T> clazz)
	{ return BINDER.bind(propertiesKey, Bindable.of(clazz)).get(); }
	
	public static <K, V> Map<K, V> getProperties(final String propertiesKey, final Class<K> keyType, final Class<V> valueType)
	{ return BINDER.bind(propertiesKey, Bindable.mapOf(keyType, valueType)).get(); }
	
	public static <T> List<T> getPropertiesList(final String propertiesKey, final Class<T> clazz)
	{ return BINDER.bind(propertiesKey, Bindable.listOf(clazz)).get(); }
	
	public static <T> Set<T> getPropertiesSet(final String propertiesKey, final Class<T> clazz)
	{ return BINDER.bind(propertiesKey, Bindable.setOf(clazz)).get(); }
}