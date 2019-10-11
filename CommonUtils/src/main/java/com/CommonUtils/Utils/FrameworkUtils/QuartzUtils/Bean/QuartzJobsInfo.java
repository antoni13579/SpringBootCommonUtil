package com.CommonUtils.Utils.FrameworkUtils.QuartzUtils.Bean;

import java.util.Collections;
import java.util.Map;

import org.springframework.scheduling.quartz.QuartzJobBean;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public final class QuartzJobsInfo 
{
	private String jobName;
	
	private String executorClassPath;
	private Class<? extends QuartzJobBean> executorClass;
	
	private String schedulingCycle;
	private long startTime;
	private Map<String, Object> params;
	private String group;
	
	private void init(final String jobName, 
					  final String executorClassPath, 
					  final Class<? extends QuartzJobBean> executorClass, 
					  final String schedulingCycle,
					  final long startTime,
					  final Map<String, Object> params,
					  final String group)
	{
		this.jobName = jobName;
		this.executorClassPath = executorClassPath;
		this.executorClass = executorClass;
		this.schedulingCycle = schedulingCycle;
		this.startTime = startTime;
		this.params = params;
		
		if (!StrUtil.isEmptyIfStr(group))
		{ this.group = group; }
		else
		{ this.group = executorClassPath; }
	}
	
	public QuartzJobsInfo(final String jobName, 
			  			  final String executorClassPath, 
			  			  final String schedulingCycle,
			  			  final long startTime,
			  			  final Map<String, Object> params,
			  			  final String group)
	{ init(jobName, executorClassPath, Convert.convert(new TypeReference<QuartzJobBean>() {}, ClassLoaderUtil.loadClass(executorClassPath, false)).getClass(), schedulingCycle, startTime, params, group); }
	
	public QuartzJobsInfo(final String jobName, 
			  			  final String executorClassPath, 
			  			  final long startTime,
			  			  final Map<String, Object> params,
			  			  final String group)
	{ init(jobName, executorClassPath, Convert.convert(new TypeReference<QuartzJobBean>() {}, ClassLoaderUtil.loadClass(executorClassPath, false)).getClass(), "", startTime, params, group); }
	
	public QuartzJobsInfo(final String jobName, 
			  			  final String executorClassPath, 
			  			  final String schedulingCycle,
			  			  final long startTime,
			  			  final String group)
	{ init(jobName, executorClassPath, Convert.convert(new TypeReference<QuartzJobBean>() {}, ClassLoaderUtil.loadClass(executorClassPath, false)).getClass(), schedulingCycle, startTime, Collections.emptyMap(), group); }
	
	public QuartzJobsInfo(final String jobName, 
			  			  final String executorClassPath, 
			  			  final long startTime,
			  			  final String group)
	{ init(jobName, executorClassPath, Convert.convert(new TypeReference<QuartzJobBean>() {}, ClassLoaderUtil.loadClass(executorClassPath, false)).getClass(), "", startTime, Collections.emptyMap(), group); }
	
	public QuartzJobsInfo(final String jobName, 
			  			  final String executorClassPath, 
			  			  final String schedulingCycle,
			  			  final long startTime,
			  			  final Map<String, Object> params)
	{ init(jobName, executorClassPath, Convert.convert(new TypeReference<QuartzJobBean>() {}, ClassLoaderUtil.loadClass(executorClassPath, false)).getClass(), schedulingCycle, startTime, params, ""); }
	
	public QuartzJobsInfo(final String jobName, 
			  			  final String executorClassPath, 
			  			  final long startTime,
			  			  final Map<String, Object> params)
	{ init(jobName, executorClassPath, Convert.convert(new TypeReference<QuartzJobBean>() {}, ClassLoaderUtil.loadClass(executorClassPath, false)).getClass(), "", startTime, params, ""); }
	
	public QuartzJobsInfo(final String jobName, 
			  			  final String executorClassPath, 
			  			  final String schedulingCycle,
			  			  final long startTime)
	{ init(jobName, executorClassPath, Convert.convert(new TypeReference<QuartzJobBean>() {}, ClassLoaderUtil.loadClass(executorClassPath, false)).getClass(), schedulingCycle, startTime, Collections.emptyMap(), ""); }
	
	public QuartzJobsInfo(final String jobName, 
			  			  final String executorClassPath, 
			  			  final long startTime)
	{ init(jobName, executorClassPath, Convert.convert(new TypeReference<QuartzJobBean>() {}, ClassLoaderUtil.loadClass(executorClassPath, false)).getClass(), "", startTime, Collections.emptyMap(), ""); }
	
	public QuartzJobsInfo(final String jobName, 
			  			  final Class<? extends QuartzJobBean> executorClass, 
			  			  final String schedulingCycle,
			  			  final long startTime,
			  			  final Map<String, Object> params,
			  			  final String group)
	{ init(jobName, executorClass.getName(), executorClass, schedulingCycle, startTime, params, group); }
	
	public QuartzJobsInfo(final String jobName, 
			  			  final Class<? extends QuartzJobBean> executorClass, 
			  			  final long startTime,
			  			  final Map<String, Object> params,
			  			  final String group)
	{ init(jobName, executorClass.getName(), executorClass, "", startTime, params, group); }
	
	public QuartzJobsInfo(final String jobName, 
			  			  final Class<? extends QuartzJobBean> executorClass, 
			  			  final String schedulingCycle,
			  			  final long startTime,
			  			  final String group)
	{ init(jobName, executorClass.getName(), executorClass, schedulingCycle, startTime, Collections.emptyMap(), group); }
	
	public QuartzJobsInfo(final String jobName, 
			  			  final Class<? extends QuartzJobBean> executorClass, 
			  			  final long startTime,
			  			  final String group)
	{ init(jobName, executorClass.getName(), executorClass, "", startTime, Collections.emptyMap(), group); }
	
	public QuartzJobsInfo(final String jobName, 
			  			  final Class<? extends QuartzJobBean> executorClass, 
			  			  final String schedulingCycle,
			  			  final long startTime,
			  			  final Map<String, Object> params)
	{ init(jobName, executorClass.getName(), executorClass, schedulingCycle, startTime, params, ""); }
	
	public QuartzJobsInfo(final String jobName, 
			  			  final Class<? extends QuartzJobBean> executorClass, 
			  			  final long startTime,
			  			  final Map<String, Object> params)
	{ init(jobName, executorClass.getName(), executorClass, "", startTime, params, ""); }
	
	public QuartzJobsInfo(final String jobName, 
			  			  final Class<? extends QuartzJobBean> executorClass, 
			  			  final String schedulingCycle,
			  			  final long startTime)
	{ init(jobName, executorClass.getName(), executorClass, schedulingCycle, startTime, Collections.emptyMap(), ""); }
	
	public QuartzJobsInfo(final String jobName, 
			  			  final Class<? extends QuartzJobBean> executorClass, 
			  			  final long startTime)
	{ init(jobName, executorClass.getName(), executorClass, "", startTime, Collections.emptyMap(), ""); }
}