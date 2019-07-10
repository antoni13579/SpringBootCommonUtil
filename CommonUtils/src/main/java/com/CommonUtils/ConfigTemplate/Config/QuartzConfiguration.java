package com.CommonUtils.ConfigTemplate.Config;

import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;


/*
########################################################
###JPA Config
########################################################
#create is recreate table, update is not
#spring.jpa.hibernate.ddl-auto=update
#spring.jpa.show-sql=true
*/


/**
 * 
 * #Spring boot actuator config
management.endpoint.shutdown.enabled=true
management.endpoints.web.exposure.include=info,health,shutdown,beans,conditions,configprops,env,httptrace,mappings,sessions,threaddump
 * */

/**
 * 持久化配置
 * #Quart base config
spring.quartz.jdbc.initialize-schema=embedded
spring.quartz.jdbc.schema=Antoni
spring.quartz.job-store-type=jdbc

spring.quartz.properties.org.quartz.threadPool.class=org.quartz.simpl.SimpleThreadPool
spring.quartz.properties.org.quartz.threadPool.threadCount=3
spring.quartz.properties.org.quartz.threadPool.threadPriority=5
spring.quartz.properties.org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread=true

spring.quartz.properties.org.quartz.jobStore.class=org.quartz.impl.jdbcjobstore.JobStoreTX
spring.quartz.properties.org.quartz.jobStore.driverDelegateClass=org.quartz.impl.jdbcjobstore.StdJDBCDelegate
spring.quartz.properties.org.quartz.jobStore.tablePrefix=QRTZ_
spring.quartz.properties.org.quartz.jobStore.isClustered=true
spring.quartz.properties.org.quartz.jobStore.clusterCheckinInterval=10000
spring.quartz.properties.org.quartz.jobStore.useProperties=false

spring.quartz.properties.org.quartz.scheduler.instanceName=ExtractDataToTDClusteredScheduler
spring.quartz.properties.org.quartz.scheduler.instanceId=AUTO
 * 
 * */
@Configuration
@EnableScheduling
//如果使用了QuartzConfig配置类来create bean，QuartzConfiguration类配置的内容会失效，以QuartzConfig配置的内容为主
public class QuartzConfiguration implements SchedulerFactoryBeanCustomizer
{	
	@Override
	public void customize(SchedulerFactoryBean schedulerFactoryBean) 
	{
		schedulerFactoryBean.setAutoStartup(true);
		schedulerFactoryBean.setStartupDelay(3);
		schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(true);
		schedulerFactoryBean.setOverwriteExistingJobs(false);
	}
}