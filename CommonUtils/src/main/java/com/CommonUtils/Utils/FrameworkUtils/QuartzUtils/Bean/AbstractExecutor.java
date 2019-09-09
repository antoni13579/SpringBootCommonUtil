package com.CommonUtils.Utils.FrameworkUtils.QuartzUtils.Bean;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 禁止并发执行多个相同定义的JobDetail, 这个注解是加在Job类上的, 但意思并不是不能同时执行多个Job, 而是不能并发执行同一个Job Definition(由JobDetail定义), 但是可以同时执行多个不同的JobDetail, 
 * 举例说明,我们有一个Job类,叫做SayHelloJob, 并在这个Job上加了这个注解, 然后在这个Job上定义了很多个JobDetail, 如sayHelloToJoeJobDetail, sayHelloToMikeJobDetail, 那么当scheduler启动时, 
 * 不会并发执行多个sayHelloToJoeJobDetail或者sayHelloToMikeJobDetail, 但可以同时执行sayHelloToJoeJobDetail跟sayHelloToMikeJobDetail
 * */
@DisallowConcurrentExecution

/**
 * 此标记说明在执行完Job的execution方法后保存JobDataMap当中固定数据,在默认情况下 也就是没有设置 @PersistJobDataAfterExecution的时候 每个job都拥有独立JobDataMap
         否则改任务在重复执行的时候具有相同的JobDataMap
 * 
 * 
 * 
 * 表示当正常执行完Job后, JobDataMap中的数据应该被改动, 以被下一次调用时用。当使用@PersistJobDataAfterExecution 注解时, 为了避免并发时, 存储数据造成混乱, 强烈建议把@DisallowConcurrentExecution注解也加上。
 * */
@PersistJobDataAfterExecution
//PersistJobDataAfterExecution与DisallowConcurrentExecution同时加入保证线程安全
public abstract class AbstractExecutor extends QuartzJobBean
{
	@Override
	protected abstract void executeInternal(JobExecutionContext context) throws JobExecutionException;
}