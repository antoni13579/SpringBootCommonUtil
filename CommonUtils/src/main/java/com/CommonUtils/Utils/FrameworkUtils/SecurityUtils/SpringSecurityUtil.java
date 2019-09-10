package com.CommonUtils.Utils.FrameworkUtils.SecurityUtils;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public final class SpringSecurityUtil 
{
	private SpringSecurityUtil() {}
	
	public static UserDetails getUser()
	{ return (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); }
	
	public static UserDetails getUser(final Authentication authentication)
	{ return (UserDetails)authentication.getPrincipal(); }
	
	public static UserDetails getUser(final String userName, final String password, final Collection<? extends GrantedAuthority> authorities)
	{ return new User(userName, password, authorities); }
}