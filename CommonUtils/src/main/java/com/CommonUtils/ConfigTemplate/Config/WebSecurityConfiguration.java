package com.CommonUtils.ConfigTemplate.Config;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.CommonUtils.SpringSecurity.Config.Encoder.BCryptPasswordEncoderConfig;
import com.CommonUtils.SpringSecurity.Config.Role.RoleHierarchyConfig;
import com.CommonUtils.Utils.HttpUtils.HttpUtil;
import com.CommonUtils.Utils.HttpUtils.Bean.SimpleResponse;
import com.CommonUtils.Utils.SecurityUtils.SpringSecurityUtil;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Order(1)
@EnableWebSecurity
@EnableGlobalMethodSecurity
(
		prePostEnabled = true, 									//解锁@PreAuthorize（方法执行前进行验证）与@PostAuthorize（方法执行后进行验证）注解
		securedEnabled = true, 									//解锁@Secured注解
		jsr250Enabled = true
)  //  启用方法级别的权限认证
@Slf4j
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter
{
	@Override
	public void configure(WebSecurity web)
	{
		web
			.ignoring()
			.antMatchers("/webjars/**", 
						 "/Js/**",
						 "/Css/**",
						 "/template/**",
						 "/Plugins/**");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception 
	{
		http
			.authorizeRequests()
				.withObjectPostProcessor
				(
						new ObjectPostProcessor<FilterSecurityInterceptor>()
						{
							@Override
							public <O extends FilterSecurityInterceptor> O postProcess(O object) 
							{
								//这里实现了动态配置权限
								//object.setSecurityMetadataSource(FilterInvocationSecurityMetadataSourceServiceImpl);
								//object.setAccessDecisionManager(AccessDecisionManagerServiceImpl);
								return object;
							}
						}
				)
				
			    //不需要拦截的资源
				.antMatchers("/homeRestController/register").permitAll()
				
				//其他资源请求都需要进行验证（这个不开启，是无法自动跳转到登录页面的）
				.anyRequest().authenticated()
			
		.and()
			
			//表单配置
			.formLogin()
				//登录页面，SpringSecurity默认值为/login
			    .loginPage("/homeController/login")
			    
			    //用于登录的Controller URL地址，SpringSecurity默认值为/login
			    .loginProcessingUrl("/homeController/login")
			    
			    //登录成功后，返回登录成功信息给页面
			    .successHandler
			    (
			    		(HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> 
			    		{
			    			HttpUtil.responseInfo
			    			(
			    					response, 
			    					new SimpleResponse()
			    						.setObject(authentication)
			    						.setStatus(HttpStatus.OK.value())
			    						.setStatusDesc("登录成功")
			    						.toJson(), 
			    					MediaType.APPLICATION_JSON_UTF8
			    			);
			    			
			    			UserDetails user = SpringSecurityUtil.getUser(authentication);
			    			String userName = user.getUsername();
			    			
			    			//如果使用了默认用户登录，需要输出警告日志，因为SpringSecurityUtil的getUser如果有问题，会返回一个默认用户，这个需要排查一下问题
			    			if ("defaultAdmin".equalsIgnoreCase(userName))
			    			{ log.warn("使用了默认用户登录？请排查一下问题出现在哪"); }
			    			
			    			//正常登录
			    			else {}
			    		}
			    )
			    
			    //设定获取登录表单用户名
			    .usernameParameter("username")
			    
			    //设定获取登录表单密码
			    .passwordParameter("password")
			    
			    //登录失败的处理器，返回错误信息给login页面
			    .failureHandler
			    (
			    		(request, response, exception) -> 
			    		{
			    			HttpUtil.responseInfo
			    			(
			    					response, 
			    					new SimpleResponse()
			    						.setObject(null)
			    						.setStatus(HttpStatus.UNAUTHORIZED.value())
			    						.setStatusDesc("找不到对应的用户，是用户名不正确？还是密码不正确？还是说。。。。没有注册？")
			    						.toJson(),
			    					MediaType.APPLICATION_PROBLEM_JSON_UTF8
			    			);
			    		}
			    ).permitAll()
				
		.and()
			.logout()
			
				//如果配置为/logout，则是使用SpringSecurity默认的注销行为
				.logoutUrl("/logout")
			
				//注销成功后重定向到登录页面
				.logoutSuccessUrl("/homeController/login")
				
				//是否将session置为无效
				.invalidateHttpSession(true)
				
				//是否清除授权信息
				.clearAuthentication(true).permitAll()
		
		//设定Seesion过期后，重定向登录页面，要求重新登录
		.and()
			.sessionManagement()
				//无效session跳转页
				.invalidSessionUrl("/homeController/login")
				
				/**
				 * 我们可以准确控制会话何时创建以及Spring Security如何与之交互：
				 * always - 如果一个会话尚不存在，将始终创建一个会话
				   ifRequired - 仅在需要时创建会话（默认）
				   never - 框架永远不会创建会话本身，但如果它已经存在，它将使用一个
				   stateless - Spring Security不会创建或使用任何会话
				   
				   
				           了解此配置仅控制Spring Security的功能非常重要 - 而不是整个应用程序。如果我们不指示Spring Security，可能无法创建会话，但我们的应用程序可能会！
				           默认情况下，Spring Security会在需要时创建会话 - 这是“ifRequired”。
                                                                   对于更无状态的应用程序，“never”选项将确保Spring Security本身不会创建任何会话;但是，如果应用程序创建了一个，那么Spring Security将使用它。
				          最后，最严格的会话创建选项 - “stateless” - 保证应用程序根本不会创建任何会话。
				         这是在Spring 3.1中引入的，它将有效地跳过部分Spring Security过滤器链。主要是会话相关的部分，如HttpSessionSecurityContextRepository，SessionManagementFilter，RequestCacheFilter。
				        这些更严格的控制机制直接暗示不使用cookie，所以每个请求都需要重新进行身份验证。这种无状态架构适用于REST API及其无状态约束。它们也适用于基本和摘要式身份验证等身份验证机制。
				 * 
				 * */
				.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
				
				//该框架通过配置在用户已有会话的情况但尝试再次进行身份验证时，提供了针对典型会话固定攻击的保护：
				/**
				 * 默认情况下，Spring Security启用了此保护（“migrateSession”） - 在身份验证时，会创建一个新的HTTP会话，旧的会话将失效，旧会话的属性将被复制。
				         如果这不是所需的行为，则可以使用其他两个选项：
                                                                 设置“none”时，原始会话不会失效
				         设置“newSession”时，将创建一个干净的会话，而不会复制旧会话中的任何属性
				 * 
				 * */
				.sessionFixation().migrateSession()
				
				//要为同一用户启用允许多个并发会话的方案
				.maximumSessions(1).and()
		
		//关闭跨域，这个不关闭，会出现登录成功后，无法跳转到主页面，暂时不清楚怎么解决，只能直接关闭这个功能
		.and()
			.csrf().disable();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception
	{
		auth
			.userDetailsService
			(
					username -> 
					{
						String encryptPassword = BCryptPasswordEncoderConfig.getInstance().encode("asd");
						
						//本来直接输入密码就可以了，不过由于不是用create bean模式注入，采用函数式编程，因此密码需要自己手工加密再传入
						return SpringSecurityUtil.getUser(username, encryptPassword, AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
					}
			);
		
		//如果定义好UserDetailsServiceImpl，可以直接用如下的代码
		//auth.userDetailsService(UserDetailsServiceImpl) 
	}
	
	@Bean
	public PasswordEncoder passwordEncoder()
	{ return BCryptPasswordEncoderConfig.getInstance(); }
	
	/**
	 * 描述角色继承（可选配置）
	 * 如果ROLE_dba是大boss，具有所有权限，ROLE_admin具有ROLE_user的权限，ROLE_user则是一个公共角色，即ROLE_admin继承ROLE_user、ROLE_dba继承ROLE_admin
	 * */
	@Bean
	public RoleHierarchy roleHierarchy() 
	{ return RoleHierarchyConfig.getInstance("ROLE_dba > ROLE_admin ROLE_admin > ROLE_user"); }
	
	/**
	 * 当已经过身份验证的用户尝试再次进行身份验证时，应用程序可以通过以下几种方式之一处理该事件。它可以使用户的活动会话无效，并使用新会话再次对用户进行身份验证，或者允许两个会话同时存在。
	 * 
	 * 启用并发会话控制支持的第一步是在web.xml中添加以下侦听器
	 * 这对于确保在销毁会话时通知Spring Security会话注册表是至关重要
	 * */
	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() 
	{ return new HttpSessionEventPublisher(); }
	
	/**
	 * 加入这个，避免启动的时候出现默认的安全密码
	 * */
	@Resource
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception
	{ auth.inMemoryAuthentication().withUser("defaultAdmin").password("!@#$%^&*()_+").roles("ADMIN"); }
}