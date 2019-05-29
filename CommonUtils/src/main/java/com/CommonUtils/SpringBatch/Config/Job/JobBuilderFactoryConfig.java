package com.CommonUtils.SpringBatch.Config.Job;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;

public final class JobBuilderFactoryConfig 
{
	private JobBuilderFactoryConfig() {}
	
	public static JobBuilderFactory getInstance(final JobRepository jobRepository)
	{ return new JobBuilderFactory(jobRepository); }
}