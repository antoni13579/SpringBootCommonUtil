package com.CommonUtils.Quartz;

import java.util.concurrent.Executor;

import javax.sql.DataSource;

import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.quartz.JobStoreType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.transaction.PlatformTransactionManager;

import com.CommonUtils.Utils.CollectionUtils.CustomCollections.Properties;

/**
 * 使用此配置类，记得使用create bean功能，
 * 编写这个类，主要是因为不想用属性文件来配置Quartz，而是使用Java配置的模式
 * */
public final class QuartzConfig 
{
	private QuartzConfig() {}
	
	/**
	 * 这个create bean的时候需要注意一下，入参需要ApplicationContext applicationContext
	 * 如
	 * @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }
	 * */
	public static JobFactory getJobFactoryInstance(final ApplicationContext context)
	{
		AutowiringSpringBeanJobFactory result = new AutowiringSpringBeanJobFactory();
		result.setApplicationContext(context);
		return result;
	}

	public static SchedulerFactoryBean getSchedulerFactoryBeanInstance(final DataSource dataSource, 
																	   final PlatformTransactionManager platformTransactionManager,
																	   final JobFactory jobFactory, 
																	   final Executor executor,
																	   final JobStoreType jobStoreType,
																	   final String instanceName) throws Exception
	{
		SchedulerFactoryBean result = new SchedulerFactoryBean();
		Properties properties = new Properties();
		
		//设置线程池
		if (null != executor)
		{ result.setTaskExecutor(executor); }
		else
		{
			properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool")
					  .setProperty("org.quartz.threadPool.threadCount", "3")
					  .setProperty("org.quartz.threadPool.threadPriority", "5")
					  .setProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
		}
		
		//设置作业存储方式
		switch (jobStoreType)
		{
			case MEMORY:
				properties.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
				break;
			
			/**
			 * 
			 * 据说..................
			 * 配置好数据源dataSource后，需要在Quartz的QRTZ_LOCKS表中插入以下数据：
			INSERT INTO QRTZ_LOCKS values('TRIGGER_ACCESS');
			INSERT INTO QRTZ_LOCKS values('JOB_ACCESS');
			否则会报
			org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'scheduler' defined in file [...webappsWEB-INFclassesconfigapplicationContext-quartz.xml]: 
			Invocation of init method failed; nested exception is org.quartz.SchedulerConfigException: Failure occured during job recovery. 
			[See nested exception: org.quartz.impl.jdbcjobstore.LockException: Failure obtaining db row lock: No row exists in table QRTZ_LOCKS for lock named: 
			TRIGGER_ACCESS [See nested exception: java.sql.SQLException: No row exists in table QRTZ_LOCKS for lock named: TRIGGER_ACCESS]]异常
			 * 
			 * */
			case JDBC:
				if (null == dataSource)
				{ throw new Exception("需要把作业存储到数据库，必须要指定数据库连接池"); }
				
				//数据库连接池不为空的情况
				else
				{
					//如果事务管理器不为空，那么同步设置数据库连接池与事务管理器
					if (null != platformTransactionManager)
					{
						result.setDataSource(dataSource);
						result.setTransactionManager(platformTransactionManager);
					}
					
					//否则只设置为非事务性数据库连接池
					else
					{ result.setNonTransactionalDataSource(dataSource); }
				}
				
				properties.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX")
						  .setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate")
						  .setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_")
						  .setProperty("org.quartz.jobStore.isClustered", "true")
						  .setProperty("org.quartz.jobStore.clusterCheckinInterval", "10000")
						  .setProperty("org.quartz.jobStore.useProperties", "false")
						  .setProperty("org.quartz.scheduler.instanceName", instanceName)
						  .setProperty("org.quartz.scheduler.instanceId", "AUTO");
				break;
				
			default:
				throw new Exception("没有指定作业存储方式！");
		}
		
		result.setJobFactory(jobFactory);
		result.setQuartzProperties(properties.setProperty("org.quartz.jobStore.misfireThreshold", "60000").getProperties());
		
		result.setAutoStartup(true);
		result.setStartupDelay(3);
		result.setWaitForJobsToCompleteOnShutdown(true);
		result.setOverwriteExistingJobs(false);
		return result;
	}
	
	public static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware
	{
		//此变量不参与序列化
		private transient AutowireCapableBeanFactory beanFactory;
		
		@Override
        public void setApplicationContext(final ApplicationContext context) 
		{ this.beanFactory = context.getAutowireCapableBeanFactory(); }
		
		@Override
        protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception
		{
            final Object job = super.createJobInstance(bundle);
            this.beanFactory.autowireBean(job);
            return job;
        }
	}
}