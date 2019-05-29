package com.CommonUtils.ConfigTemplate.Startup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;

@SpringBootApplication
@EnableAutoConfiguration(exclude={ DataSourceAutoConfiguration.class, ThymeleafAutoConfiguration.class })
@ComponentScan(basePackages= {"com.CommonUtils"})
@IntegrationComponentScan("com.CommonUtils")
@EnableIntegration
public class CommonUtilsApplication 
{
	public static void main(String[] args) 
	{ SpringApplication.run(CommonUtilsApplication.class, args); }
}