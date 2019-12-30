package com.CommonUtils.ConfigTemplate.Config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.ResourceRegionHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.unit.DataSize;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.CommonUtils.Config.MiddleWare.Config.ServletWebServerFactoryConfig;
import com.CommonUtils.Config.MiddleWare.Config.GracefulShutdowns.GracefulShutdownTomcat;
import com.CommonUtils.Config.Web.Config.MultipartConfigElementConfig;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class WebConfiguration implements WebMvcConfigurer, WebApplicationInitializer
{
	//如果开启了https，这个就是https的端口，否则是http端口
	@Value("${server.port}")
	private int serverPort;
	
	//自定义设置的http端口，如果没有设置https，这个是不会生效
	@Value("${http_port}")
	private int httpPort;
	
	@Resource
	private ThreadPoolTaskExecutor commonThreadPool;
	
	/**文件上传配置，统一配置为2GB*/
	@Bean(name = "multipartConfigElement")
	public MultipartConfigElement multipartConfigElement()
	{ return MultipartConfigElementConfig.getInstance(FileUtil.getTmpDirPath(), DataSize.ofGigabytes(2), DataSize.ofGigabytes(2)); }
	
	/**优雅关闭Tomcat容器*/
	@Bean("gracefulShutdownTomcat")
	public TomcatConnectorCustomizer gracefulShutdownTomcat()
	{ return new GracefulShutdownTomcat(); }
	
	/**默认开启Tomcat个性化配置，包含防止连接重置，http连接跳转到https等功能*/
	@Bean(name = "tomcatServletWebServerFactory")
	public TomcatServletWebServerFactory tomcatServletWebServerFactory(@Qualifier("gracefulShutdownTomcat")TomcatConnectorCustomizer gracefulShutdownTomcat)
	{
		//开启https
		TomcatServletWebServerFactory result = ServletWebServerFactoryConfig.getTomcatInstance(this.httpPort, this.serverPort);
		result.addConnectorCustomizers(gracefulShutdownTomcat);
		return result;
	}
	
	@Bean
	public FilterRegistrationBean<ShowRequestUrlFilter> showRequestUrlFilter()
	{
		FilterRegistrationBean<ShowRequestUrlFilter> registration = new FilterRegistrationBean<ShowRequestUrlFilter>();
		registration.setFilter(new ShowRequestUrlFilter());
		registration.addUrlPatterns("/*");
        registration.addInitParameter("paramName", "paramValue");
        registration.setName("ShowRequestUrlFilter");
        registration.setOrder(1);
        return registration;
	}
	
	@Bean
	public FilterRegistrationBean<SessionFilter> sessionFilter()
	{
		FilterRegistrationBean<SessionFilter> registration = new FilterRegistrationBean<SessionFilter>();
		registration.setFilter(new SessionFilter());
		registration.addUrlPatterns("/*");
        registration.addInitParameter("paramName", "paramValue");
        registration.setName("SessionFilter");
        registration.setOrder(2);
        return registration;
	}
	
	@Bean
	public CorsFilter corsFilter() 
	{
		final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		final CorsConfiguration corsConfiguration = new CorsConfiguration();
		/* 是否允许请求带有验证信息 */
		corsConfiguration.setAllowCredentials(true);
		/* 允许访问的客户端域名 */
		corsConfiguration.addAllowedOrigin("*");
		/* 允许服务端访问的客户端请求头 */
		corsConfiguration.addAllowedHeader("*");
		/* 允许访问的方法名,GET POST等 */
		corsConfiguration.addAllowedMethod("*");
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(urlBasedCorsConfigurationSource);
	}
	
	/**
	  *  解决转码问题Spring @responseBody 问题，设置下载断点续传，
	 * */
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters)
	{
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
		converters.add(stringHttpMessageConverter);
		
		//据说加入这个可以实现断点续传功能
		converters.add(new ResourceHttpMessageConverter());
		converters.add(new ResourceRegionHttpMessageConverter()); 
	}
	
	/**
	 * 未使用Spring Security的情况下，开启跨域支持
	 * */
	/*
	@Override
	public void addCorsMappings(CorsRegistry registry)
	{
		registry
			//所有方法都处理跨域请求
			.addMapping("/**")
			
			.allowedOrigins("*")
			
			//允许通过的请求数
			.allowedMethods("GET", "POST", "OPTIONS", "PUT")
			
			//表示允许的请求头
			.allowedHeaders("Content-Type", "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers","accessToken")
			
			.exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
			
			.allowCredentials(true)
			
			.maxAge(3600);
	}
	*/
	
	@Override
	public void addFormatters(FormatterRegistry registry)
	{
		//加入字符串自动转换为时间的功能，主要是表单提交了时间，后台用bean接收，如果没有这个转换，会报异常		
		registry.addConverter(String.class, Date.class, (String source) -> { return DateUtil.parse(source, DatePattern.NORM_DATETIME_PATTERN); });
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry)
	{ registry.addInterceptor(new ShowRequestUrlInterceptor()).addPathPatterns("/**").order(1); }
	
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) 
	{
		//registry.jsp(); // 启用ViewResolver
	}
	
	//这里可以设置线程池配置异步
	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) 
	{ configurer.setTaskExecutor(this.commonThreadPool); }
	
	/**
	 * 接下来，我们将讨论如何保护会话cookie。
                       我们可以使用httpOnly和secure标签来保护我们的会话cookie：
	   httpOnly：如果为true，那么浏览器脚本将无法访问cookie
	   secure：如果为true，则cookie将仅通过HTTPS连接发送

	        从Java servlet 3开始，此配置选项可用。默认情况下，http-only为true且secure为false。

	      如果我们使用Spring Boot，我们可以在application.properties中设置这些标志：
	  server.servlet.session.cookie.http-only=true
	  server.servlet.session.cookie.secure=true
	 * */
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException 
	{
		servletContext.getSessionCookieConfig().setHttpOnly(true);        
		servletContext.getSessionCookieConfig().setSecure(false);
		
		//因为webflux是响应式调用，所以最后需要配置servlet.setAsyncSupported(true)。如果不使用该特性可以不需要这句话。
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        //context.register(BeanConfig.class);
        context.setServletContext(servletContext);
 
        ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcher", new DispatcherServlet(context));
 
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/");
        servlet.setAsyncSupported(true);
	}
	
	/**
	 * 访问根路径默认跳转 index.html页面 （简化部署方案： 可以把前端打包直接放到项目的 webapp，上面的配置）
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) 
	{
		//registry.addViewController("/").setViewName("doc.html");
		//registry.addViewController("/").setViewName("index.html");
	}
	
	/**
	 * 静态资源的配置 - 使得可以从磁盘中读取 Html、图片、视频、音频等
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) 
	{
		/*
		registry.addResourceHandler("/**")
		.addResourceLocations("file:" + upLoadPath + "//", "file:" + webAppPath + "//")
		.addResourceLocations(staticLocations.split(","));
		*/
	}
	
	public final class ShowRequestUrlFilter implements Filter
	{
		@Override
		public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException 
		{
			if (servletRequest instanceof HttpServletRequest)
			{
				log.info("这里是Filter拦截下来的请求，请求的URL地址为 : {}", ((HttpServletRequest) servletRequest).getRequestURI());
				filterChain.doFilter(servletRequest, servletResponse);
			}
			else
			{ throw new ServletException("request请求并不是HttpServletRequest，请排查原因"); }
		}
		
		@Override
		public void init(FilterConfig filterConfig) throws ServletException {}
		
		@Override
		public void destroy() {}
	}
	
	/**
	 * 我们还可以使用Filter手动实现如何保护会话cookie
	 * 
	 * */
	public final class SessionFilter implements Filter
	{
		@Override
		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException 
		{
			HttpServletRequest req = (HttpServletRequest) request;
	        HttpServletResponse res = (HttpServletResponse) response;
	        Cookie[] allCookies = req.getCookies();
	        if (allCookies != null) 
	        {
	            Cookie session = Arrays.stream(allCookies).filter(x -> x.getName().equals("JSESSIONID")).findFirst().orElse(null);
	 
	            if (session != null) 
	            {
	                session.setHttpOnly(true);
	                session.setSecure(false);
	                res.addCookie(session);
	            }
	        }
	        chain.doFilter(req, res);
		}
		
		@Override
		public void init(FilterConfig filterConfig) throws ServletException {}
		
		@Override
		public void destroy() {}
	}
	
	public final class ShowRequestUrlInterceptor implements AsyncHandlerInterceptor
	{
		//执行操作前的步骤
		@Override
		public boolean preHandle(final HttpServletRequest request, 
								 final HttpServletResponse response, 
								 final Object handler)
		{
			log.info("这里是Interceptor拦截下来的请求，请求的URL地址为 : {}", request.getRequestURI());
			return true;
		}
		
		@Override
		public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final ModelAndView modelAndView) throws Exception {}
		
		@Override
		public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final Exception ex) throws Exception {}
		
		@Override
		public void afterConcurrentHandlingStarted(final HttpServletRequest request, final HttpServletResponse response,final Object handler) throws Exception {}
	}
}