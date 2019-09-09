package com.CommonUtils.Config.SpringBatch.Config.Step;

import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

public final class StepBuilderFactoryConfig 
{
	private StepBuilderFactoryConfig() {}
	
	public static StepBuilderFactory getInstance(final JobRepository jobRepository, 
												 final DataSourceTransactionManager dataSourceTransactionManager)
	{ return new StepBuilderFactory(jobRepository, dataSourceTransactionManager); }
}