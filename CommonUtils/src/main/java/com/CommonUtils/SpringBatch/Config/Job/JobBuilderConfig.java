package com.CommonUtils.SpringBatch.Config.Job;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;

public final class JobBuilderConfig 
{
	private JobBuilderConfig() {}
	
	public static JobBuilder getJobBuilder(final JobBuilderFactory jobBuilderFactory,
										   final String jobBuilderName)
	{
		return jobBuilderFactory
					.get(jobBuilderName)
					//.incrementer(jobParametersIncrementer)
					//.listener(listener)
					//.preventRestart()
					//.repository(jobRepository)
					//.validator(jobParametersValidator)
					;
	}
}