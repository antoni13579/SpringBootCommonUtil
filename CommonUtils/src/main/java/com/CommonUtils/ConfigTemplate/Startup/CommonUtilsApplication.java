package com.CommonUtils.ConfigTemplate.Startup;

import org.apache.shiro.spring.boot.autoconfigure.ShiroAnnotationProcessorAutoConfiguration;
import org.apache.shiro.spring.boot.autoconfigure.ShiroAutoConfiguration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;

@SpringBootApplication
@EnableAutoConfiguration
(
		exclude=
		{
				DataSourceAutoConfiguration.class, 
				DruidDataSourceAutoConfigure.class,
				
				ThymeleafAutoConfiguration.class,
				
				ShiroAutoConfiguration.class,
				ShiroAnnotationProcessorAutoConfiguration.class
		}
)
@ComponentScan(basePackages= {"com.CommonUtils"})
@IntegrationComponentScan("com.CommonUtils")
@EnableIntegration
public class CommonUtilsApplication 
{
	public static void main(String[] args) 
	{ SpringApplication.run(CommonUtilsApplication.class, args); }
}