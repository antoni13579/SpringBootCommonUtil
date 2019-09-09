package com.CommonUtils.Config.SpringBatch.Config.Step;

import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.builder.StepBuilder;

public final class StepBuilderConfig 
{
	private StepBuilderConfig() {}
	
	public static StepBuilder getStepBuilder(final StepBuilderFactory stepBuilderFactory,
  		     								 final String stepBuilderName,
  		     								 final int startLimit)
	{
		return stepBuilderFactory
					.get(stepBuilderName)
					.allowStartIfComplete(true)
					//.repository(jobRepository)
					.startLimit(startLimit)
					//.transactionManager(transactionManager)
					;
	}
}