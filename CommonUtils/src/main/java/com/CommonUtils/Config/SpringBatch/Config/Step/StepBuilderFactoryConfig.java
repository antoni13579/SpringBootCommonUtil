package com.CommonUtils.Config.SpringBatch.Config.Step;

import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.PlatformTransactionManager;

public final class StepBuilderFactoryConfig 
{
	private StepBuilderFactoryConfig() {}
	
	public static StepBuilderFactory getInstance(final JobRepository jobRepository, 
												 final PlatformTransactionManager platformTransactionManager)
	{ return new StepBuilderFactory(jobRepository, platformTransactionManager); }
}