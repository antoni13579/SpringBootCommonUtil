package com.CommonUtils.Config.Security.Shiro.Config;

import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;

public final class AuthorizationAttributeSourceAdvisorConfig 
{
	private AuthorizationAttributeSourceAdvisorConfig() {}
	
	public static AuthorizationAttributeSourceAdvisor getInstance(final DefaultWebSecurityManager securityManager)
	{
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(securityManager);
		return advisor;
	}
}