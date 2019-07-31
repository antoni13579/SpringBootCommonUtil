package com.CommonUtils.Utils.SpringSecurityUtils;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public final class SpringSecurityUtil 
{
	private SpringSecurityUtil() {}
	
	public static Optional<UserDetails> getUser()
	{ return getUser(SecurityContextHolder.getContext()); }
	
	/**
	 * SpringSecurity获取当前登录用户，入参可以使用SecurityContextHolder.getContext()进行输入
	 * */
	public static Optional<UserDetails> getUser(final SecurityContext securityContext)
	{
		UserDetails user = null;
		
		if (null != securityContext)
		{
			Authentication authentication = securityContext.getAuthentication();
			if (null != authentication)
			{
				Object principal = authentication.getPrincipal();
				if (null != principal && principal instanceof UserDetails)
				{ user = (UserDetails)principal; }
			}
		}
		
		/*
		if (null == user)
		{ user = new User(SpringSecurityContants.DEFAULT_USER_NAME, SpringSecurityContants.DEFAULT_PASS_WORD, AuthorityUtils.commaSeparatedStringToAuthorityList(SpringSecurityContants.DEFAULT_ROLES)); }
		*/
		
		return Optional.ofNullable(user);
	}
	
	public static Optional<UserDetails> getUser(final Authentication authentication)
	{
		UserDetails user = null;
		
		if (null != authentication)
		{
			Object principal = authentication.getPrincipal();
			if (null != principal && principal instanceof UserDetails)
			{ user = (UserDetails)principal; }
		}
		
		/*
		if (null == user)
		{ user = new User(SpringSecurityContants.DEFAULT_USER_NAME, SpringSecurityContants.DEFAULT_PASS_WORD, AuthorityUtils.commaSeparatedStringToAuthorityList(SpringSecurityContants.DEFAULT_ROLES)); }
		*/
		
		return Optional.ofNullable(user);
	}
}