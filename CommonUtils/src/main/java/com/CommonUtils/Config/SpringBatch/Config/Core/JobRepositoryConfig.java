package com.CommonUtils.Config.SpringBatch.Config.Core;

import javax.sql.DataSource;

import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.DatabaseType;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

public final class JobRepositoryConfig 
{
	private JobRepositoryConfig() {}
	
	public static JobRepository getMapJobRepositoryInstance(final DataSourceTransactionManager dataSourceTransactionManager) throws Exception
	{
		MapJobRepositoryFactoryBean result = new MapJobRepositoryFactoryBean();;
		result.setTransactionManager(dataSourceTransactionManager);
		return result.getObject();
	}
	
	/**
	 * clobType由Types clobType指定即可
	 * */
	public static JobRepository getJobRepositoryIntance(final DataSource dataSource, 
														final DataSourceTransactionManager dataSourceTransactionManager,
														final DatabaseType dbType,
														final int clobType) throws Exception
	{
		JobRepositoryFactoryBean result = new JobRepositoryFactoryBean();
		result.setDataSource(dataSource);
		result.setTransactionManager(dataSourceTransactionManager);
		result.setDatabaseType(dbType.name());
		result.setClobType(clobType);
		return result.getObject();
	}
}