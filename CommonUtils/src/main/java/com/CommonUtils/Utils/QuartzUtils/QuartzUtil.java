package com.CommonUtils.Utils.QuartzUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.Trigger.TriggerState;

import com.CommonUtils.Quartz.Bean.QuartzJobsInfo;
import com.CommonUtils.Utils.ArrayUtils.ArrayUtil;
import com.CommonUtils.Utils.CollectionUtils.JavaCollectionsUtil;
import com.CommonUtils.Utils.StringUtils.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class QuartzUtil //implements ApplicationListener<ContextClosedEvent>
{
	private QuartzUtil() {}
	
	private static boolean init = false;
	
	private static void init(final Scheduler scheduler) throws SchedulerException
	{
		if (!init)
		{
			scheduler.clear();
			init = true;
		}
	}
	
	public static void buildJob(final QuartzJobsInfo quartzJobsInfo, final Scheduler scheduler)
	{
		if (null == quartzJobsInfo)
		{ return; }
		
		String group = quartzJobsInfo.getGroup();
		String jobName = quartzJobsInfo.getJobName();
		String schedulingCycle = quartzJobsInfo.getSchedulingCycle();
		long startTime = quartzJobsInfo.getStartTime();
		
        //创建任务
        JobDetail jobDetail = JobBuilder.newJob(quartzJobsInfo.getExecutorClass())
        								.withIdentity(jobName, group)
        								.build();
        
        //设置任务传递参数
        if (!JavaCollectionsUtil.isMapEmpty(quartzJobsInfo.getParams()))
        { jobDetail.getJobDataMap().putAll(quartzJobsInfo.getParams()); }
        
        //创建任务触发器
        Trigger trigger = setTrigger(schedulingCycle, startTime, jobName, group);        
        try
        {
        	init(scheduler);
            //将触发器与任务绑定到调度器内
        	scheduler.scheduleJob(jobDetail, trigger);
        }
        catch (Exception ex)
        { log.error("建立job失败，异常原因为：", ex); }
	}
	
	public static boolean checkExists(final JobKey jobKey, final Scheduler scheduler)
	{
		boolean result;
		try
		{ result = scheduler.checkExists(jobKey); }
		catch (Exception ex)
		{
			log.error("检查job是否存在出现异常，异常原因为：", ex);
			result = false;
		}
		return result;
	}
	
	public static boolean checkExists(final QuartzJobsInfo quartzJobsInfo, final Scheduler scheduler)
	{ return checkExists(new JobKey(quartzJobsInfo.getJobName(), quartzJobsInfo.getGroup()), scheduler); }
	
	public static void deleteJobs(final List<JobKey> jobKeys, final Scheduler scheduler)
	{
		if (!JavaCollectionsUtil.isCollectionEmpty(jobKeys))
		{
			try { scheduler.deleteJobs(jobKeys); }
			catch (Exception ex) { log.error("删除job出现异常，异常原因为：", ex); }
		}
	}
	
	public static void deleteJobs(final Scheduler scheduler, final JobKey ... jobKeys)
	{
		if (!ArrayUtil.isArrayEmpty(jobKeys))
		{ deleteJobs(Arrays.asList(jobKeys), scheduler); }
	}
	
	public static void deleteJobs(final Scheduler scheduler, final QuartzJobsInfo ... quartzJobsInfos)
	{
		if (!ArrayUtil.isArrayEmpty(quartzJobsInfos))
		{
			List<JobKey> jobKeys = new ArrayList<JobKey>();
			for (QuartzJobsInfo quartzJobsInfo : quartzJobsInfos)
			{ jobKeys.add(new JobKey(quartzJobsInfo.getJobName(), quartzJobsInfo.getGroup())); }
			deleteJobs(jobKeys, scheduler);
		}
	}
	
	public static TriggerState getJobState(final TriggerKey triggerKey, final Scheduler scheduler)
	{
		TriggerState result = null;
		try { result = scheduler.getTriggerState(triggerKey); }
		catch (Exception ex) { log.error("获取job状态异常，异常原因为：", ex); }
		return result;
	}
	
	public static TriggerState getJobState(final QuartzJobsInfo quartzJobsInfo, final Scheduler scheduler)
	{ return getJobState(new TriggerKey(quartzJobsInfo.getJobName(), quartzJobsInfo.getGroup()), scheduler); }
	
	public static void modifyJob(final QuartzJobsInfo quartzJobsInfo, final Scheduler scheduler)
	{
		//任务所属分组
		String group = quartzJobsInfo.getGroup();
		String jobName = quartzJobsInfo.getJobName();
		TriggerKey triggerKey = new TriggerKey(jobName, group);
		long startTime = quartzJobsInfo.getStartTime();
		String schedulingCycle = quartzJobsInfo.getSchedulingCycle();
		//创建任务触发器
        Trigger trigger = setTrigger(schedulingCycle, startTime, jobName, group);	        
        try
        { scheduler.rescheduleJob(triggerKey, trigger); }
        catch (Exception ex)
        { log.error("修改job失败，异常原因为：", ex); }
	}
	
	private static Trigger setTrigger(final String schedulingCycle, final long startTime, final String jobName, final String group)
	{
		Trigger trigger;
		if (startTime > 0 && StringUtil.isStrEmpty(schedulingCycle)) 
        {
        	trigger = TriggerBuilder.newTrigger()
									.withIdentity(jobName, group)
									.startAt(new Date(startTime))
									.build();
        }
        else if (startTime <= 0 && !StringUtil.isStrEmpty(schedulingCycle))
        {
        	trigger = TriggerBuilder.newTrigger()
									.withIdentity(jobName, group)
									.withSchedule(CronScheduleBuilder.cronSchedule(schedulingCycle))
									.build();
        }
        else
        { trigger = null; }
		return trigger;
	}

	/**
	 * 监听Spring关闭事件，释放对应的资源
	 * */
	/*
	@Override
	public void onApplicationEvent(ContextClosedEvent arg0) 
	{
		try
		{
			this.beanUtilService
				.getBean(Scheduler.class)
				.shutdown(true);
		}
		catch (Exception ex)
		{ log.error("QuartzJobsUtilServiceImpl清理资源异常，异常原因为：", ex); }
	}
	*/
}