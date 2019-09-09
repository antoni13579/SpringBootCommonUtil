package com.CommonUtils.Config.SpringSecurity.Config.Encoder;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class BCryptPasswordEncoderConfig 
{
	private static BCryptPasswordEncoder INSTANCE = null;
	
	private BCryptPasswordEncoderConfig() {}
	
	public static PasswordEncoder getInstance()
	{
		if (null == INSTANCE)
		{
			synchronized (BCryptPasswordEncoderConfig.class)
			{
				if (null == INSTANCE)
				{ INSTANCE = new BCryptPasswordEncoder(6); }
			}
		}
		
		return INSTANCE;
	}
}