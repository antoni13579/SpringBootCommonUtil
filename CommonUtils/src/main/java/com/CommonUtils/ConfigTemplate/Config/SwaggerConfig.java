package com.CommonUtils.ConfigTemplate.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig 
{
	@Bean
	public Docket docket()
	{
		return com.CommonUtils.Config.Swagger.SwaggerConfig.getDocket
		(
				"Swagger接口文档", 
				"Swagger接口文档", 
				"https://localhost:10086/swagger-ui.html", 
				"Antoni", 
				"364770752@qq.com", 
				"1.0.0", 
				DocumentationType.SWAGGER_2, 
				"com.CommonUtils.ConfigTemplate"
		);
	}
}