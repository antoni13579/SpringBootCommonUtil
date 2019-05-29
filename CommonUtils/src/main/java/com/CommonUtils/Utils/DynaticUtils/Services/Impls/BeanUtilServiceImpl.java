package com.CommonUtils.Utils.DynaticUtils.Services.Impls;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.CommonUtils.Utils.StringUtils.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Service("beanUtilService")
@Slf4j
public final class BeanUtilServiceImpl implements ApplicationContextAware
{
	private static ApplicationContext APPLICATION_CONTEXT = null;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException 
	{ APPLICATION_CONTEXT = applicationContext; }
	
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
			
			if (!StringUtil.isStrEmpty(initMethodName))
			{ bean.setInitMethodName(initMethodName); }
			
			if (!StringUtil.isStrEmpty(destroyMethodName))
			{ bean.setDestroyMethodName(destroyMethodName); }
			
			DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) APPLICATION_CONTEXT.getAutowireCapableBeanFactory();
			defaultListableBeanFactory.registerBeanDefinition(beanName, bean);
			defaultListableBeanFactory.initializeBean(bean, beanName);
		}
		catch (Exception ex) { log.error("新增Bean异常，异常原因为：", ex); }
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
		Arrays.asList(beans).forEach
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
}