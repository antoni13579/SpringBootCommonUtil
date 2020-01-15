package com.CommonUtils.Config.Swagger.Config;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * 
 * getDocket需要用create bean动作
 * 
 * 配置类需要加上@Configuration与@EnableSwagger2
 * 
 * 
 * swagger 通过注解表明该接口会生成文档，包括接口名、请求方法、参数、返回信息的等等。
@Api：修饰整个类，描述Controller的作用
@ApiOperation：描述一个类的一个方法，或者说一个接口
@ApiParam：单个参数描述
@ApiModel：用对象来接收参数
@ApiProperty：用对象接收参数时，描述对象的一个字段
@ApiResponse：HTTP响应其中1个描述
@ApiResponses：HTTP响应整体描述
@ApiIgnore：使用该注解忽略这个API
@ApiError ：发生错误返回的信息
@ApiImplicitParam 一个参数
@ApiImplicitParams 多个参数
 * */
public final class SwaggerConfig 
{
	private SwaggerConfig() {}
	/**
	 * 如果不清楚，DocumentationType就用默认值DocumentationType.SWAGGER_2
	 * */
	public static Docket getDocket(final String title, 
			   					   final String description, 
			   					   final String url,
			   					   final String name,
			   					   final String email,
			   					   final String version,
			   					   final DocumentationType documentationType,
			   					   final String basePackage)
	{
		return new Docket(documentationType)
				.apiInfo(apiInfo(title, description, url, name, email, version))
				.select()
				.apis(RequestHandlerSelectors.basePackage(basePackage))
				.build();
	}
	
	private static ApiInfo apiInfo(final String title, 
								   final String description, 
								   final String url,
								   final String name,
								   final String email,
								   final String version)
	{
		return new ApiInfoBuilder()
				.title(title)
				.description(description)
				.termsOfServiceUrl(url)
				.contact(new Contact(name, url, email))
				.version(version)
				.build();
	}
}