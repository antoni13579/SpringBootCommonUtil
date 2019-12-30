package com.CommonUtils.Config.IO.SpringBatch.Config.Core;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public final class JobLauncherConfig 
{
	private JobLauncherConfig() {}
	
	public static JobLauncher getInstance(final ThreadPoolTaskExecutor taskExecutor, 
										  final JobRepository jobRepository)
	{
		SimpleJobLauncher result = new SimpleJobLauncher();
		result.setJobRepository(jobRepository);
		result.setTaskExecutor(taskExecutor);
		return result;
	}
}