package com.CommonUtils.Config.Security.SpringSecurity.Config.Encoder;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class BCryptPasswordEncoderConfig 
{
	private BCryptPasswordEncoderConfig() {}
	
	private static class SingletonContainer
	{ private static BCryptPasswordEncoder instance = new BCryptPasswordEncoder(6); }
	
	public static BCryptPasswordEncoder getInstance()
	{ return SingletonContainer.instance; } 
}