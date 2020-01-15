package com.CommonUtils.Utils.FrameworkUtils.QuartzUtils;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public enum CronTriggerMisfireHandlerType 
{
	WITH_MISFIRE_HANDLING_INSTRUCTION_DO_NOTHING("不触发立即执行，等待下次Cron触发频率到达时刻开始按照Cron频率依次执行"),
	WITH_MISFIRE_HANDLING_INSTRUCTION_IGNORE_MISFIRES("以错过的第一个频率时间立刻开始执行，重做错过的所有频率周期后，当下一次触发频率发生时间大于当前时间后，再按照正常的Cron频率依次执行"),
	WITH_MISFIRE_HANDLING_INSTRUCTION_FIRE_AND_PROCEED("以当前时间为触发频率立刻触发一次执行，然后按照Cron频率依次执行，这个是默认策略");
	
	private final String type;
	
	private CronTriggerMisfireHandlerType(final String type) 
	{ this.type = type; }
}