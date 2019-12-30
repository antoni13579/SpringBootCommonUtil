package com.CommonUtils.Config.IO.Quartz.Config;

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

import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.CustomCollections.Properties;

/**
 * 使用此配置类，记得使用create bean功能，
 * 编写这个类，主要是因为不想用属性文件来配置Quartz，而是使用Java配置的模式
 * */
/**
 * 
 * QRTZ_CALENDARS	以 Blob 类型存储 Quartz 的 Calendar 信息
QRTZ_CRON_TRIGGERS	存储 Cron Trigger，包括 Cron 表达式和时区信息
QRTZ_FIRED_TRIGGERS	存储与已触发的 Trigger 相关的状态信息，以及相联 Job 的执行信息
QRTZ_PAUSED_TRIGGER_GRPS	存储已暂停的 Trigger 组的信息
QRTZ_SCHEDULER_STATE	存储少量的有关 Scheduler 的状态信息，和别的 Scheduler 实例(假如是用于一个集群中)
QRTZ_LOCKS	存储程序的非观锁的信息(假如使用了悲观锁)
QRTZ_JOB_DETAILS	存储每一个已配置的 Job 的详细信息
QRTZ_JOB_LISTENERS	存储有关已配置的 JobListener 的信息
QRTZ_SIMPLE_TRIGGERS	存储简单的 Trigger，包括重复次数，间隔，以及已触的次数
QRTZ_BLOG_TRIGGERS Trigger	作为 Blob 类型存储(用于 Quartz 用户用 JDBC 创建他们自己定制的 Trigger 类型，JobStore 并不知道如何存储实例的时候)
QRTZ_TRIGGER_LISTENERS	存储已配置的 TriggerListener 的信息
QRTZ_TRIGGERS	存储已配置的 Trigger 的信息
 * 
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
	
	public static SchedulerFactoryBean getSchedulerFactoryBeanInstanceInJdbc(final DataSource dataSource, 
		    																 final PlatformTransactionManager platformTransactionManager,
		    																 final JobFactory jobFactory, 
		    																 final String instanceName) throws Exception
	{ return getSchedulerFactoryBeanInstance(dataSource, platformTransactionManager, jobFactory, null, JobStoreType.JDBC, instanceName); }
	
	public static SchedulerFactoryBean getSchedulerFactoryBeanInstanceInMemory(final JobFactory jobFactory) throws Exception
	{ return getSchedulerFactoryBeanInstance(null, null, jobFactory, null, JobStoreType.MEMORY, null); }

	private static SchedulerFactoryBean getSchedulerFactoryBeanInstance(final DataSource dataSource, 
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
			  		  .setProperty("org.quartz.threadPool.threadCount", "15")
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
						  
						  //设置为 true 打开集群特性。如果你有多个 Quartz 实例在用同一套数据库时，这个属性就必须设置为 true。
						  .setProperty("org.quartz.jobStore.isClustered", "true")
						  
						  //设置一个频度(毫秒)，用于实例报告给集群中的其他实例。这会影响到侦测失败实例的敏捷度。它只用于设置了 isClustered 为 true 的时候。
						  .setProperty("org.quartz.jobStore.clusterCheckinInterval", "10000")
						  
						  //“use properties” 标记指示着持久性 JobStore 所有在 JobDataMap 中的值都是字符串，因此能以 名-值 对的形式存储，而不用让更复杂的对象以序列化的形式存入 BLOB 列中。这样会更方便，因为让你避免了发生于序列化你的非字符串的类到 BLOB 时的有关类版本的问题。
						  .setProperty("org.quartz.jobStore.useProperties", "false")
						  .setProperty("org.quartz.scheduler.instanceName", instanceName)
						  
						  //值为 true 时告知 Quartz(当使用 JobStoreTX 或 CMT) 调用 JDBC 连接的 setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE) 方法。这有助于阻止某些数据库在高负载和长时间事物时锁的超时。
						  .setProperty("org.quartz.jobStore.txIsolationLevelSerializable", "true")
						  
						  //这必须是一个从 LOCKS 表查询一行并对这行记录加锁的 SQL 语句。假如未设置，默认值就是 SELECT * FROM {0}LOCKS WHERE LOCK_NAME = ? FOR UPDATE，这能在大部分数据库上工作。{0} 会在运行期间被前面你配置的 TABLE_PREFIX 所替换。
						  //.setProperty("org.quartz.jobStore.selectWithLockSQL", "SELECT * FROM {0}LOCKS WHERE LOCK_NAME = ? FOR UPDATE")
						  
						  //设置这个参数为 true 会告诉 Quartz 从数据源获取的连接后不要调用它的 setAutoCommit(false) 方法。这在少些情况下是有帮助的，比如假如你有这样一个驱动，它会抱怨本来就是关闭的又来调用这个方法。这个属性默认值是 false，因为大多数的驱动都要求调用 setAutoCommit(false)。
						  //.setProperty("org.quartz.jobStore.dontSetAutoCommitFalse", "false")
						  
						  .setProperty("org.quartz.scheduler.instanceId", "AUTO");
				break;
				
			default:
				throw new Exception("没有指定作业存储方式！");
		}
		
		result.setJobFactory(jobFactory);
		
		
		result.setQuartzProperties
		(
				properties
					//这里需要注意的是，quartz对于冲突的判定有一个容忍期，见配置文件当中的 org.quartz.jobStore.misfireThreshold。 如果冲突时间在这个范围以内不会被判为冲突，依然会执行
					//在 Trigger 被认为是错过触发之前，Scheduler 还容许 Trigger 通过它的下次触发时间的毫秒数。默认值(假如你未在配置中存在这一属性条目) 是 60000(60 秒)。这个不仅限于 JDBC-JobStore；它也可作为 RAMJobStore 的参数
					.setProperty("org.quartz.jobStore.misfireThreshold", "60000")
					
					//这是 JobStore 能处理的错过触发的 Trigger 的最大数量。处理太多(超过两打) 很快会导致数据库表被锁定够长的时间，这样就妨碍了触发别的(还未错过触发) trigger 执行的性能。
					//.setProperty("org.quartz.jobStore.maxMisfiresToHandleAtATime", "20")
					.getProperties()
		);
		
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