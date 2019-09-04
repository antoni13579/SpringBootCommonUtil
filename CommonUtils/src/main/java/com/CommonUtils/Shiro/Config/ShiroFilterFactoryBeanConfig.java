package com.CommonUtils.Shiro.Config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.Filter;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;

import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;

import lombok.Getter;
import lombok.ToString;

public final class ShiroFilterFactoryBeanConfig 
{
	private ShiroFilterFactoryBeanConfig() {}
	
	/**
	 * Filter Chain定义说明 <P/>
	 * 
	 * 1、一个URL可以配置多个Filter，使用逗号分隔<P/>
	 * 2、当设置多个过滤器时，全部验证通过，才视为通过<P/>
	 * 3、部分过滤器可指定参数，如perms，roles<P/>
	 * 4、如果涉及到thymeleaf，这里不能直接使用 ("/static/**", "anon")来配置匿名访问，必须配置到每个静态目录<P/>
	 *   filterChainDefinitionMap.put("/css/**", "anon");<P/>
	 *   filterChainDefinitionMap.put("/fonts/**", "anon");<P/>
	 *   filterChainDefinitionMap.put("/img/**", "anon");<P/>
	 *   filterChainDefinitionMap.put("/js/**", "anon");<P/>
	 *   filterChainDefinitionMap.put("/html/**", "anon");<P/>
	 *            
	 */
	public static ShiroFilterFactoryBean getInstance(final SecurityManager securityManager, 
													 final LinkedHashMap<String, InterceptorChain> paramFilterChainDefinitionMap,
													 final Optional<Map<String, Filter>> customFilter,
													 final String unauthorizedUrl,
													 final String loginUrl,
													 final Optional<String> successUrl)
	{
		// 拦截器
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
		// 配置链接需注意这个是顺序判断
		JavaCollectionsUtil.mapProcessor
		(
				paramFilterChainDefinitionMap, 
				(final String key, final InterceptorChain value, final int indx) -> 
				{ filterChainDefinitionMap.put(key, value.getInterceptorType()); }
		);
		
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		shiroFilterFactoryBean.setLoginUrl(loginUrl);
		shiroFilterFactoryBean.setUnauthorizedUrl(unauthorizedUrl);
		
		//添加自己的过滤器
		customFilter.ifPresent(consumer -> { shiroFilterFactoryBean.setFilters(consumer); });
		
		successUrl.ifPresent(consumer -> { shiroFilterFactoryBean.setSuccessUrl(consumer); });
		
		return shiroFilterFactoryBean;
	}
	
	@ToString
	@Getter
	public enum InterceptorChain 
	{
		/************认证过滤器********************/
		/**
		 * 例子/admins/**=anon 没有参数，表示可以匿名使用。 
		 * */
		ANON("anon"),
		
		/**
		 * 例如/admins/user/**=authcBasic没有参数表示httpBasic认证
		 * */
		AUTHC_BASIC("authcBasic"),
		
		/**
		 * 例如/admins/user/**=authc表示需要认证(登录)才能使用，没有参数  
		 * */
		AUTHC("authc"),
		
		/**
		 * 例如/admins/user/**=user没有参数表示必须存在用户，当登入操作时不做检查  
		 * */
		USER("user"),
		
		/************授权过滤器********************/
		/**
		 * 例子/admins/user/**=roles[admin],参数可以写多个，多个时必须加上引号，  并且参数之间用逗号分割，当有多个参数时，例如admins/user/**=roles["admin,guest"],  每个参数通过才算通过，相当于hasAllRoles()方法。  
		 * */
		ROLES("roles"),
		
		/**
		 * 例子/admins/user/**=perms[user:add:*],参数可以写多个，多个时必须加上引号，并且参数之间用逗号分割，  例如/admins/user/**=perms["user:add:*,user:modify:*"]，当有多个参数时必须每个参数都通过才通过，  想当于isPermitedAll()方法。
		 * */
		PERMS("perms"),
		
		/**
		 * 例子/admins/user/**=rest[user],根据请求的方法，相当于/admins/user/**=perms[user:method] ,  其中method为post，get，delete等。
		 * */
		REST("rest"),
		
		/**
		 * 例子/admins/user/**=port[8081],当请求的url的端口不是8081是跳转到schemal://serverName:8081?queryString,  其中schmal是协议http或https等，serverName是你访问的host,8081是url配置里port的端口，queryString是你访问的url里的？后面的参数。
		 * */
		PORT("port"),
		
		/**
		 * 例子/admins/user/**=ssl没有参数，表示安全的url请求，协议为https
		 * */
		SSL("ssl");
		
		private final String interceptorType;
		
		private InterceptorChain(final String interceptorType)
		{ this.interceptorType = interceptorType; }
	}
}