package com.CommonUtils.Config.SpringBatch.Config.Core;

import javax.sql.DataSource;

import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.DatabaseType;
import org.springframework.transaction.PlatformTransactionManager;

public final class JobRepositoryConfig 
{
	private JobRepositoryConfig() {}
	
	public static JobRepository getMapJobRepositoryInstance(final PlatformTransactionManager platformTransactionManager) throws Exception
	{
		MapJobRepositoryFactoryBean result = new MapJobRepositoryFactoryBean();;
		result.setTransactionManager(platformTransactionManager);
		return result.getObject();
	}
	
	/**
	 * clobType由Types clobType指定即可
	 * */
	public static JobRepository getJobRepositoryIntance(final DataSource dataSource, 
														final PlatformTransactionManager platformTransactionManager,
														final DatabaseType dbType,
														final int clobType) throws Exception
	{
		JobRepositoryFactoryBean result = new JobRepositoryFactoryBean();
		result.setDataSource(dataSource);
		result.setTransactionManager(platformTransactionManager);
		result.setDatabaseType(dbType.name());
		result.setClobType(clobType);
		return result.getObject();
	}
}