package com.CommonUtils.Utils.FrameworkUtils.QuartzUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.Trigger.TriggerState;

import com.CommonUtils.Utils.FrameworkUtils.QuartzUtils.Bean.QuartzJobsInfo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
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
	
	public static void buildJob(final QuartzJobsInfo quartzJobsInfo, final Scheduler scheduler, final CronTriggerMisfireHandlerType cronTriggerMisfireHandlerType)
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
        if (!CollUtil.isEmpty(quartzJobsInfo.getParams()))
        { jobDetail.getJobDataMap().putAll(quartzJobsInfo.getParams()); }
        
        //创建任务触发器
        Trigger trigger = getCronTrigger(schedulingCycle, startTime, jobName, group, cronTriggerMisfireHandlerType);        
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
		if (!CollUtil.isEmpty(jobKeys))
		{
			try { scheduler.deleteJobs(jobKeys); }
			catch (Exception ex) { log.error("删除job出现异常，异常原因为：", ex); }
		}
	}
	
	public static void deleteJobs(final Scheduler scheduler, final JobKey ... jobKeys)
	{
		if (!ArrayUtil.isEmpty(jobKeys))
		{ deleteJobs(CollUtil.newArrayList(jobKeys), scheduler); }
	}
	
	public static void deleteJobs(final Scheduler scheduler, final QuartzJobsInfo ... quartzJobsInfos)
	{
		if (!ArrayUtil.isEmpty(quartzJobsInfos))
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
	
	public static void modifyJob(final QuartzJobsInfo quartzJobsInfo, final Scheduler scheduler, final CronTriggerMisfireHandlerType cronTriggerMisfireHandlerType)
	{
		//任务所属分组
		String group = quartzJobsInfo.getGroup();
		String jobName = quartzJobsInfo.getJobName();
		TriggerKey triggerKey = new TriggerKey(jobName, group);
		long startTime = quartzJobsInfo.getStartTime();
		String schedulingCycle = quartzJobsInfo.getSchedulingCycle();
		//创建任务触发器
        Trigger trigger = getCronTrigger(schedulingCycle, startTime, jobName, group, cronTriggerMisfireHandlerType);	        
        try
        { scheduler.rescheduleJob(triggerKey, trigger); }
        catch (Exception ex)
        { log.error("修改job失败，异常原因为：", ex); }
	}
	
	public static Trigger getCronTrigger(final String schedulingCycle, final long startTime, final String jobName, final String group, final CronTriggerMisfireHandlerType cronTriggerMisfireHandlerType)
	{
		Trigger trigger;
		if (startTime > 0 && StrUtil.isEmptyIfStr(schedulingCycle)) 
        {
        	trigger = TriggerBuilder.newTrigger()
									.withIdentity(jobName, group)
									.startAt(new Date(startTime))
									.build();
        }
        else if (startTime <= 0 && !StrUtil.isEmptyIfStr(schedulingCycle))
        {
        	CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(schedulingCycle);
        	switch (cronTriggerMisfireHandlerType)
        	{
        		case WITH_MISFIRE_HANDLING_INSTRUCTION_DO_NOTHING:
        			cronScheduleBuilder.withMisfireHandlingInstructionDoNothing();
        			break;
        		case WITH_MISFIRE_HANDLING_INSTRUCTION_IGNORE_MISFIRES:
        			cronScheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
        			break;
        		case WITH_MISFIRE_HANDLING_INSTRUCTION_FIRE_AND_PROCEED:
        			cronScheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
        			break;
        		default:
        			log.warn("CronTriggerMisfireHandlerType枚举指定不正确");
        			break;
        	}
        	
        	trigger = TriggerBuilder.newTrigger()
									.withIdentity(jobName, group)
									.withSchedule(cronScheduleBuilder)
									.build();
        }
        else
        { trigger = null; }
		return trigger;
	}
	
	/**
	 * SimpleTrigger
	 * 
	 * SimpleScheduleBuilder ssb = SimpleScheduleBuilder.simpleSchedule();

withMisfireHandlingInstructionFireNow
——以当前时间为触发频率立即触发执行
——执行至FinalTIme的剩余周期次数
——以调度或恢复调度的时刻为基准的周期频率，FinalTime根据剩余次数和当前时间计算得到
——调整后的FinalTime会略大于根据starttime计算的到的FinalTime值

withMisfireHandlingInstructionIgnoreMisfires
——以错过的第一个频率时间立刻开始执行
——重做错过的所有频率周期
——当下一次触发频率发生时间大于当前时间以后，按照Interval的依次执行剩下的频率
——共执行RepeatCount+1次

withMisfireHandlingInstructionNextWithExistingCount（默认）
——不触发立即执行
——等待下次触发频率周期时刻，执行至FinalTime的剩余周期次数
——以startTime为基准计算周期频率，并得到FinalTime
——即使中间出现pause，resume以后保持FinalTime时间不变


withMisfireHandlingInstructionNowWithExistingCount（默认）
——以当前时间为触发频率立即触发执行
——执行至FinalTIme的剩余周期次数
——以调度或恢复调度的时刻为基准的周期频率，FinalTime根据剩余次数和当前时间计算得到
——调整后的FinalTime会略大于根据starttime计算的到的FinalTime值

withMisfireHandlingInstructionNextWithRemainingCount
——不触发立即执行
——等待下次触发频率周期时刻，执行至FinalTime的剩余周期次数
——以startTime为基准计算周期频率，并得到FinalTime
——即使中间出现pause，resume以后保持FinalTime时间不变

withMisfireHandlingInstructionNowWithRemainingCount
——以当前时间为触发频率立即触发执行
——执行至FinalTIme的剩余周期次数
——以调度或恢复调度的时刻为基准的周期频率，FinalTime根据剩余次数和当前时间计算得到

——调整后的FinalTime会略大于根据starttime计算的到的FinalTime值

MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT
——此指令导致trigger忘记原始设置的starttime和repeat-count
——触发器的repeat-count将被设置为剩余的次数
——这样会导致后面无法获得原始设定的starttime和repeat-count值
	 * */
	public static Trigger getSimpleTrigger(final String jobName, final String group, final SimpleScheduleBuilder simpleScheduleBuilder)
	{
		Trigger trigger = TriggerBuilder.newTrigger()
										.withIdentity(jobName, group)
										.withSchedule(simpleScheduleBuilder)
										.build();
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